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
        lvl1("50", "speed_lvl1"),
        lvl2("100", "speed_lvl2"),
        lvl3("600", "speed_lvl3"),
        lvl4("10000", "speed_lvl4"),
        max("65535", "speed_max"),
        learn("50", "speed_learn");

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
        lvl1("50", "acc_lvl1"),
        lvl2("100", "acc_lvl2"),
        lvl3("600", "acc_lvl3"),
        lvl4("10000", "acc_lvl4"),
        max("65535", "acc_max"),
        learn("50", "acc_learn");

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

    public void setAcceleration(Acceleration acceleration) throws TimeoutException, NotConnectedException {
        // same acceleration as deacceleration
        this.brickStepper.setSpeedRamping(
                valueOf(
                    stepperConfigProperties.getProperty(
                        acceleration.getPropertyString(), acceleration.getDefaultValue())),
                valueOf(
                    stepperConfigProperties
                        .getProperty(acceleration.getPropertyString(), acceleration.getDefaultValue())));
    }

    public void configStepper() throws TimeoutException, NotConnectedException {

        //Sets nextCommand properties
        brickStepper.setMinimumVoltage(valueOf(stepperConfigProperties.getProperty("outputVoltage", "7500")));
        brickStepper.setMotorCurrent(800);
        brickStepper.setStepMode((short)8);

        brickStepper.setMaxVelocity(valueOf(stepperConfigProperties.getProperty("speed_lvl4", "200")));
        brickStepper.setSpeedRamping(valueOf(stepperConfigProperties.getProperty("acc_lvl3", "200")),
                valueOf(stepperConfigProperties.getProperty("acc_lvl4", "200")));

        brickStepper.setDecay(valueOf(stepperConfigProperties.getProperty("decay", String.valueOf("65535"))));

        //Sets Stepper Listener
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

    public void setDecay(int decay) throws TimeoutException, NotConnectedException {
        brickStepper.setDecay(decay);
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
