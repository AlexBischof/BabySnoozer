package babysnoozer.listeners;

import babysnoozer.EventBus;
import babysnoozer.events.AkkuEmptyEvent;
import babysnoozer.events.ServoPositionReachedEvent;
import com.tinkerforge.BrickServo;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class ServoListener implements BrickServo.UnderVoltageListener, BrickServo.PositionReachedListener {

  @Override public void underVoltage(int voltage) {
	EventBus.instance().fire(new AkkuEmptyEvent());
  }

  @Override public void positionReached(short servoNum, short position) {
	EventBus.instance().fire(new ServoPositionReachedEvent());
  }
}
