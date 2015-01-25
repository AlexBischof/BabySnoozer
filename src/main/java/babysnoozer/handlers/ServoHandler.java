package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.SetServoPosEvent;
import babysnoozer.tinkerforge.TinkerforgeSystem;
import com.tinkerforge.BrickServo;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class ServoHandler implements EventHandler {

  private short servoNumber = 0;

  @Override public void handle(Event event) {

	if (event.getClass().equals(SetServoPosEvent.class)) {
	  BrickServo servo = TinkerforgeSystem.instance().getServo();

	  SetServoPosEvent setServoPosEvent = (SetServoPosEvent) event;

	  try {
		servo.setPosition(servoNumber, (short) setServoPosEvent.getPos());
		servo.enable(servoNumber);
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	}

  }
}
