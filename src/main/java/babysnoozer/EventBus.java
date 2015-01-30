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
	eventBus = new AsyncEventBus(
			Executors.newCachedThreadPool(), new EventSubscriberExceptionHandler());
  }

  private void registerHandlers() {
	eventBus.register(new LogHandler());
	eventBus.register(new DisplayHandler(TinkerforgeSystem.getDisplay4x7()));
	eventBus.register(new AnlernStateMachine());
	eventBus.register(new ServoHandler());
	eventBus.register(new SnoozingBabyHandler());
	eventBus.register(new SnoozeCycleStateHandler());
  }

  public void post(Object event) {
	eventBus.post(event);
  }
}
