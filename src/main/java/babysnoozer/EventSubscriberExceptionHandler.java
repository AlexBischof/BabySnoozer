package babysnoozer;

import babysnoozer.events.ShutdownEvent;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import static babysnoozer.EventBus.*;

/**
 * Created by Alexander Bischof on 30.01.15.
 */
public class EventSubscriberExceptionHandler implements SubscriberExceptionHandler {

  @Override public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {
    throwable.printStackTrace();
	//EventBus.post(new ShutdownEvent());
  }
}
