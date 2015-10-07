package babysnoozer.handlers.commands;

import static babysnoozer.tinkerforge.BrickStepperWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickStepperWrapper.Velocity;

/**
 * Encapsulates cycleCreation params.
 */
public class CycleCreationParam {
  private final int cycleCount;
  private final long releaseWait;
  private final long drawWait;
  private final long startPos;
  private final long endPos;
  private final Velocity drawVelocity;
  private final Acceleration drawAcceleration;
  private final Acceleration drawDeacceleration;
  private final Velocity releaseVelocity;
  private final Acceleration releaseAcceleration;
  private final Acceleration releaseDeacceleration;

  public CycleCreationParam(int cycleCount, long drawWait, long releaseWait, long startPos, long endPos) {
	this.cycleCount = cycleCount;
	this.drawWait = drawWait;
	this.releaseWait = releaseWait;
	this.startPos = startPos;
	this.endPos = endPos;

	this.drawVelocity = Velocity.draw;
	this.drawAcceleration = Acceleration.acc_draw;
    this.drawDeacceleration = Acceleration.deacc_draw;
	this.releaseVelocity = Velocity.release;
	this.releaseAcceleration = Acceleration.acc_release;
    this.releaseDeacceleration = Acceleration.deacc_release;
  }

  public CycleCreationParam(int cycleCount, long drawWait, long releaseWait, long startPos, long endPos,
                            Velocity drawVelocity, Acceleration drawAcceleration,
                            Acceleration drawDeacceleration,
                            Velocity releaseVelocity,
                            Acceleration releaseAcceleration,
                            Acceleration releaseDeAcceleration) {
	this.cycleCount = cycleCount;
	this.releaseWait = releaseWait;
	this.drawWait = drawWait;
	this.startPos = startPos;
	this.endPos = endPos;
	this.drawVelocity = drawVelocity;
	this.drawAcceleration = drawAcceleration;
    this.drawDeacceleration = drawDeacceleration;
	this.releaseVelocity = releaseVelocity;
	this.releaseAcceleration = releaseAcceleration;
    this.releaseDeacceleration = releaseDeAcceleration;
  }

  public int getCycleCount() {
	return cycleCount;
  }

  public long getReleaseWait() {
	return releaseWait;
  }

  public long getDrawWait() {
	return drawWait;
  }

  public long getStartPos() {
	return startPos;
  }

  public long getEndPos() {
	return endPos;
  }

  public Velocity getDrawVelocity() {
	return drawVelocity;
  }

  public Acceleration getDrawAcceleration() {
	return drawAcceleration;
  }

  public Acceleration getDrawDeacceleration() {
    return drawDeacceleration;
  }

  public Velocity getReleaseVelocity() {
	return releaseVelocity;
  }

  public Acceleration getReleaseAcceleration() { return releaseAcceleration; }

  public Acceleration getReleaseDeacceleration() { return releaseDeacceleration; }
}
