package babysnoozer.handlers.commands;

import static babysnoozer.tinkerforge.BrickServoWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickServoWrapper.Velocity;

/**
 * Encapsulates cycleCreation params.
 */
public class CycleCreationParam {
  private final int cycleCount;
  private final long releaseWait;
  private final int startPos;
  private final int endPos;
  private final Velocity drawVelocity;
  private final Acceleration drawAcceleration;
  private final Velocity releaseVelocity;
  private final Acceleration releaseAcceleration;

  public CycleCreationParam(int cycleCount, long releaseWait, int startPos, int endPos) {
	this.cycleCount = cycleCount;
	this.releaseWait = releaseWait;
	this.startPos = startPos;
	this.endPos = endPos;

	this.drawVelocity = Velocity.learn;
	this.drawAcceleration = Acceleration.learn;
	this.releaseVelocity = Velocity.learn;
	this.releaseAcceleration = Acceleration.learn;
  }

  public CycleCreationParam(int cycleCount, long releaseWait, int startPos, int endPos,
                            Velocity drawVelocity, Acceleration drawAcceleration,
                            Velocity releaseVelocity,
                            Acceleration releaseAcceleration) {
	this.cycleCount = cycleCount;
	this.releaseWait = releaseWait;
	this.startPos = startPos;
	this.endPos = endPos;
	this.drawVelocity = drawVelocity;
	this.drawAcceleration = drawAcceleration;
	this.releaseVelocity = releaseVelocity;
	this.releaseAcceleration = releaseAcceleration;
  }

  public int getCycleCount() {
	return cycleCount;
  }

  public long getReleaseWait() {
	return releaseWait;
  }

  public int getStartPos() {
	return startPos;
  }

  public int getEndPos() {
	return endPos;
  }

  public Velocity getDrawVelocity() {
	return drawVelocity;
  }

  public Acceleration getDrawAcceleration() {
	return drawAcceleration;
  }

  public Velocity getReleaseVelocity() {
	return releaseVelocity;
  }

  public Acceleration getReleaseAcceleration() {
	return releaseAcceleration;
  }
}
