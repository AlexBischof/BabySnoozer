package babysnoozer.events;

import babysnoozer.Event;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class ShutdownEvent implements Event {

  @Override public boolean isAsync() {
	return false;
  }
}
