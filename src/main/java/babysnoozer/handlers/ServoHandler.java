package babysnoozer.handlers;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.events.ServoPositionReachedEvent;
import babysnoozer.events.SetServoPosEvent;
import babysnoozer.events.ShutdownEvent;
import babysnoozer.tinkerforge.BrickServoWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.tinkerforge.BrickServoWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickServoWrapper.Velocity;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class ServoHandler {

  @Subscribe
  public void handleShutdownEvent(ShutdownEvent shutdownEvent) throws TimeoutException, NotConnectedException {
	TinkerforgeSystem.getServo().disable();

	Properties properties = null;
	try {
	  PropertiesLoader propertiesLoader = new PropertiesLoader("initialpositionrecall.properties", false);
	  properties = propertiesLoader.load();
	  properties.setProperty("lastPosition", String.valueOf(TinkerforgeSystem.getServo().getCurrentPosition()));
	  propertiesLoader.store(properties);
	} catch (IOException e) {
	  e.printStackTrace();
	}

	System.out.println("Ende");

	System.exit(0);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleSetServoPosEvent(SetServoPosEvent setServoPosEvent)
		  throws TimeoutException, NotConnectedException {

	BrickServoWrapper servo = TinkerforgeSystem.getServo();

    /*
     * problem: if current position equal to desired position no positionreachedevent is fired...damn
     */
	short pos = setServoPosEvent.getPos();
	if (servo.getCurrentPosition() == pos) {
	  EventBus.post(new ServoPositionReachedEvent());
	} else {
	  servo.setPosition(pos);
	  servo.setAcceleration(setServoPosEvent.getAcceleration());
	  servo.setVelocity(setServoPosEvent.getVelocity());
	  servo.enable();
	}
  }
}
