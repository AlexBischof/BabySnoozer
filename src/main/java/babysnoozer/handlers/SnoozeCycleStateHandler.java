package babysnoozer.handlers;

import babysnoozer.events.DisplayTextEvent;
import babysnoozer.events.SetServoPosEvent;
import babysnoozer.events.SnoozingStartEvent;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozeCycleStateMachine.SnoozeCycleStateMachine;
import static babysnoozer.handlers.SnoozeCycleStateMachine.State;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozeCycleStateHandler {

  @Subscribe
  @AllowConcurrentEvents
  public void handleSnoozingStartEvent(SnoozingStartEvent snoozingStartEvent) {
	SnoozeCycleStateMachine.setCyclesLeft(SnoozingBabyStateMachine.getCycleCount());
	SnoozeCycleStateMachine.setTargetState(State.Draw);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleServoPositionReachedEvent() {
	boolean isDraw = SnoozeCycleStateMachine.getTargetState().equals(State.Draw);

	//changes targetState
	short targetPos = 0;
	if (isDraw) {

	  Short learn_velocity = Short.valueOf(TinkerforgeSystem.getServoConfigProperties()
	                                                        .getProperty("snooze_velocity", "50"));
	  //TODO refac exception
	  try {
		TinkerforgeSystem.getServo().setVelocity((short) 0, learn_velocity);
	  } catch (TimeoutException e) {
		e.printStackTrace();
	  } catch (NotConnectedException e) {
		e.printStackTrace();
	  }

	  SnoozeCycleStateMachine.setTargetState(State.Release);
	  targetPos = SnoozingBabyStateMachine.getStartPos();
	} else {
	  SnoozeCycleStateMachine.setTargetState(State.Draw);
	  SnoozeCycleStateMachine.decreaseCyclesLeft();
	  targetPos = SnoozingBabyStateMachine.getEndPos();
	}

	int cyclesLeft = SnoozeCycleStateMachine.getCyclesLeft();
	if (cyclesLeft > 0) {
	  EventBus.post(new SetServoPosEvent(targetPos));
	  EventBus.post(new DisplayTextEvent(String.valueOf(cyclesLeft)));
	}
  }
}
