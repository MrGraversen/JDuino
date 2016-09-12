public class JDuinoExample
{
	public JDuinoExample()
	{
		ISerialObserver serialObserver = new ISerialObserver()
		{
			@Override
			public void onSerialCommunication(ISerialEvent serialEvent)
			{
				serialEvent.print();
			}

			@Override
			public void onDisconnect(ISerialEvent serialEvent)
			{
				serialEvent.print();
			}

			@Override
			public void onConnectSuccess(ISerialEvent serialEvent)
			{
				serialEvent.print();
			}

			@Override
			public void onConnectError(ISerialEvent serialEvent)
			{
				serialEvent.print();
			}

			@Override
			public void onCommunicationError(ISerialEvent serialEvent)
			{
				serialEvent.print();
			}
		};

		SerialInterface serialInterface = new SerialInterface(9600);
		serialInterface.addObserver(serialObserver);

		serialInterface.connect();
		serialInterface.setAppendLineFeed(true);

		if (serialInterface.isConnected())
		{
			serialInterface.send("Hello from a desktop PC!");
			serialInterface.send("Hello from a desktop PC!");
			serialInterface.send("Hello from a desktop PC!");
			serialInterface.send("Hello from a desktop PC!");
			serialInterface.send("Hello from a desktop PC!");

			sleep(1000);

			serialInterface.disconnect();
		}
	}

	public static void main(String[] args)
	{
		new JDuinoExample();
	}

	private void sleep(int n)
	{
		try
		{
			Thread.sleep(n);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}