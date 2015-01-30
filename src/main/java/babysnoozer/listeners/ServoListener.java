package babysnoozer.listeners;

import babysnoozer.events.AkkuEmptyEvent;
import babysnoozer.events.ServoPositionReachedEvent;
import com.tinkerforge.BrickServo;

import static babysnoozer.EventBus.EventBus;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class ServoListener implements BrickServo.UnderVoltageListener, BrickServo.PositionReachedListener {

  @Override public void underVoltage(int voltage) {
	EventBus.post(new AkkuEmptyEvent(voltage));
  }

  @Override public void positionReached(short servoNum, short position) {
	EventBus.post(new ServoPositionReachedEvent());
  }
}
