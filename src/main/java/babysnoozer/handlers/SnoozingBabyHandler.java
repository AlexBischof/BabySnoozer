package babysnoozer.handlers;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.events.*;
import babysnoozer.handlers.commands.*;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.handlers.SnoozingBabyStateMachine.State;
import static babysnoozer.tinkerforge.BrickStepperWrapper.*;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

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
  public void handleSetSnoozingDrawWaitTimeEvent(SetSnoozingDrawWaitTimeEvent setSnoozingDrawWaitTimeEvent) {
    SnoozingBabyStateMachine.setDrawWaitTime(setSnoozingDrawWaitTimeEvent.getDrawWaitTime());
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleSetSnoozingReleaseWaitTimeEvent(SetSnoozingReleaseWaitTimeEvent setSnoozingReleaseWaitTimeEvent) {
    SnoozingBabyStateMachine.setReleaseWaitTime(setSnoozingReleaseWaitTimeEvent.getReleaseWaitTime());
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleRotiPressEvent(RotiPressEvent rotiPressEvent) {
    if (SnoozingBabyStateMachine.getState().equals(State.SetCycleCount)) {
      if (rotiPressEvent.getPressedLengthInMs() > 2000)
      {
        // button pressed for more than 2s -> learn event triggered
        // reset state and jump to learn routine
        SnoozingBabyStateMachine.setState(State.Null);
        EventBus.post(new LearnEvent(
                                     SnoozingBabyStateMachine.getStartPos(),
                                     SnoozingBabyStateMachine.getEndPos(),
                                     SnoozingBabyStateMachine.getReleaseWaitTime(),
                                     SnoozingBabyStateMachine.getDrawWaitTime()));
      }
      else {
        SnoozingBabyStateMachine.setState(State.Snooze);
        EventBus.post(new SnoozingStartEvent());

        // Saves values to cycleconfig.properties
        try {

          // TODO Filenotfoundexception
          // Files.ex
          PropertiesLoader propertiesLoader = new PropertiesLoader("cycleconfig.properties", false);
          Properties properties = propertiesLoader.load();
          properties.setProperty("startPos", String.valueOf(SnoozingBabyStateMachine.getStartPos()));
          properties.setProperty("endPos", String.valueOf(SnoozingBabyStateMachine.getEndPos()));
          properties.setProperty("wait_after_draw", String.valueOf(SnoozingBabyStateMachine.getDrawWaitTime()));
          properties.setProperty("wait_after_release", String.valueOf(SnoozingBabyStateMachine.getReleaseWaitTime()));

          propertiesLoader.store(properties);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleRotiCountEvent(RotiCountEvent rotiCountEvent) {
    // TODO verlängerung
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleInitSnoozingStateEvent(InitSnoozingStateEvent initSnoozingStateEvent)
      throws TimeoutException, NotConnectedException {
    SnoozingBabyStateMachine.setState(State.SetCycleCount);

    // TODO weil auch zwischendrin gedreht werden kann
    BrickletRotaryEncoder roti = TinkerforgeSystem.getRoti();
    roti.getCount(/* reset */true);

    EventBus.post(new DisplayTextEvent(String.valueOf(SnoozingBabyStateMachine.getCycleCount())));
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleSnoozingStartEvent(SnoozingStartEvent snoozingStartEvent) {
    EventBus.post(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.FULL.getValue()));

    /*
     * Creates CommandQueue
     */
    CycleQueue cycles = new CycleCreator()
                                          .create(new CycleCreationParam(
                                                                         SnoozingBabyStateMachine.getCycleCount(),
                                                                         SnoozingBabyStateMachine.getDrawWaitTime(),
                                                                         SnoozingBabyStateMachine.getReleaseWaitTime(),
                                                                         SnoozingBabyStateMachine.getStartPos(),
                                                                         SnoozingBabyStateMachine.getEndPos(),
                                                                         Velocity.draw,
                                                                         Acceleration.acc_draw,
                                                                         Acceleration.deacc_draw,
                                                                         Velocity.release,
                                                                         Acceleration.acc_release,
                                                                         Acceleration.deacc_release));
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
  public void handleStepperPositionReachedEvent(StepperPositionReachedEvent positionReachedEvent)
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
