package babysnoozer.handlers;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.events.*;
import babysnoozer.handlers.commands.*;
import babysnoozer.tinkerforge.BrickServoWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.handlers.SnoozingBabyStateMachine.State;
import static babysnoozer.tinkerforge.BrickServoWrapper.*;
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

	  //Saves values to cycleconfig.properties
	  Properties properties = null;
	  try {

		//TODO Filenotfoundexception
		//Files.ex
		PropertiesLoader propertiesLoader = new PropertiesLoader("cycleconfig.properties", false);
		properties = propertiesLoader.load();
		properties.setProperty("startPos", String.valueOf(SnoozingBabyStateMachine.getStartPos()));
		properties.setProperty("endPos", String.valueOf(SnoozingBabyStateMachine.getEndPos()));
		properties.setProperty("cycleCount", String.valueOf(SnoozingBabyStateMachine.getCycleCount()));

		propertiesLoader.store(properties);
	  } catch (IOException e) {
		e.printStackTrace();
	  }
	}
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleRotiCountEvent(RotiCountEvent rotiCountEvent) {
	//TODO verlängerung
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
	Velocity releaseVelocity = Velocity.lvl3;
	Velocity drawVelocity = Velocity.lvl4;

	CycleQueue cycles = new CycleCreator()
			.create(new CycleCreationParam(cycleCount, 100l, 111000l, SnoozingBabyStateMachine.getStartPos(),
			                               SnoozingBabyStateMachine.getEndPos(), drawVelocity,
			                               Acceleration.lvl2, releaseVelocity,
			                               Acceleration.lvl2));
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
