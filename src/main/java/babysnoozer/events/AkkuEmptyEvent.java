package babysnoozer.events;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class AkkuEmptyEvent {

  private final int voltage;

  public AkkuEmptyEvent(int voltage) {
	this.voltage = voltage;
  }

  public int getVoltage() {
	return voltage;
  }
}
