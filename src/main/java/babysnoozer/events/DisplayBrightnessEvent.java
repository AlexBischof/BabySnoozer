package babysnoozer.events;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class DisplayBrightnessEvent {

  public enum Brightness {
	LOW, FULL;

	public short getValue() {
	  switch (this) {
	  case LOW:
		return 0;
	  case FULL:
		return 7;
	  }
	  throw new RuntimeException("BADBABDBA");
	}
  }

  private final short brightness;

  public DisplayBrightnessEvent(short brightness) {
	this.brightness = brightness;
  }

  public short getBrightness() {
	return brightness;
  }
}
