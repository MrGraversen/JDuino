import java.io.PrintStream;

public abstract class BaseEvent implements ISerialEvent
{
	public abstract String getMessage();

	public void print(PrintStream printStream)
	{
		printStream.print(getMessage());
	}

	public void print()
	{
		System.out.println(getMessage());
	}
}