package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.SnoozingStartEvent;

import java.util.Timer;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozyBabyHandler implements EventHandler {
  @Override public void handle(Event event) {
	if (event.getClass().equals(SnoozingStartEvent.class)) {
	  SnoozingBabyStateMachine.instance().start();


	}
  }
}
