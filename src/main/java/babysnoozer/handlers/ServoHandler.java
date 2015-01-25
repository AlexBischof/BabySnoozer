package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.SetServoPosEvent;
import babysnoozer.tinkerforge.TinkerforgeSystem;
import com.tinkerforge.BrickServo;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class ServoHandler implements EventHandler {

  @Override public void handle(Event event) {
	if (event.getClass().equals(SetServoPosEvent.class)) {
	  BrickServo servo = TinkerforgeSystem.instance().getServo();

	  try {
		servo.setPosition((short) 0, (short) 0);
		servo.enable((short) 0);
	  } catch (Exception e) {
	    e.printStackTrace();;
	  }
	}
  }
}
