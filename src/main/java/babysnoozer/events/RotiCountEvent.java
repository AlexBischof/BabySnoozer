package babysnoozer.events;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class RotiCountEvent {
  private final int count;

  public RotiCountEvent(int count) {
	this.count = count;
  }

  public int getCount() {
	return count;
  }
}
