package babysnoozer.handlers;

import babysnoozer.events.DisplayTextEvent;
import babysnoozer.events.SetStepperPosEvent;
import babysnoozer.tinkerforge.BrickStepperWrapper;

import static babysnoozer.EventBus.EventBus;

public class LearnCycle {

    private int rotiValue;
    private final int displayValueMultiplicator;
    private final int learnValueMultiplicator;
    private final int motorPositionMultiplicator;
    private final boolean controlMotor;

    public LearnCycle(int learnValueMultiplicator,
                      int displayValueMultiplicator,
                      int motorPositionMultiplicator)
    {
        this.learnValueMultiplicator = learnValueMultiplicator;
        this.displayValueMultiplicator = displayValueMultiplicator;
        this.motorPositionMultiplicator = motorPositionMultiplicator;
        this.controlMotor = true;
    }

    public LearnCycle(int learnValueMultiplicator,
                      int displayValueMultiplicator)
    {
        this.learnValueMultiplicator = learnValueMultiplicator;
        this.displayValueMultiplicator = displayValueMultiplicator;
        this.motorPositionMultiplicator = 0;
        this.controlMotor = false;
    }

    public int getMotorPosition()
    {
        return this.rotiValue * motorPositionMultiplicator;
    }

    private String getDisplayValue()
    {
        return String.valueOf(this.rotiValue * displayValueMultiplicator);
    }

    public long getLearnValue()
    {
        return this.rotiValue * learnValueMultiplicator;
    }

    public void setRotiValue(int rotiValue)
    {
        this.rotiValue = rotiValue;
        EventBus.post(new DisplayTextEvent(this.getDisplayValue()));
        if (controlMotor)
        {
            EventBus.post(new SetStepperPosEvent(
                    this.getMotorPosition(),
                    BrickStepperWrapper.Velocity.learn,
                    BrickStepperWrapper.Acceleration.acc_learn,
                    BrickStepperWrapper.Acceleration.deacc_learn));
        }
    }

    public void setInitLearnValue(long learnValue)
    {
        this.rotiValue = Math.round(learnValue / learnValueMultiplicator);
        setRotiValue(this.rotiValue);
    }
}
