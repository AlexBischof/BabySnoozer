package babysnoozer.handlers.commands;

/**
 * Created by Alexander Bischof on 10.03.15.
 */
public class WaitCommand implements Command {
  private final long waitInMillis;

  public WaitCommand(long waitInMillis) {
	this.waitInMillis = waitInMillis;
  }

  public long getWaitInMillis() {
	return waitInMillis;
  }

  @Override public String toString() {
	return "WaitCommand{" +
	       "waitInMillis=" + waitInMillis +
	       '}';
  }
}
