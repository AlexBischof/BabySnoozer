package babysnoozer;

import babysnoozer.handlers.*;

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
	eventBus.register(new StepperHandler());
	eventBus.register(new SnoozingBabyHandler());
  }

  public void post(Object event) {
	eventBus.post(event);
  }
}
