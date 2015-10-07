package babysnoozer.handlers.commands;

import static babysnoozer.tinkerforge.BrickStepperWrapper.*;

/**
 * Created by Alexander Bischof on 10.03.15.
 */
public class PositionCommand implements Command {
  private final int position;
  private final Velocity velocity;
  private final Acceleration acceleration;
  private final Acceleration deacceleration;

  public PositionCommand(int position, Velocity velocity,
                         Acceleration acceleration,
                         Acceleration deacceleration) {
	this.position = position;
	this.velocity = velocity;
	this.acceleration = acceleration;
    this.deacceleration = deacceleration;
  }

  public int getPosition() {
	return position;
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

  @Override public String toString() {
	return "PositionCommand{" +
	       "position=" + position +
	       ", velocity=" + velocity +
	       ", acceleration=" + acceleration +
           ", deacceleration=" + deacceleration +
	       '}';
  }
}
