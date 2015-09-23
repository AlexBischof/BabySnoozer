package babysnoozer.events;

import static babysnoozer.tinkerforge.BrickStepperWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickStepperWrapper.Velocity;

public class SetStepperPosEvent {
    private final int pos;
    private final Velocity velocity;
    private final Acceleration acceleration;

    public SetStepperPosEvent(int pos, Velocity velocity, Acceleration acceleration) {
        this.pos = pos;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public int getPos() {
        return pos;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public Acceleration getAcceleration() {
        return acceleration;
    }
}
