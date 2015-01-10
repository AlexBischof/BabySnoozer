package babysnoozer.listeners;

import com.tinkerforge.BrickletRotaryEncoder;
import babysnoozer.EventBus;
import babysnoozer.events.RotiPressEvent;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class RotiListener implements BrickletRotaryEncoder.PressedListener, BrickletRotaryEncoder.ReleasedListener{

  private long timePressed;

  @Override public void pressed() {
    timePressed = System.currentTimeMillis();
  }

  @Override public void released() {
    EventBus.instance().fire(new RotiPressEvent(timePressed, System.currentTimeMillis()));
  }
}
