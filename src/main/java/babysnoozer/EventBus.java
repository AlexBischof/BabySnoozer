package babysnoozer;

import babysnoozer.handlers.*;
import com.google.common.eventbus.AsyncEventBus;

import java.util.concurrent.Executors;

import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public enum EventBus {

  EventBus;

  private com.google.common.eventbus.EventBus eventBus;

  private EventBus() {
    // AsyncEventBus does not work currently
    // Handlers are never called by Guava EventBus
    eventBus = new com.google.common.eventbus.EventBus();
//	eventBus = new AsyncEventBus(
//			Executors.newCachedThreadPool(), new EventSubscriberExceptionHandler());

	registerHandlers();
  }

  private void registerHandlers() {
	eventBus.register(new LogHandler());
	eventBus.register(new DisplayHandler());
	eventBus.register(new AnlernStateMachine());
	eventBus.register(new ServoHandler());
	eventBus.register(new SnoozingBabyHandler());
	eventBus.register(new SnoozeCycleStateHandler());
  }

  public void post(Object event) {
	eventBus.post(event);
  }
}
