package babysnoozer.events;

import babysnoozer.Event;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SetSnoozingEndPosEvent implements Event {
  private final short endPos;

  public SetSnoozingEndPosEvent(short endPos) {
	this.endPos = endPos;
  }

  public short getEndPos() {
	return endPos;
  }
}

