package babysnoozer.handlers;

import babysnoozer.events.SetServoPosEvent;
import babysnoozer.events.ShutdownEvent;
import babysnoozer.tinkerforge.BrickServoWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class ServoHandler {

  @Subscribe
  public void handleShutdownEvent(ShutdownEvent shutdownEvent) throws TimeoutException, NotConnectedException {
	handleSetServoPosEvent(new SetServoPosEvent(TinkerforgeSystem.getServo().getCurrentPosition()));
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleSetServoPosEvent(SetServoPosEvent setServoPosEvent)
		  throws TimeoutException, NotConnectedException {

	BrickServoWrapper servo = TinkerforgeSystem.getServo();
	servo.setPosition(setServoPosEvent.getPos());
	servo.enable();
  }
}
