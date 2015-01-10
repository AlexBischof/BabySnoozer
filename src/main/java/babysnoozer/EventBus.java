package babysnoozer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class EventBus {

  private static final EventBus singleton = new EventBus();

  private List<EventHandler> eventHandlerList = new ArrayList<>();

  private EventBus() {
  }

  public static EventBus instance() {
	return singleton;
  }

  public void registerHandler(EventHandler handler) {
	eventHandlerList.add(handler);
  }

  public void fire(Event event) {
	//TODO nochmal hinsichtlich flow anschauen
	if (event.isAsync()) {
	  new Thread() {
		@Override public void run() {
		  fireAllHandlers(event);
		}
	  }.start();
	} else {
	  fireAllHandlers(event);
	}
  }

  private void fireAllHandlers(Event event) {
	eventHandlerList.parallelStream().forEach(e -> e.handle(event));

  }
}
