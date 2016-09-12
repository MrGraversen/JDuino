public class CommunicationEvent extends BaseEvent
{
	private final String message;
	private final long timestamp;
	private final long sequenceNumber;

	public CommunicationEvent(String message, long sequenceNumber)
	{
		this.message = message;
		this.timestamp = System.currentTimeMillis();
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public String getMessage()
	{
		return message;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public long getSequenceNumber()
	{
		return sequenceNumber;
	}

	public String toString()
	{
		return "[Seq=" + sequenceNumber + "] " + message;
	}
}