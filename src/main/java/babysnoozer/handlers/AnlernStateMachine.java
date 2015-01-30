package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventBus;
import babysnoozer.events.*;
import babysnoozer.tinkerforge.TinkerforgeSystem;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.util.Arrays;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class AnlernStateMachine {

  public static final int ANLERN_TRIGGER_TIME_IN_MS = 1000;

  public enum State {
	Null, Init, StartPos, EndPos;
  }

  private State state;

  public AnlernStateMachine() {
	state = State.Null;
  }

  public State getState() {
	return state;
  }

  public void next(Event event) {

	//Nur bei null möglich TODO
	if (!SnoozingBabyStateMachine.instance().getState().equals(SnoozingBabyStateMachine.State.Null)) {
	  return;
	}

	boolean changeToInitState = event.getClass().equals(RotiPressEvent.class) && state.equals(State.Null);
	if (changeToInitState) {
	  RotiPressEvent rotiPressEvent = (RotiPressEvent) event;

	  if (rotiPressEvent.getPressedLengthInMs() > ANLERN_TRIGGER_TIME_IN_MS) {

		//TODO BADBADBAD REFAC
		this.state = State.Init;

		EventBus.instance().fire(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.FULL.getValue()));
		EventBus.instance().fire(new DisplayTextEvent("Learn"));

		//Setzt learn velocity
		Short learn_velocity = Short.valueOf(TinkerforgeSystem.instance().getServoConfigProperties()
		                                                      .getProperty("learn_velocity", "50"));
		//TODO refac exception
		try {
		  TinkerforgeSystem.instance().getServo().setVelocity((short) 0, learn_velocity);
		} catch (TimeoutException e) {
		  e.printStackTrace();
		} catch (NotConnectedException e) {
		  e.printStackTrace();
		}

		//Nach 2 Sekunden Anzeige
		new Thread(() -> {
		  try {
			Thread.sleep(2000l);
			EventBus.instance().fire(new DisplayTextEvent("SetS"));

			//Statuswechsel
			AnlernStateMachine.this.state = AnlernStateMachine.State.StartPos;

			Thread.sleep(1000l);
			fireCount(TinkerforgeSystem.instance().getRoti().getCount(false));

			//TODO weil auch zwischendrin gedreht werden kann
			TinkerforgeSystem.instance().getRoti().getCount(/*reset*/ true);

		  } catch (Exception e) {
			e.printStackTrace();
		  }
		}
		).start();
	  }
	}

	//TODO refac
	boolean acceptRotiCountEvent =
			Arrays.asList(State.StartPos, State.EndPos).contains(state) && event.getClass().equals(
					RotiCountEvent.class);
	if (acceptRotiCountEvent) {

	  RotiCountEvent rotiCountEvent = (RotiCountEvent) event;
	  int count = rotiCountEvent.getCount();

	  //TODO binden an properties
	  int zaehlerwert = fireCount(count);

	  EventBus.instance().fire(new SetServoPosEvent((short) zaehlerwert));
	}

	//TODO refac for next and end
	boolean switchToEndPosState =
			Arrays.asList(State.StartPos, State.EndPos).contains(state) && event.getClass().equals(
					RotiPressEvent.class);
	if (switchToEndPosState) {
	  //TODO refac
	  try {

		if (state.equals(State.StartPos)) {
		  this.state = State.EndPos;
		  EventBus.instance().fire(new DisplayTextEvent("SetE"));
		  EventBus.instance()
		          .fire(new SetSnoozingStartPosEvent(TinkerforgeSystem.instance().getServo().getPosition((short) 0)));
		} else if (state.equals(State.EndPos)) {
		  EventBus.instance().fire(new DisplayTextEvent("End"));
		  EventBus.instance()
		          .fire(new SetSnoozingEndPosEvent(TinkerforgeSystem.instance().getServo().getPosition((short) 0)));
		  EventBus.instance()
		          .fire(new SetServoPosEvent((short) SnoozingBabyStateMachine.instance().getStartPos()));
		  this.state = State.Null;

		  EventBus.instance().fire(new InitSnoozingStateEvent());
		}
	  } catch (TimeoutException e) {
		e.printStackTrace();
	  } catch (NotConnectedException e) {
		e.printStackTrace();
	  }
	}
  }

  private int fireCount(int count) {
	int zaehlerwert = Math.min(900, Math.max(-900, 900 - (10 * count)));
	EventBus.instance().fire(new DisplayTextEvent(String.valueOf(zaehlerwert)));
	return zaehlerwert;
  }
}
