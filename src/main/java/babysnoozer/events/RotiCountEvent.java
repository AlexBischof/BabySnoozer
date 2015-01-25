package babysnoozer.events;

import babysnoozer.Event;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class RotiCountEvent implements Event {
  private final int count;

  public RotiCountEvent(int count) {
	this.count = count;
  }

  public int getCount() {
	return count;
  }
}
