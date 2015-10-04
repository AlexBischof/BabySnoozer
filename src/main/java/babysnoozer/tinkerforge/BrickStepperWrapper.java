package babysnoozer.tinkerforge;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.listeners.StepperListener;
import com.tinkerforge.BrickStepper;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.io.IOException;
import java.util.Properties;

import static java.lang.Integer.valueOf;
import static java.lang.Short.MAX_VALUE;

public class BrickStepperWrapper {

    private static final String STEPPER_BRICK_UID = "6R5FYW";

    private Properties stepperConfigProperties;

    public enum Velocity {
        draw("50", "speed_draw"),
        release("100", "speed_release"),
        learn("600", "speed_learn");

        private String defaultValue;
        private String propertyString;

        Velocity(String defaultValue, String propertyString) {
            this.defaultValue = defaultValue;
            this.propertyString = propertyString;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getPropertyString() {
            return propertyString;
        }
    }

    public enum Acceleration {
        acc_draw("50", "acc_draw"),
        deacc_draw("100", "deacc_draw"),
        acc_release("600", "acc_release"),
        deacc_release("10000", "deacc_release"),
        acc_learn("65535", "acc_learn"),
        deacc_learn("50", "deacc_learn");

        private String defaultValue;
        private String propertyString;

        Acceleration(String defaultValue, String propertyString) {
            this.defaultValue = defaultValue;
            this.propertyString = propertyString;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getPropertyString() {
            return propertyString;
        }
    }

    public BrickStepper brickStepper;

    public BrickStepperWrapper() {

        try {
            stepperConfigProperties = new PropertiesLoader("stepper.properties").load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initBrick(IPConnection ipconnection) {
        this.brickStepper = new BrickStepper(STEPPER_BRICK_UID, ipconnection);
    }

    public void setVelocity(Velocity velocity) throws TimeoutException, NotConnectedException {
        this.brickStepper.setMaxVelocity(valueOf(
                stepperConfigProperties.getProperty(velocity.getPropertyString(), velocity.getDefaultValue())));
    }

    public void setAcceleration(Acceleration acc, Acceleration deacc) throws TimeoutException, NotConnectedException {
        this.brickStepper.setSpeedRamping(
                valueOf(stepperConfigProperties.getProperty(acc.getPropertyString(), acc.getDefaultValue())),
                valueOf(stepperConfigProperties.getProperty(deacc.getPropertyString(), deacc.getDefaultValue())));
    }

    public void configStepper() throws TimeoutException, NotConnectedException {

        // Sets properties
        brickStepper.setMinimumVoltage(valueOf(stepperConfigProperties.getProperty("minOutputVoltage", "7200")));
        brickStepper.setMotorCurrent(valueOf(stepperConfigProperties.getProperty("maxMotorCurrent", "800")));
        brickStepper.setStepMode(Short.valueOf(stepperConfigProperties.getProperty("stepMode", "8")));

        brickStepper.setDecay(valueOf(stepperConfigProperties.getProperty("decay", String.valueOf("65535"))));

        // Sets Stepper Listener
        StepperListener stepperListener = new StepperListener();
        brickStepper.addUnderVoltageListener(stepperListener);
        brickStepper.addPositionReachedListener(stepperListener);
    }

    public int getCurrentPosition() throws TimeoutException, NotConnectedException {
        return brickStepper.getCurrentPosition();
    }

    public void setCurrentPosition(int position) throws TimeoutException, NotConnectedException {
        brickStepper.setCurrentPosition(position);
    }

    public void setPosition(int position) throws TimeoutException, NotConnectedException {
        brickStepper.setTargetPosition(position);
    }

    public void enable() throws TimeoutException, NotConnectedException {
        brickStepper.enable();
    }

    public boolean isEnabled() throws TimeoutException, NotConnectedException {
        return brickStepper.isEnabled();
    }

    public void disable() throws TimeoutException, NotConnectedException {
        brickStepper.disable();
    }
}
