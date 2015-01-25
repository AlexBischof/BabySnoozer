package babysnoozer.tinkerforge;

import babysnoozer.EventBus;
import babysnoozer.events.DisplayEvent;
import babysnoozer.events.LogEvent;
import babysnoozer.handlers.DisplayHandler;
import babysnoozer.handlers.LogHandler;
import babysnoozer.handlers.SnoozingBabyConfig;
import babysnoozer.listeners.RotiListener;
import com.tinkerforge.*;

import java.io.IOException;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class TinkerforgeSystem {

  private static final TinkerforgeSystem singleton = new TinkerforgeSystem();

  private IPConnection ipconnection;
  private BrickServo servo;
  private BrickletSegmentDisplay4x7 display4x7;
  private BrickletRotaryEncoder roti;

  private TinkerforgeSystem() {
  }

  public static TinkerforgeSystem instance() {
	return singleton;
  }

  public void initBricks() throws Exception {
	EventBus.instance().fire(new LogEvent("Initbricks"));

	//TODO automatische Erkennung

	ipconnection = new IPConnection();
	servo = new BrickServo("6xhbGJ", ipconnection);
	display4x7 = new BrickletSegmentDisplay4x7("pPJ", ipconnection);

	initRoti();

    configServo(servo);

	ipconnection.connect("localhost", 4223);

  }

  public void registerHandlers() {

	//TODO repitiv
	EventBus.instance().registerHandler(new LogHandler());
	EventBus.instance().registerHandler(new DisplayHandler(TinkerforgeSystem.instance().getDisplay4x7()));
  }

  private void initRoti() {
	roti = new BrickletRotaryEncoder("kGs", ipconnection);

	RotiListener rotiListener = new RotiListener();
	roti.addPressedListener(rotiListener);
	roti.addReleasedListener(rotiListener);

    roti.addCountListener(new BrickletRotaryEncoder.CountListener() {
      @Override public void count(int count) {
        //TODO Mapping von Drehstellung auf Display??
        EventBus.instance().fire(new DisplayEvent(String.format("%04d", count)));
        SnoozingBabyConfig.instance().setRuntimeInMinutes(count);
      }
    });
  }

  private void configServo(BrickServo servo) throws TimeoutException, NotConnectedException,
		  InterruptedException {

	servo.setOutputVoltage(7200);

	servo.setDegree((short) 0, (short) -10000, (short) 10000);
	servo.setPulseWidth((short) 0, 1000, 2000);
	servo.setPeriod((short) 0, 19500);
	servo.setAcceleration((short) 0, 0xFFFF);
	servo.setVelocity((short) 0, 0xAA);

  }

  public IPConnection getIpconnection() {
	return ipconnection;
  }

  public BrickServo getServo() {
	return servo;
  }

  public BrickletSegmentDisplay4x7 getDisplay4x7() {
	return display4x7;
  }

  public BrickletRotaryEncoder getRoti() {
	return roti;
  }
}
