package babysnoozer.handlers;

import babysnoozer.EventBus;
import babysnoozer.events.LogEvent;
import babysnoozer.events.SnoozingStartEvent;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozingBabyStateMachine {

  public enum State {
	Null, Learning, ReadyToStart, Running, ShutDown
  }

  private static final SnoozingBabyStateMachine singleton = new SnoozingBabyStateMachine();

  private State state = State.Null;

  private SnoozingBabyStateMachine() {
  }

  public static SnoozingBabyStateMachine instance() {
	return singleton;
  }

  public void start() {
	if (state.equals(State.ReadyToStart)) {
	  //TODO howTo start?
	  state = State.Running;
	  EventBus.instance().fire(new SnoozingStartEvent());
	} else {
	  EventBus.instance().fire(new LogEvent("System not angelernt"));
	}
  }
}
