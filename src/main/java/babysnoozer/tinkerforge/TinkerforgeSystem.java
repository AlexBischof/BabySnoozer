package babysnoozer.tinkerforge;

import babysnoozer.EventBus;
import babysnoozer.config.PropertiesLoader;
import babysnoozer.events.LogEvent;
import babysnoozer.events.RotiCountEvent;
import babysnoozer.handlers.AnlernHandler;
import babysnoozer.handlers.DisplayHandler;
import babysnoozer.handlers.LogHandler;
import babysnoozer.handlers.SnoozingBabyConfig;
import babysnoozer.listeners.RotiListener;
import babysnoozer.listeners.ServoListener;
import com.tinkerforge.*;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class TinkerforgeSystem {

  private static final TinkerforgeSystem singleton = new TinkerforgeSystem();

  private Properties servoConfigProperties;

  private IPConnection ipconnection;
  private BrickServo servo;
  private BrickletSegmentDisplay4x7 display4x7;
  private BrickletRotaryEncoder roti;

  private TinkerforgeSystem() {

	try {
	  servoConfigProperties = new PropertiesLoader("servo.properties").load();
	} catch (IOException e) {
	  e.printStackTrace();
	}

  }

  public static TinkerforgeSystem instance() {
	return singleton;
  }

  public void initBricks() throws Exception {
	EventBus.instance().fire(new LogEvent("Initbricks"));

	//TODO automatische Erkennung

	ipconnection = new IPConnection();
	servo = new BrickServo("62Bpyf", ipconnection);
	display4x7 = new BrickletSegmentDisplay4x7("pPJ", ipconnection);

	ipconnection.connect("localhost", 4223);

	initRoti();

	configServo(servo);
  }

  public void registerHandlers() {

	//TODO repitiv
	EventBus.instance().registerHandler(new LogHandler());
	EventBus.instance().registerHandler(new DisplayHandler(TinkerforgeSystem.instance().getDisplay4x7()));
	EventBus.instance().registerHandler(new AnlernHandler());
  }

  private void initRoti() throws TimeoutException, NotConnectedException {
	roti = new BrickletRotaryEncoder("kGs", ipconnection);

	roti.setCountCallbackPeriod(100l);

	RotiListener rotiListener = new RotiListener();
	roti.addPressedListener(rotiListener);
	roti.addReleasedListener(rotiListener);

	roti.addCountListener(new BrickletRotaryEncoder.CountListener() {
	  @Override public void count(int count) {
		//TODO Mapping von Drehstellung auf Display??

		EventBus.instance().fire(new RotiCountEvent(count));
		SnoozingBabyConfig.instance().setRuntimeInMinutes(count);
	  }
	});
  }

  private void configServo(BrickServo servo) throws TimeoutException, NotConnectedException,
		  InterruptedException {

    //Sets start properties
	servo.setOutputVoltage(Integer.valueOf(servoConfigProperties.getProperty("outputVoltage", "7200")));

	servo.setDegree((short) 0, Short.valueOf(servoConfigProperties.getProperty("degreeStart", "-900")),
	                Short.valueOf(servoConfigProperties.getProperty("degreeEnd", "900")));
	servo.setPulseWidth((short) 0, Short.valueOf(servoConfigProperties.getProperty("pulseWidthStart", "960")),
	                    Short.valueOf(servoConfigProperties.getProperty("pulseWidthEnd", "2040")));
	servo.setPeriod((short) 0, Integer.valueOf(servoConfigProperties.getProperty("period", "19500")));
	servo.setAcceleration((short) 0, Integer.valueOf(servoConfigProperties.getProperty("acceleration", "2000")));
	servo.setVelocity((short) 0, Integer.valueOf(servoConfigProperties.getProperty("velocity", "200")));

    //Sets servolistener
    ServoListener servoListener = new ServoListener();
    servo.addUnderVoltageListener(servoListener);
    servo.addPositionReachedListener(servoListener);
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
