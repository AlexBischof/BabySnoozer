package babysnoozer.events;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class LogEvent {
  private final String text;

  public LogEvent(String text) {
	this.text = text;
  }

  public String getText() {
	return text;
  }

}
