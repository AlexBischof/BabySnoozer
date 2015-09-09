package babysnoozer.handlers;

import babysnoozer.events.*;
import babysnoozer.tinkerforge.BrickServoWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.util.Arrays;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.handlers.SnoozingBabyStateMachine.State.Null;
import static babysnoozer.handlers.SnoozingBabyStateMachine.State.SetCycleCount;
import static babysnoozer.tinkerforge.BrickServoWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickServoWrapper.Velocity;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class AnlernStateMachine {

  private static final int ANLERN_TRIGGER_TIME_IN_MS = 1000;

  public enum State {
	Null, Init, StartPos, EndPos
  }

  private State state = State.Null;

	private int initStartPos;
	private int initEndPos;
	private long initReleaseWait;

  private int currPos = 0;

  @Subscribe
  @AllowConcurrentEvents
  public void handleRotiPressEvent(RotiPressEvent rotiPressEvent) throws TimeoutException, NotConnectedException {

	if (isDisabled()) {
	  return;
	}

	if (state.equals(State.Null)) {
	  if (rotiPressEvent.getPressedLengthInMs() > ANLERN_TRIGGER_TIME_IN_MS) {
		handleRotiPressEventForNullState();
	  }
	} else if (Arrays.asList(State.StartPos, State.EndPos).contains(state)) {
	  handleRotiPressEventForStartAndEndPos();
	}
  }

  @Subscribe
  @AllowConcurrentEvents
  public void handleLearnEvent(LearnEvent learnEvent) throws TimeoutException, NotConnectedException {

	if (isDisabled()) {
	  return;
	}

	  if (learnEvent.hasInitValues) {
		  this.initStartPos = learnEvent.optStartPos;
		  this.initEndPos = learnEvent.optEndPos;
		  this.initReleaseWait = learnEvent.optReleaseWait;
	  }
	  else {
		  // set initial values
		  this.initStartPos = 900;
		  this.initEndPos = 700;
		  this.initReleaseWait = 120000l;
	  }

	if (state.equals(State.Null)) {
	  handleRotiPressEventForNullState();
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
		try {
			// reset roti
			TinkerforgeSystem.getRoti().getCount(/*reset*/ true);
		}
	    catch (Exception e) {
		  e.printStackTrace();
	  	}

	  //TODO binden an properties
	  this.currPos = fireCount(count);

	  EventBus.post(new SetServoPosEvent((short) this.currPos, Velocity.learn, Acceleration.learn));
	} else {
	  int count = rotiCountEvent.getCount();

	  if (count > 0 && count <= 10) {
		SnoozingBabyStateMachine.setCycleCount(count);
		EventBus.post(new DisplayTextEvent(String.valueOf(rotiCountEvent.getCount())));
	  }
	}
  }

  private void handleRotiPressEventForStartAndEndPos()
		  throws TimeoutException, NotConnectedException {
	if (state.equals(State.StartPos)) {
	  this.state = State.EndPos;
	  EventBus.post(new DisplayTextEvent("SetE"));
	  EventBus.post(new SetSnoozingStartPosEvent(TinkerforgeSystem.getServo().getCurrentPosition()));
		this.currPos = initEndPos;
	} else if (state.equals(State.EndPos)) {
	  EventBus.post(new DisplayTextEvent("End"));
	  EventBus.post(new SetSnoozingEndPosEvent(TinkerforgeSystem.getServo().getCurrentPosition()));
	  EventBus.post(new SetServoPosEvent(SnoozingBabyStateMachine.getStartPos(), Velocity.learn, Acceleration.learn));
	  this.state = State.Null;

	  EventBus.post(new InitSnoozingStateEvent());
	}
  }

  private void handleRotiPressEventForNullState()
		  throws TimeoutException, NotConnectedException {
	//TODO BADBADBAD REFAC
	this.state = State.Init;

	EventBus.post(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.FULL.getValue()));
	EventBus.post(new DisplayTextEvent("Learn"));

	//Setzt learn velocity
	BrickServoWrapper servo = TinkerforgeSystem.getServo();
	servo.setVelocity(Velocity.learn);
	servo.setAcceleration(Acceleration.learn);

	//Nach 2 Sekunden Anzeige
	new Thread(() -> {
	  try {
		Thread.sleep(2000l);
		EventBus.post(new DisplayTextEvent("SetS"));

		//Statuswechsel
		AnlernStateMachine.this.state = AnlernStateMachine.State.StartPos;

		  // reset roti
		  TinkerforgeSystem.getRoti().getCount(/*reset*/ true);
		Thread.sleep(1000l);
		  currPos = initStartPos;
		  EventBus.post(new DisplayTextEvent(String.valueOf(initStartPos)));

	  } catch (Exception e) {
		e.printStackTrace();
	  }
	}
	).start();
  }

  private boolean isDisabled() {
	return !Arrays.asList(SetCycleCount, Null).contains(SnoozingBabyStateMachine.getState());
  }

  private int fireCount(int count) {
	int zaehlerwert = this.currPos + 10 * count;
	  zaehlerwert = Math.min(900, Math.max(-900, zaehlerwert));
	EventBus.post(new DisplayTextEvent(String.valueOf(zaehlerwert)));
	return zaehlerwert;
  }
}
