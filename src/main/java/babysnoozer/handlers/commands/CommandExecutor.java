package babysnoozer.handlers.commands;

import babysnoozer.EventBus;
import babysnoozer.events.StepperDisableEvent;
import babysnoozer.events.StepperPositionReachedEvent;
import babysnoozer.events.SetStepperPosEvent;

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
		EventBus.EventBus.post(new StepperPositionReachedEvent());
	  }
	} else if (command instanceof MotorDisableCommand) {
		EventBus.EventBus.post(new StepperDisableEvent());
	}
	else if (command instanceof PositionCommand) {
	  PositionCommand positionCommand = (PositionCommand) command;
	  EventBus.EventBus.post(new SetStepperPosEvent(
			  positionCommand.getPosition(),
			  positionCommand.getVelocity(),
			  positionCommand.getAcceleration(),
			  positionCommand.getDeacceleration()));
	}
  }
}
