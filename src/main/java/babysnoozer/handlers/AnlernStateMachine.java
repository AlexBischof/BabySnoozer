package babysnoozer.handlers;

import babysnoozer.events.*;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.util.Arrays;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class AnlernStateMachine {

  public static final int ANLERN_TRIGGER_TIME_IN_MS = 1000;

  public enum State {
	Null, Init, StartPos, EndPos;
  }

  private State state = State.Null;

  @Subscribe
  @AllowConcurrentEvents
  public void handleRotiPressEvent(RotiPressEvent rotiPressEvent) throws TimeoutException, NotConnectedException {

	if (isDisabled()) {
	  return;
	}

	if (state.equals(State.Null)) {
	  handleRotiPressEventForNullState(rotiPressEvent);
	} else if (Arrays.asList(State.StartPos, State.EndPos).contains(state)) {
	  handleRotiPressEventForStartAndEndPos();
	}
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleRotiCountEvent(RotiCountEvent rotiCountEvent) {

	if (isDisabled()) {
	  return;
	}

	boolean acceptRotiCountEvent = Arrays.asList(State.StartPos, State.EndPos).contains(state);
	if (acceptRotiCountEvent) {

	  int count = rotiCountEvent.getCount();

	  //TODO binden an properties
	  int zaehlerwert = fireCount(count);

	  EventBus.post(new SetServoPosEvent((short) zaehlerwert));
	}
  }

  private void handleRotiPressEventForStartAndEndPos()
		  throws TimeoutException, NotConnectedException {
	if (state.equals(State.StartPos)) {
	  this.state = State.EndPos;
	  EventBus.post(new DisplayTextEvent("SetE"));
	  EventBus.post(new SetSnoozingStartPosEvent(TinkerforgeSystem.getServo().getPosition((short) 0)));
	} else if (state.equals(State.EndPos)) {
	  EventBus.post(new DisplayTextEvent("End"));
	  EventBus.post(new SetSnoozingEndPosEvent(TinkerforgeSystem.getServo().getPosition((short) 0)));
	  EventBus.post(new SetServoPosEvent(SnoozingBabyStateMachine.getStartPos()));
	  this.state = State.Null;

	  EventBus.post(new InitSnoozingStateEvent());
	}
  }

  private void handleRotiPressEventForNullState(RotiPressEvent rotiPressEvent) {
	if (rotiPressEvent.getPressedLengthInMs() > ANLERN_TRIGGER_TIME_IN_MS) {

	  //TODO BADBADBAD REFAC
	  this.state = State.Init;

	  EventBus.post(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.FULL.getValue()));
	  EventBus.post(new DisplayTextEvent("Learn"));

	  //Setzt learn velocity
	  Short learn_velocity = Short.valueOf(TinkerforgeSystem.getServoConfigProperties()
	                                                        .getProperty("learn_velocity", "50"));
	  //TODO refac exception
	  try {
		TinkerforgeSystem.getServo().setVelocity((short) 0, learn_velocity);
	  } catch (TimeoutException e) {
		e.printStackTrace();
	  } catch (NotConnectedException e) {
		e.printStackTrace();
	  }

	  //Nach 2 Sekunden Anzeige
	  new Thread(() -> {
		try {
		  Thread.sleep(2000l);
		  EventBus.post(new DisplayTextEvent("SetS"));

		  //Statuswechsel
		  AnlernStateMachine.this.state = AnlernStateMachine.State.StartPos;

		  Thread.sleep(1000l);
		  fireCount(TinkerforgeSystem.getRoti().getCount(false));

		  //TODO weil auch zwischendrin gedreht werden kann
		  TinkerforgeSystem.getRoti().getCount(/*reset*/ true);

		} catch (Exception e) {
		  e.printStackTrace();
		}
	  }
	  ).start();
	}
  }

  private boolean isDisabled() {
	return !SnoozingBabyStateMachine.getState().equals(babysnoozer.handlers.SnoozingBabyStateMachine.State.Null);
  }

  private int fireCount(int count) {
	int zaehlerwert = Math.min(900, Math.max(-900, 900 - (10 * count)));
	EventBus.post(new DisplayTextEvent(String.valueOf(zaehlerwert)));
	return zaehlerwert;
  }
}
