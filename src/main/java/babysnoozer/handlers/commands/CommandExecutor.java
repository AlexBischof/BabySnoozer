package babysnoozer.handlers.commands;

import babysnoozer.EventBus;
import babysnoozer.events.ServoPositionReachedEvent;
import babysnoozer.events.SetServoPosEvent;

/**
 * Created by Alexander Bischof on 10.03.15.
 */
public class CommandExecutor {

  public void execute(Command command) {
	if (command instanceof WaitCommand) {
	  WaitCommand waitCommand = (WaitCommand) command;
	  try {
		Thread.sleep(waitCommand.getWaitInMillis());
	  } catch (InterruptedException e) {
		e.printStackTrace();
	  } finally {
		EventBus.EventBus.post(new ServoPositionReachedEvent());
	  }
	} else if (command instanceof PositionCommand) {
	  PositionCommand positionCommand = (PositionCommand) command;
	  EventBus.EventBus.post(new SetServoPosEvent(positionCommand.getPosition(), positionCommand.getVelocity(),
	                                              positionCommand.getAcceleration()));
	}
  }
}
