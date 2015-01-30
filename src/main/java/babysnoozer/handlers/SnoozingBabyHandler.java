package babysnoozer.handlers;

import babysnoozer.events.*;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.handlers.SnoozingBabyStateMachine.State;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozingBabyHandler {

  @Subscribe
  @AllowConcurrentEvents
  public void handleSetSnoozingStartPosEvent(SetSnoozingStartPosEvent setSnoozingStartPosEvent) {
	SnoozingBabyStateMachine.setStartPos(setSnoozingStartPosEvent.getStartPos());
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleSetSnoozingEndPosEvent(SetSnoozingEndPosEvent setSnoozingEndPosEvent) {
	SnoozingBabyStateMachine.setEndPos(setSnoozingEndPosEvent.getEndPos());
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleRotiPressEvent() {
	if (SnoozingBabyStateMachine.getState().equals(State.SetCycleCount)) {
	  SnoozingBabyStateMachine.setState(State.Snooze);
	  EventBus.post(new SnoozingStartEvent());
	}
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleRotiCountEvent(RotiCountEvent rotiCountEvent) {
	if (SnoozingBabyStateMachine.getState().equals(State.SetCycleCount)) {
	  int count = rotiCountEvent.getCount();

	  if (count > 0 && count <= 10) {
		SnoozingBabyStateMachine.setCycleCount(count);
		EventBus.post(new DisplayTextEvent(String.valueOf(rotiCountEvent.getCount())));
	  }
	}
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleInitSnoozingStateEvent() throws TimeoutException, NotConnectedException {
	SnoozingBabyStateMachine.setState(State.SetCycleCount);

	//TODO weil auch zwischendrin gedreht werden kann
	BrickletRotaryEncoder roti = TinkerforgeSystem.getRoti();
	roti.getCount(/*reset*/ true);

	EventBus.post(new DisplayTextEvent(String.valueOf(SnoozingBabyStateMachine.getCycleCount())));
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleSnoozingStartEvent() {
	EventBus.post(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.LOW.getValue()));
	EventBus.post(new SetServoPosEvent(SnoozingBabyStateMachine.getEndPos()));
  }
}
