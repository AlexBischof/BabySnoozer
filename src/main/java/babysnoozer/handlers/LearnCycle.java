package babysnoozer.handlers;

import babysnoozer.events.DisplayTextEvent;
import babysnoozer.events.SetStepperPosEvent;
import babysnoozer.tinkerforge.BrickStepperWrapper;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

public class LearnCycle {

    private int rotiValue;
    private final int displayValueMultiplicator;
    private final int learnValueMultiplicator;
    private final int motorPositionMultiplicator;
    private final boolean controlMotor;

    private final long minLearnValue;
    private final long maxLearnValue;


    public LearnCycle(int learnValueMultiplicator,
                      int displayValueMultiplicator,
                      int motorPositionMultiplicator,
                      long minLearnValue,
                      long maxLearnValue)
    {
        this.learnValueMultiplicator = learnValueMultiplicator;
        this.displayValueMultiplicator = displayValueMultiplicator;
        this.motorPositionMultiplicator = motorPositionMultiplicator;
        this.controlMotor = true;
        this.minLearnValue = minLearnValue;
        this.maxLearnValue = maxLearnValue;
    }

    public LearnCycle(int learnValueMultiplicator,
                      int displayValueMultiplicator,
                      long minLearnValue,
                      long maxLearnValue)
    {
        this.learnValueMultiplicator = learnValueMultiplicator;
        this.displayValueMultiplicator = displayValueMultiplicator;
        this.motorPositionMultiplicator = 0;
        this.controlMotor = false;
        this.minLearnValue = minLearnValue;
        this.maxLearnValue = maxLearnValue;
    }

    public int getMotorPosition()
    {
        return this.rotiValue * motorPositionMultiplicator;
    }

    private int getMinRotiValue()
    {
        return Math.round(this.minLearnValue / this.learnValueMultiplicator);
    }

    private int getMaxRotiValue()
    {
        return Math.round(this.maxLearnValue / this.learnValueMultiplicator);
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
        int newRotiValue = this.rotiValue + rotiValue;
        int minRotiValue = this.getMinRotiValue();
        int maxRotiValue = this.getMaxRotiValue();

        if (newRotiValue < minRotiValue)
            newRotiValue = minRotiValue;
        else if (newRotiValue > maxRotiValue)
            newRotiValue = maxRotiValue;

        this.rotiValue = newRotiValue;

        try {
            TinkerforgeSystem.getRoti().getCount(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
