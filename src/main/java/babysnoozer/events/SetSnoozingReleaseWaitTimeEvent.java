package babysnoozer.events;

public class SetSnoozingReleaseWaitTimeEvent {
  private final long releaseWaitTime;

  public SetSnoozingReleaseWaitTimeEvent(long releaseWaitTime) {
	this.releaseWaitTime = releaseWaitTime;
  }

  public long getReleaseWaitTime() {
	return releaseWaitTime;
  }
}

