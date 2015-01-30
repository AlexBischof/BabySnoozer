package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.*;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozeCycleStateHandler implements EventHandler {

  private SnoozeCycleStateMachine snoozeCycleStateMachine;

  @Override public void handle(Event event) {

	try {
	  Class<? extends Event> aClass = event.getClass();
	  if (aClass.equals(SnoozingStartEvent.class)) {
	    snoozeCycleStateMachine = new SnoozeCycleStateMachine(SnoozingBabyStateMachine.instance().getCycleCount());
	  }else if (aClass.equals(ServoPositionReachedEvent.class)){
	    if (snoozeCycleStateMachine != null) {
	      snoozeCycleStateMachine.next(event);
	    }
	  }
	} catch (Exception e) {e.printStackTrace();}
  }
}
