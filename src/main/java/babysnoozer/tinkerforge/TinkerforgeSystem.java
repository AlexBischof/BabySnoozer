package babysnoozer.tinkerforge;

import babysnoozer.events.LogEvent;
import babysnoozer.events.RotiCountEvent;
import babysnoozer.listeners.RotiListener;
import com.tinkerforge.*;

import static babysnoozer.EventBus.EventBus;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public enum TinkerforgeSystem {

  TinkerforgeSystem;

  private IPConnection ipconnection;
  private BrickServoWrapper servoWrapper;
  private BrickletSegmentDisplay4x7 display4x7;
  private BrickletRotaryEncoder roti;

  public void initBricks() throws Exception {
	EventBus.post(new LogEvent("Initbricks"));

	//TODO automatische Erkennung

	ipconnection = new IPConnection();
	servoWrapper = new BrickServoWrapper(ipconnection, (short) 0);
	display4x7 = new BrickletSegmentDisplay4x7("pPJ", ipconnection);

	ipconnection.connect("localhost", 4223);

	initRoti();

	servoWrapper.configServo();
  }

  private void initRoti() throws TimeoutException, NotConnectedException {
	roti = new BrickletRotaryEncoder("kGs", ipconnection);

	roti.setCountCallbackPeriod(100l);

	RotiListener rotiListener = new RotiListener();
	roti.addPressedListener(rotiListener);
	roti.addReleasedListener(rotiListener);

	roti.addCountListener(count -> EventBus.post(new RotiCountEvent(count)));
  }

  public IPConnection getIpconnection() {
	return ipconnection;
  }

  public BrickServoWrapper getServo() {
	return servoWrapper;
  }

  public BrickletSegmentDisplay4x7 getDisplay4x7() {
	return display4x7;
  }

  public BrickletRotaryEncoder getRoti() {
	return roti;
  }

}
