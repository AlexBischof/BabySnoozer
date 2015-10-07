package babysnoozer.events;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SetSnoozingEndPosEvent {
  private final long endPos;

  public SetSnoozingEndPosEvent(long endPos) {
	this.endPos = endPos;
  }

  public long getEndPos() {
	return endPos;
  }
}

