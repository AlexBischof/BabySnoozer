package babysnoozer.handlers.commands;

import babysnoozer.tinkerforge.BrickServoWrapper;

import static babysnoozer.tinkerforge.BrickServoWrapper.*;

/**
 * Created by Alexander Bischof on 10.03.15.
 */
public class PositionCommand implements Command {
  private final short position;
  private final Velocity velocity;
  private final Acceleration acceleration;

  public PositionCommand(int position, Velocity velocity,
                         Acceleration acceleration) {
	this.position = (short) position;
	this.velocity = velocity;
	this.acceleration = acceleration;
  }

  public short getPosition() {
	return position;
  }

  public Velocity getVelocity() {
	return velocity;
  }

  public Acceleration getAcceleration() {
	return acceleration;
  }

  @Override public String toString() {
	return "PositionCommand{" +
	       "position=" + position +
	       ", velocity=" + velocity +
	       ", acceleration=" + acceleration +
	       '}';
  }
}
