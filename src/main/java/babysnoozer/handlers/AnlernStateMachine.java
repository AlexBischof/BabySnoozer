package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventBus;
import babysnoozer.events.DisplayEvent;
import babysnoozer.events.RotiCountEvent;
import babysnoozer.events.RotiPressEvent;
import babysnoozer.events.SetServoPosEvent;
import babysnoozer.tinkerforge.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class AnlernStateMachine {

  public enum State {
	Null, Init, StartPos, EndPos;
  }

  private State state;
  private int lastCount;

  public AnlernStateMachine() {
	state = State.Null;
  }

  public State getState() {
	return state;
  }

  public void next(Event event) {

	boolean changeToInitState = event.getClass().equals(RotiPressEvent.class) && state.equals(State.Null);
	if (changeToInitState) {
	  RotiPressEvent rotiPressEvent = (RotiPressEvent) event;

	  if (rotiPressEvent.getPressedLengthInMs() > 3000l) {

		//TODO BADBADBAD REFAC
		this.state = State.Init;

		EventBus.instance().fire(new DisplayEvent("Learn"));

		//Nach 2 Sekunden Anzeige
		new Thread() {
		  @Override public void run() {
			try {
			  Thread.sleep(2000l);
			  EventBus.instance().fire(new DisplayEvent("SetS"));

			  //Statuswechsel
			  AnlernStateMachine.this.state = AnlernStateMachine.State.StartPos;

			   //TODO weil auch zwischendrin gedreht werden kann
			  TinkerforgeSystem.instance().getRoti().getCount(/*reset*/ true);

			} catch (Exception e) {
			  e.printStackTrace();
			}
		  }
		}.start();
	  }
	}

	//TODO refac
	boolean acceptRotiCountEvent = state.equals(State.StartPos) && event.getClass().equals(RotiCountEvent.class);
	if (acceptRotiCountEvent) {

	  RotiCountEvent rotiCountEvent = (RotiCountEvent) event;

	  int count = rotiCountEvent.getCount();
	  EventBus.instance().fire(new SetServoPosEvent(count - lastCount));

	  this.lastCount = count;
	}
  }
}
