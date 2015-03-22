package babysnoozer.tinkerforge;

import babysnoozer.events.RotiCountEvent;
import babysnoozer.listeners.RotiListener;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import static babysnoozer.EventBus.EventBus;

/**
 * Created by Alexander Bischof on 22.03.15.
 */
public class BrickRotaryWrapper {
  BrickletRotaryEncoder brickletRotaryEncoder;

  public void initBrick(IPConnection ipconnection) throws TimeoutException, NotConnectedException {
	brickletRotaryEncoder = new BrickletRotaryEncoder("kGs", ipconnection);

	brickletRotaryEncoder.setCountCallbackPeriod(100l);

	RotiListener rotiListener = new RotiListener();
	brickletRotaryEncoder.addPressedListener(rotiListener);
	brickletRotaryEncoder.addReleasedListener(rotiListener);

	brickletRotaryEncoder.addCountListener(count -> EventBus.post(new RotiCountEvent(count)));
  }
}
