package babysnoozer.events;

import babysnoozer.Event;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class DisplayTextEvent implements Event {
  private final String text;

  public DisplayTextEvent(String text) {
	this.text = text;
  }

  public String getText() {
	return text;
  }
}
