package babysnoozer.events;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SetSnoozingEndPosEvent {
  private final short endPos;

  public SetSnoozingEndPosEvent(short endPos) {
	this.endPos = endPos;
  }

  public short getEndPos() {
	return endPos;
  }
}

