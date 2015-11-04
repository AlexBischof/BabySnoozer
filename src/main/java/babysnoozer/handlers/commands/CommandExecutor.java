package babysnoozer.handlers.commands;

import babysnoozer.EventBus;
import babysnoozer.events.SetStepperPosEvent;
import babysnoozer.events.StepperDisableDoneEvent;
import babysnoozer.events.StepperPositionReachedEvent;
import babysnoozer.events.WaitFinishedEvent;
import babysnoozer.tinkerforge.BrickStepperWrapper;
import babysnoozer.tinkerforge.TinkerforgeSystem;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

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
            }
            EventBus.EventBus.post(new WaitFinishedEvent());
        } else if (command instanceof MotorDisableCommand) {
            System.out.println("CommandExecutor: Disabling motor");
            BrickStepperWrapper stepper = TinkerforgeSystem.TinkerforgeSystem.getStepper();
            try {
                stepper.disable();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }
            EventBus.EventBus.post(new StepperDisableDoneEvent());
        } else if (command instanceof PositionCommand) {
            PositionCommand positionCommand = (PositionCommand) command;
            EventBus.EventBus.post(new SetStepperPosEvent(
                    positionCommand.getPosition(),
                    positionCommand.getVelocity(),
                    positionCommand.getAcceleration(),
                    positionCommand.getDeacceleration()));
        }
    }
}
