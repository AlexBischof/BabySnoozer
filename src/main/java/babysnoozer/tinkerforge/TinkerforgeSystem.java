package babysnoozer.tinkerforge;

import babysnoozer.EventBus;
import babysnoozer.events.DisplayEvent;
import babysnoozer.events.LogEvent;
import babysnoozer.events.RotiCountEvent;
import babysnoozer.handlers.AnlernHandler;
import babysnoozer.handlers.DisplayHandler;
import babysnoozer.handlers.LogHandler;
import babysnoozer.handlers.SnoozingBabyConfig;
import babysnoozer.listeners.RotiListener;
import com.tinkerforge.*;

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

	//TODO
	//assert false : "Muss Connectetd sein";

    //TODO Auslagerung properties

	servo.setOutputVoltage(7200);

	servo.setDegree((short) 0, (short) -900, (short) 900);
	servo.setPulseWidth((short) 0, 960, 2040);
	servo.setPeriod((short) 0, 19500);
	servo.setAcceleration((short) 0, 2000);
	servo.setVelocity((short) 0, 200);

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
