package babysnoozer.handlers;

import babysnoozer.events.SetServoPosEvent;
import babysnoozer.events.ShutdownEvent;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.BrickServo;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class ServoHandler {

  //TODO refac servo
  private final static short SERVO_NUMBER = 0;

  //TODO Shutdowns nicht asynchron
  @Subscribe
  public void handleShutdownEvent(ShutdownEvent shutdownEvent) throws TimeoutException, NotConnectedException {
	BrickServo servo = TinkerforgeSystem.getServo();
	short position = servo.getPosition(SERVO_NUMBER);
	handleSetServoPosEvent(new SetServoPosEvent(position));
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleSetServoPosEvent(SetServoPosEvent setServoPosEvent)
		  throws TimeoutException, NotConnectedException {

	BrickServo servo = TinkerforgeSystem.getServo();
	servo.setPosition(SERVO_NUMBER, (short) setServoPosEvent.getPos());
	servo.enable(SERVO_NUMBER);
  }
}
