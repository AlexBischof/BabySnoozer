package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventBus;
import babysnoozer.events.*;
import babysnoozer.tinkerforge.TinkerforgeSystem;
import com.tinkerforge.BrickletRotaryEncoder;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozingBabyStateMachine {

  public enum State {
	Null, SetCycleCount, Snooze, ShutDown
  }

  private static final SnoozingBabyStateMachine singleton = new SnoozingBabyStateMachine();

  private State state = State.Null;

  private short startPos;
  private short endPos;
  private int cycleCount = 1;

  private SnoozingBabyStateMachine() {
  }

  public static SnoozingBabyStateMachine instance() {
	return singleton;
  }

  public void next(Event event) throws TimeoutException, NotConnectedException {

	Class<? extends Event> aClass = event.getClass();
	if (aClass.equals(SnoozingStartEvent.class)) {

	  EventBus.instance().fire(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.LOW.getValue()));
	  EventBus.instance().fire(new SetServoPosEvent(this.endPos));

	} else if (aClass.equals(InitSnoozingStateEvent.class)) {
	  this.state = State.SetCycleCount;

	  //TODO weil auch zwischendrin gedreht werden kann
	  BrickletRotaryEncoder roti = TinkerforgeSystem.instance().getRoti();
	  roti.getCount(/*reset*/ true);

	  EventBus.instance().fire(new DisplayTextEvent(String.valueOf(cycleCount)));
	}

	boolean acceptRotiCountEvents =
			aClass.equals(RotiCountEvent.class) && this.state.equals(State.SetCycleCount);
	if (acceptRotiCountEvents) {
	  RotiCountEvent rotiCountEvent = (RotiCountEvent) event;
	  int count = rotiCountEvent.getCount();

	  if (count > 0 && count <= 10) {
		this.cycleCount = count;
		EventBus.instance()
		        .fire(new DisplayTextEvent(String.valueOf(rotiCountEvent.getCount())));
	  }
	}

	boolean acceptRotiPressEvents = aClass.equals(RotiPressEvent.class) && this.state.equals(State.SetCycleCount);
	if (acceptRotiPressEvents) {
	  this.state = State.Snooze;

	  EventBus.instance().fire(new SnoozingStartEvent());
	}

	if (aClass.equals(SetSnoozingStartPosEvent.class)) {
	  SetSnoozingStartPosEvent setSnoozingStartPosEvent = (SetSnoozingStartPosEvent) event;
	  this.startPos = setSnoozingStartPosEvent.getStartPos();
	} else if (aClass.equals(SetSnoozingEndPosEvent.class)) {
	  SetSnoozingEndPosEvent setSnoozingEndPosEvent = (SetSnoozingEndPosEvent) event;
	  this.endPos = setSnoozingEndPosEvent.getEndPos();
	}

    /*
	if (state.equals(State.Snooze)) {
	  //TODO howTo next?
	  //state = State.Running;

	  Short snooze_velocity = Short.valueOf(TinkerforgeSystem.instance().getServoConfigProperties()
	                                                         .getProperty("snooze_velocity", "200"));
	  //TODO refac exception
	  try {
		TinkerforgeSystem.instance().getServo().setVelocity((short) 0, snooze_velocity);
	  } catch (TimeoutException e) {
		e.printStackTrace();
	  } catch (NotConnectedException e) {
		e.printStackTrace();
	  }

	  EventBus.instance().fire(new SnoozingStartEvent());
	}*/
  }

  public short getStartPos() {
	return startPos;
  }

  public short getEndPos() {
	return endPos;
  }

  public State getState() {
	return state;
  }

  public int getCycleCount() {
	return cycleCount;
  }
}
