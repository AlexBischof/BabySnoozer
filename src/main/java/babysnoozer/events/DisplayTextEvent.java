package babysnoozer.events;

import babysnoozer.Event;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class DisplayTextEvent implements Event {
  private final String text;
  private boolean inactive;
  private long created;

  public DisplayTextEvent(String text) {
	this.text = text;
	this.created = System.currentTimeMillis();
  }

  public String getText() {
	return text;
  }

  public DisplayTextEvent inactive() {
	this.inactive = true;
	return this;
  }

  public boolean isInactive() {
	return inactive;
  }

  public long getCreated() {
	return created;
  }
}
