package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.SetServoPosEvent;
import babysnoozer.events.ShutdownEvent;
import babysnoozer.tinkerforge.TinkerforgeSystem;
import com.tinkerforge.BrickServo;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class ServoHandler implements EventHandler {

  private final static short SERVO_NUMBER = 0;

  @Override public void handle(Event event) {

	BrickServo servo = TinkerforgeSystem.instance().getServo();

	Class<? extends Event> eventClass = event.getClass();

	try {
	  if (eventClass.equals(SetServoPosEvent.class)) {
		handleSetServoPosEvent((SetServoPosEvent) event, servo);
	  } else if (eventClass.equals(ShutdownEvent.class)) {
		handleShutdownEvent(servo);
	  }
	} catch (Exception e) {e.printStackTrace();}

  }

  private void handleShutdownEvent(BrickServo servo) throws TimeoutException, NotConnectedException {
	short position = servo.getPosition(SERVO_NUMBER);
	handleSetServoPosEvent(new SetServoPosEvent(position), servo);
  }

  private void handleSetServoPosEvent(SetServoPosEvent event, BrickServo servo)
		  throws TimeoutException, NotConnectedException {
	SetServoPosEvent setServoPosEvent = (SetServoPosEvent) event;
	servo.setPosition(SERVO_NUMBER, (short) setServoPosEvent.getPos());
	servo.enable(SERVO_NUMBER);
  }
}
