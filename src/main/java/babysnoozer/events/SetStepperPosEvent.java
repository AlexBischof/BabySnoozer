package babysnoozer.events;

import static babysnoozer.tinkerforge.BrickStepperWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickStepperWrapper.Velocity;

public class SetStepperPosEvent {
    private final long pos;
    private final Velocity velocity;
    private final Acceleration deacceleration;
    private final Acceleration acceleration;

    public SetStepperPosEvent(long pos, Velocity velocity, Acceleration acceleration,
                              Acceleration deacceleration) {
        this.pos = pos;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.deacceleration = deacceleration;
    }

    public long getPos() {
        return pos;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public Acceleration getAcceleration() {
        return acceleration;
    }
    public Acceleration getDeacceleration() {
        return deacceleration;
    }
}
