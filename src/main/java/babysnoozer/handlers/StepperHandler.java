package babysnoozer.handlers;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.events.SetStepperPosEvent;
import babysnoozer.events.StepperPositionReachedEvent;
import babysnoozer.events.ShutdownEvent;
import babysnoozer.events.StepperDisableEvent;
import babysnoozer.tinkerforge.BrickStepperWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import java.io.IOException;
import java.util.Properties;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

public class StepperHandler {

    @Subscribe
    public void handleShutdownEvent(ShutdownEvent shutdownEvent) throws TimeoutException, NotConnectedException {
        TinkerforgeSystem.getStepper().disable();

        Properties properties;
        try {
            PropertiesLoader propertiesLoader = new PropertiesLoader("initialpositionrecall.properties", false);
            properties = propertiesLoader.load();
            properties.setProperty("lastPosition", String.valueOf(TinkerforgeSystem.getStepper().getCurrentPosition()));
            propertiesLoader.store(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Ende");

        System.exit(0);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSetStepperPosEvent(SetStepperPosEvent setStepperPosEvent)
            throws TimeoutException, NotConnectedException {

        BrickStepperWrapper stepper = TinkerforgeSystem.getStepper();

        /*
         * problem: if current position equal to desired position no positionreachedevent is fired...damn
         */
        int pos = setStepperPosEvent.getPos();
        if (stepper.getCurrentPosition() == pos) {
            EventBus.post(new StepperDisableEvent());
            EventBus.post(new StepperPositionReachedEvent());
        } else {
            stepper.setAcceleration(setStepperPosEvent.getAcceleration(),
                    setStepperPosEvent.getDeacceleration());
            stepper.setVelocity(setStepperPosEvent.getVelocity());
            if (!stepper.isEnabled()) {
                stepper.enable();
            }
            stepper.setPosition(pos);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleStepperDisableEvent(StepperDisableEvent stepperDisableEvent)
            throws TimeoutException, NotConnectedException {
        BrickStepperWrapper stepper = TinkerforgeSystem.getStepper();
        stepper.disable();
    }


}
