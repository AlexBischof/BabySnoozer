package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.LogEvent;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class LogHandler implements EventHandler {
  @Override public void handle(Event event) {
	if (event.getClass().equals(LogEvent.class)){
	  System.out.println(((LogEvent)event).getText());
	}
  }
}
