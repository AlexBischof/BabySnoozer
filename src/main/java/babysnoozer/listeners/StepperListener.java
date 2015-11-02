package babysnoozer.listeners;

import babysnoozer.events.AkkuEmptyEvent;
import babysnoozer.events.StepperPositionReachedEvent;
import babysnoozer.events.StepperDisableEvent;
import com.tinkerforge.BrickStepper;

import static babysnoozer.EventBus.EventBus;


public class StepperListener implements BrickStepper.UnderVoltageListener, BrickStepper.PositionReachedListener {

  @Override public void underVoltage(int voltage) {
	EventBus.post(new AkkuEmptyEvent(voltage));
  }

  @Override public void positionReached(int position) {
	EventBus.post(new StepperPositionReachedEvent());
  }
}
