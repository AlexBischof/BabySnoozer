package babysnoozer.events;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class SetServoPosEvent {
  private final short pos;

  public SetServoPosEvent(short pos) {
	this.pos = pos;
  }

  public int getPos() {
	return pos;
  }
}
