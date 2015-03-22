package babysnoozer.handlers;

import babysnoozer.events.*;
import babysnoozer.handlers.commands.*;
import babysnoozer.tinkerforge.BrickServoWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.util.NoSuchElementException;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.handlers.SnoozingBabyStateMachine.State;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozingBabyHandler {

  private CommandExecutor commandExecutor;

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
  public void handleRotiPressEvent(RotiPressEvent rotiPressEvent) {
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
  public void handleInitSnoozingStateEvent(InitSnoozingStateEvent initSnoozingStateEvent)
		  throws TimeoutException, NotConnectedException {
	SnoozingBabyStateMachine.setState(State.SetCycleCount);

	//TODO weil auch zwischendrin gedreht werden kann
	BrickletRotaryEncoder roti = TinkerforgeSystem.getRoti();
	roti.getCount(/*reset*/ true);

	EventBus.post(new DisplayTextEvent(String.valueOf(SnoozingBabyStateMachine.getCycleCount())));
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleSnoozingStartEvent(SnoozingStartEvent snoozingStartEvent) {
	EventBus.post(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.FULL.getValue()));

    /*
     * Creates CommandQueue
     */
	int cycleCount = SnoozingBabyStateMachine.getCycleCount();

	//TODO velocities and acceleration into properties
	CycleQueue cycles = new CycleCreator()
			.create(new CycleCreationParam(cycleCount, 1000l, SnoozingBabyStateMachine.getStartPos(),
			                               SnoozingBabyStateMachine.getEndPos(), BrickServoWrapper.Velocity.lvl2,
			                               BrickServoWrapper.Acceleration.lvl2, BrickServoWrapper.Velocity.lvl2,
			                               BrickServoWrapper.Acceleration.lvl2));
	SnoozingBabyStateMachine.setCycles(cycles);

	System.out.println(cycles);

	fireNextCommand();
  }

  private void fireNextCommand() {
	CycleQueue cycles = SnoozingBabyStateMachine.getCycles();

	try {
	  Command command = cycles.nextCommand();

	  System.out.println("Executing " + command);
	  getCommandExecutor().execute(command);

	  EventBus.post(new DisplayTextEvent(String.valueOf(cycles.size())));

	} catch (NoSuchElementException e) {
	  EventBus.post(new DisplayTextEvent("0"));
	  EventBus.post(new ShutdownEvent());
	}
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleServoPositionReachedEvent(ServoPositionReachedEvent positionReachedEvent)
		  throws TimeoutException, NotConnectedException {
	if (!SnoozingBabyStateMachine.getState().equals(State.Snooze))
	  // do only run that function is target state was already set by handleSnoozingStartEvent
	  return;

	fireNextCommand();
  }

  public CommandExecutor getCommandExecutor() {
	if (commandExecutor == null) {
	  commandExecutor = new CommandExecutor();
	}
	return commandExecutor;
  }
}
