package babysnoozer.events;

public class SetSnoozingDrawWaitTimeEvent {
  private final long drawWaitTime;

  public SetSnoozingDrawWaitTimeEvent(long drawWaitTime) {
	this.drawWaitTime = drawWaitTime;
  }

  public long getDrawWaitTime() {
	return drawWaitTime;
  }
}

