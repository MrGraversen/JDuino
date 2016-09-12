import java.util.ArrayList;
import java.util.List;

public class SerialInterface
{
	private final SerialLink serialLink;
	private List<ISerialObserver> serialObservers;

	private final String wantedCommPort;
	private final int baudRate;

	private boolean connected;

	private long sequence;
	private String port;

	private boolean appendLineFeed;

	private ListenerThread listenerThread;

	public SerialInterface()
	{
		serialLink = new SerialLink();
		serialObservers = new ArrayList<>();
		wantedCommPort = "";
		baudRate = 9600;
		connected = false;
		sequence = -1;
		appendLineFeed = false;
		listenerThread = new ListenerThread(0);
	}

	public SerialInterface(int baudRate)
	{
		serialLink = new SerialLink();
		serialObservers = new ArrayList<>();
		wantedCommPort = "";
		this.baudRate = baudRate;
		connected = false;
		sequence = -1;
		appendLineFeed = false;
		listenerThread = new ListenerThread(0);
	}

	public SerialInterface(String commPort)
	{
		serialLink = new SerialLink();
		serialObservers = new ArrayList<>();
		wantedCommPort = commPort;
		baudRate = 9600;
		connected = false;
		sequence = -1;
		appendLineFeed = false;
		listenerThread = new ListenerThread(0);
	}

	public SerialInterface(int baudRate, String commPort)
	{
		serialLink = new SerialLink();
		serialObservers = new ArrayList<>();
		wantedCommPort = commPort;
		this.baudRate = baudRate;
		connected = false;
		sequence = -1;
		appendLineFeed = false;
		listenerThread = new ListenerThread(0);
	}

	public void setSkipCount(int count)
	{
		listenerThread.setSkip(count);
	}

	public void setAppendLineFeed(boolean append)
	{
		appendLineFeed = append;
	}

	public void addObserver(ISerialObserver serialObserver)
	{
		serialObservers.add(serialObserver);
	}

	public void removeObserver(ISerialObserver serialObserver)
	{
		serialObservers.remove(serialObserver);
	}
	
	public boolean isConnected()
	{
		return connected;
	}

	public void connect()
	{
		boolean hasReportedErrors = false;
		
		try
		{
			if (connected) throw new SerialException("Connection already established");

			if (wantedCommPort.isEmpty())
			{
				try
				{
					connected = serialLink.connect(port = serialLink.getPortList().get(0), baudRate);
				}
				catch (IndexOutOfBoundsException e)
				{
					ISerialEvent event = new FailureEvent(new SerialException("No active serial communication ports. Did you connect the device?"));
					for (ISerialObserver so : serialObservers)
					{
						so.onConnectError(event);
					}
					hasReportedErrors = true;
				}
			}
			else
			{
				connected = serialLink.connect(port = wantedCommPort, baudRate);
			}

			if (connected)
			{
				listenerThread.start();
				
				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				ISerialEvent event = new CommunicationEvent("[JDuino] Connection succesful on port " + port + ".", getSequenceNumber());
				for (ISerialObserver so : serialObservers)
				{
					so.onConnectError(event);
				}
			}
			else if(!connected && !hasReportedErrors)
			{
				ISerialEvent event = new FailureEvent(new SerialException("Could not connect to port " + port));
				for (ISerialObserver so : serialObservers)
				{
					so.onConnectError(event);
				}
			}
		}
		catch (SerialException e)
		{
			ISerialEvent event = new FailureEvent(e);
			for (ISerialObserver so : serialObservers)
			{
				so.onConnectError(event);
			}
		}
	}

	public void disconnect()
	{
		listenerThread.halt();
		serialLink.disconnect();
		connected = false;

		ISerialEvent event = new CommunicationEvent("[JDuino] Gracefully disconnected from port " + port + ".", getSequenceNumber());
		for (ISerialObserver so : serialObservers)
		{
			so.onSerialCommunication(event);
		}
	}

	public void send(String message)
	{
		try
		{
			serialLink.send(message, appendLineFeed);
		}
		catch (SerialException e)
		{
			ISerialEvent event = new FailureEvent(e);
			for (ISerialObserver so : serialObservers)
			{
				so.onConnectError(event);
			}
		}
	}

	private long getSequenceNumber()
	{
		long temp = sequence;
		sequence = sequence + 1;
		return temp;
	}

	private class ListenerThread extends Thread
	{
		private final int THROTTLE_VALUE;
		private boolean stop;
		private int skipCount;
		private boolean skip;

		public ListenerThread(int skipCount)
		{
			THROTTLE_VALUE = 10;
			stop = false;
			this.skipCount = skipCount;
		}

		public void setSkip(int skipCount)
		{
			if (!isAlive()) this.skipCount = skipCount;
		}

		public void run()
		{
			while (!stop)
			{
				try
				{
					if (serialLink.peek())
					{
						skip = skipCount > sequence;

						String input = serialLink.poll();
						ISerialEvent event = new CommunicationEvent(input, getSequenceNumber());

						if (!skip)
						{
							for (ISerialObserver so : serialObservers)
							{
								so.onSerialCommunication(event);
							}
						}

						skip = false;
					}
				}
				catch (SerialException se)
				{
					ISerialEvent event = new FailureEvent(se);
					for (ISerialObserver so : serialObservers)
					{
						so.onSerialCommunication(event);
					}
				}

				try
				{
					sleep(THROTTLE_VALUE);
				}
				catch (InterruptedException ie)
				{
					stop = true;
				}
			}
		}

		public void halt()
		{
			stop = true;
			interrupt();
		}
	}
}