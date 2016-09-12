
public interface ISerialObserver
{
	public void onSerialCommunication(ISerialEvent serialEvent);

	public void onCommunicationError(ISerialEvent serialEvent);

	public void onConnectSuccess(ISerialEvent serialEvent);

	public void onConnectError(ISerialEvent serialEvent);

	public void onDisconnect(ISerialEvent serialEvent);
}
