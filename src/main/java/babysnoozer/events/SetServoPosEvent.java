package babysnoozer.events;

import static babysnoozer.tinkerforge.BrickServoWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickServoWrapper.Velocity;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class SetServoPosEvent {
  private final short pos;
  private final Velocity velocity;
  private final Acceleration acceleration;

  public SetServoPosEvent(short pos, Velocity velocity, Acceleration acceleration) {
	this.pos = pos;
	this.velocity = velocity;
	this.acceleration = acceleration;
  }

  public short getPos() {
	return pos;
  }

  public Velocity getVelocity() {
	return velocity;
  }

  public Acceleration getAcceleration() {
	return acceleration;
  }
}
