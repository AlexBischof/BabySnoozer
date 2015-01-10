package babysnoozer.events;

import babysnoozer.Event;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class LogEvent implements Event {
  private final String text;

  public LogEvent(String text) {
	this.text = text;
  }

  public String getText() {
	return text;
  }

}
