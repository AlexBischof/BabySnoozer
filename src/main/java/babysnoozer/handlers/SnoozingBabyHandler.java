package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.*;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozingBabyHandler implements EventHandler {

  @Override public void handle(Event event) {

	SnoozingBabyStateMachine snoozingBabyStateMachine = SnoozingBabyStateMachine.instance();

	try {
	  Class<? extends Event> aClass = event.getClass();
	  if (aClass.equals(SnoozingStartEvent.class)) {
		SnoozingBabyStateMachine.instance().next(event);
	  } else if (aClass.equals(InitSnoozingStateEvent.class)) {
		SnoozingBabyStateMachine.instance().next(event);
	  } else if (aClass.equals(RotiCountEvent.class)) {
		SnoozingBabyStateMachine.instance().next(event);
	  } else if (aClass.equals(RotiPressEvent.class)) {
		SnoozingBabyStateMachine.instance().next(event);
	  } else if (aClass.equals(SetSnoozingStartPosEvent.class)) {
		SnoozingBabyStateMachine.instance().next(event);
	  } else if (aClass.equals(SetSnoozingEndPosEvent.class)) {
		SnoozingBabyStateMachine.instance().next(event);
	  }
	} catch (Exception e) {e.printStackTrace();}
  }
}
