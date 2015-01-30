package babysnoozer.events;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SetSnoozingStartPosEvent {
  private final short startPos;

  public SetSnoozingStartPosEvent(short startPos) {
	this.startPos = startPos;
  }

  public short getStartPos() {
	return startPos;
  }
}

