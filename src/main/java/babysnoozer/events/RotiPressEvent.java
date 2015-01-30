package babysnoozer.events;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class RotiPressEvent {
  private final long pressedLengthInMs;

  public RotiPressEvent(long pressed, long released) {
	this.pressedLengthInMs = released - pressed;
  }

  public long getPressedLengthInMs() {
	return pressedLengthInMs;
  }
}
