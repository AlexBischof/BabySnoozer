package babysnoozer.listeners;

import babysnoozer.events.RotiPressEvent;
import com.tinkerforge.BrickletRotaryEncoder;

import static babysnoozer.EventBus.EventBus;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class RotiListener implements BrickletRotaryEncoder.PressedListener, BrickletRotaryEncoder.ReleasedListener {

  private long timePressed;

  @Override public void pressed() {
	timePressed = System.currentTimeMillis();

	//TODO probiere ohne ReleaseEvent den Statuswechsel hinzubekommen
  }

  @Override public void released() {
	EventBus.post(new RotiPressEvent(timePressed, System.currentTimeMillis()));
  }
}
