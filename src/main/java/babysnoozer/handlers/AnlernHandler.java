package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.RotiPressEvent;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class AnlernHandler implements EventHandler {

  private AnlernStateMachine machine = new AnlernStateMachine();

  @Override public void handle(Event event) {

    //TODO wie kommt man in den Initzustand
    if (event.getClass().equals(RotiPressEvent.class)) {

      RotiPressEvent rotiPressEvent = (RotiPressEvent) event;

      //Analyze Event for Mapping to states
      machine.getState().next(rotiPressEvent);
	}
  }
}
