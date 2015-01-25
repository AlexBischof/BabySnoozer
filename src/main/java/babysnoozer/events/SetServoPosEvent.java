package babysnoozer.events;

import babysnoozer.Event;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class SetServoPosEvent implements Event {
  private final int pos;

  public SetServoPosEvent(int pos) {
	this.pos = pos;
  }

  public int getPos() {
	return pos;
  }
}
