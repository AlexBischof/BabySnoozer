package babysnoozer.events;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SetSnoozingEndPosEvent {
  private final int endPos;

  public SetSnoozingEndPosEvent(int endPos) {
	this.endPos = endPos;
  }

  public int getEndPos() {
	return endPos;
  }
}

