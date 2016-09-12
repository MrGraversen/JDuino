public class FailureEvent extends BaseEvent
{
	private final SerialException exception;

	public FailureEvent(SerialException exception)
	{
		this.exception = exception;
	}

	public SerialException getError()
	{
		return exception;
	}

	public String getMessage()
	{
		return "[JDuino] ERROR: " + exception.getMessage();
	}

	public String toString()
	{
		return getMessage();
	}
}