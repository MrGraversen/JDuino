import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class SerialLink implements SerialPortEventListener
{
	private BufferedReader bufferedReader;
	private InputStreamReader inputStreamReader;
	private InputStream inputStream;
	private OutputStream outputStream;
	private SerialPort serialPort;

	private Enumeration<CommPortIdentifier> portEnumeration;
	private ArrayList<String> portList;

	private LinkedList<String> serialMessages;
	private int latestPollIndex;
	private Exception latestException;

	public SerialLink()
	{
		portEnumeration = CommPortIdentifier.getPortIdentifiers();
		portList = new ArrayList<String>();
		serialMessages = new LinkedList<>();
		latestPollIndex = 0;
	}

	public ArrayList<String> getPortList()
	{
		if (portList.size() > 0) return portList;

		CommPortIdentifier portId;
		while (portEnumeration.hasMoreElements())
		{
			portId = portEnumeration.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				portList.add(portId.getName());
			}
		}

		return portList;
	}

	public boolean connect(String commPort, int baudRate) throws SerialException
	{
		CommPortIdentifier portIdentifier;

		try
		{
			portIdentifier = CommPortIdentifier.getPortIdentifier(commPort);

			if (portIdentifier.isCurrentlyOwned())
			{
				throw new SerialException("The requested port ('" + commPort + "') is currently in use");
			}
			else
			{
				try
				{
					serialPort = (SerialPort) portIdentifier.open("JDuino", 2000);
					serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					serialPort.addEventListener(this);
					serialPort.notifyOnDataAvailable(true);

					inputStream = serialPort.getInputStream();
					inputStreamReader = new InputStreamReader(inputStream);
					bufferedReader = new BufferedReader(inputStreamReader);
					outputStream = serialPort.getOutputStream();

					return true;
				}
				catch (TooManyListenersException e)
				{
					throw new SerialException("The requested port ('" + commPort + "') has too many listeners");
				}
			}
		}
		catch (NoSuchPortException e)
		{
			throw new SerialException("The requested port ('" + commPort + "') does not exist");
		}
		catch (PortInUseException e)
		{
			throw new SerialException("The requested port ('" + commPort + "') is currently in use");
		}
		catch (UnsupportedCommOperationException e)
		{
			throw new SerialException("Unsupported communication operation");
		}
		catch (IOException e)
		{
			throw new SerialException("An I/O error occured: " + e.getMessage());
		}
	}

	public void disconnect()
	{
		try
		{
			outputStream.close();
			bufferedReader.close();
			serialPort.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private boolean hasNewSerialMessages()
	{
		return latestPollIndex < serialMessages.size();
	}

	private String getLatestSerialMessage()
	{
		synchronized (serialMessages)
		{
			return serialMessages.getLast();
		}
	}

	public boolean peek()
	{
		return hasNewSerialMessages();
	}

	public String poll() throws SerialException
	{
		if (latestException != null)
		{
			Exception tempException = latestException;
			latestException = null;
			throw new SerialException(tempException.getMessage());
		}

		latestPollIndex = serialMessages.size();
		return getLatestSerialMessage();
	}

	public String poll(int timeout) throws SerialException
	{
		if (latestException != null)
		{
			Exception tempException = latestException;
			latestException = null;
			throw new SerialException(tempException.getMessage());
		}

		int duration = 0;
		int n = serialMessages.size();

		while (serialMessages.size() == n)
		{
			if (duration > timeout) return null;

			try
			{
				Thread.sleep(10);
				duration += 10;
			}
			catch (InterruptedException e)
			{
				return null;
			}
		}

		return getLatestSerialMessage();
	}

	public void send(String data, boolean lineFeed) throws SerialException
	{
		if (lineFeed) data = data + "\n";

		try
		{
			outputStream.write(data.getBytes());
			outputStream.flush();
		}
		catch (IOException e)
		{
			throw new SerialException("Could not send data: " + e.getMessage());
		}
	}

	@Override
	public void serialEvent(SerialPortEvent ev)
	{
		int eventType = ev.getEventType();

		try
		{
			if (eventType == SerialPortEvent.DATA_AVAILABLE)
			{
				if (bufferedReader.ready())
				{
					String inputLine = bufferedReader.readLine();
					serialMessages.add(inputLine);
				}
			}
		}
		catch (IOException e)
		{
			latestException = e;
		}
	}
}