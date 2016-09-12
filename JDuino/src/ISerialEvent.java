import java.io.PrintStream;

public interface ISerialEvent
{
	public abstract String getMessage();

	public void print();

	public void print(PrintStream printStream);
}
