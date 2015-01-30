package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.RotiCountEvent;
import babysnoozer.events.RotiPressEvent;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class AnlernHandler implements EventHandler {

  private AnlernStateMachine machine = new AnlernStateMachine();

  @Override public void handle(Event event) {

	  Class<? extends Event> aClass = event.getClass();
	  if (aClass.equals(RotiPressEvent.class)) {

		RotiPressEvent rotiPressEvent = (RotiPressEvent) event;
		machine.next(rotiPressEvent);

	  } else if (aClass.equals(RotiCountEvent.class)) {

		RotiCountEvent rotiCountEvent = (RotiCountEvent) event;
		machine.next(rotiCountEvent);
	}
  }
}
