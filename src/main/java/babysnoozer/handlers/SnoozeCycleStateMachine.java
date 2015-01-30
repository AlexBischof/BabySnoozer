package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventBus;
import babysnoozer.events.DisplayTextEvent;
import babysnoozer.events.ServoPositionReachedEvent;
import babysnoozer.events.SetServoPosEvent;
import babysnoozer.tinkerforge.TinkerforgeSystem;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

/**
 * Created by Alexander Bischof on 30.01.15.
 */
public class SnoozeCycleStateMachine {
  public enum State {
	Draw, Release;
  }

  private State targetState;
  private int cyclesLeft;

  public SnoozeCycleStateMachine(int cyclesLeft) {
	this.cyclesLeft = cyclesLeft;
	this.targetState = State.Draw;
  }

  public void next(Event event) {
	Class<? extends Event> eventClass = event.getClass();

	if (eventClass.equals(ServoPositionReachedEvent.class)) {
	  boolean isDraw = this.targetState.equals(State.Draw);

	  SnoozingBabyStateMachine snoozingBabyStateMachine = SnoozingBabyStateMachine.instance();

	  //changes targetState
	  short targetPos = 0;
	  if (isDraw) {

	    Short learn_velocity = Short.valueOf(TinkerforgeSystem.instance().getServoConfigProperties()
	                                                          .getProperty("snooze_velocity", "50"));
	    //TODO refac exception
	    try {
		  TinkerforgeSystem.instance().getServo().setVelocity((short) 0, learn_velocity);
	    } catch (TimeoutException e) {
		  e.printStackTrace();
	    } catch (NotConnectedException e) {
		  e.printStackTrace();
	    }

		this.targetState = State.Release;
		targetPos = snoozingBabyStateMachine.getStartPos();
	  } else {
		this.targetState = State.Draw;
		this.cyclesLeft--;
		targetPos = snoozingBabyStateMachine.getEndPos();
	  }

	  if (this.cyclesLeft > 0) {
		//fire new Event
		EventBus.instance().fire(new SetServoPosEvent(targetPos));
	    EventBus.instance().fire(new DisplayTextEvent(String.valueOf(cyclesLeft)));
	  }
	}
  }
}
