package babysnoozer.events;

import babysnoozer.Event;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SetSnoozingStartPosEvent implements Event {
  private final short startPos;

  public SetSnoozingStartPosEvent(short startPos) {
	this.startPos = startPos;
  }

  public short getStartPos() {
	return startPos;
  }
}

