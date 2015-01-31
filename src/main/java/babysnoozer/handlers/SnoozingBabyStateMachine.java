package babysnoozer.handlers;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public enum SnoozingBabyStateMachine {

  SnoozingBabyStateMachine;

  public enum State {
	Null, SetCycleCount, Snooze, ShutDown
  }

  private State state = State.Null;

  private RopeDistance ropeDistance;
  private int cycleCount = 1;

  public State getState() {
	return state;
  }

  public int getCycleCount() {
	return cycleCount;
  }

  public void setState(State state) {
	this.state = state;
  }

  public void setCycleCount(int cycleCount) {
	this.cycleCount = cycleCount;
  }

  public short getStartPos() {
	return getRopeDistance().getStartPos();
  }

  public RopeDistance getRopeDistance() {
	if (ropeDistance == null) {
	  ropeDistance = new RopeDistance();
	}
	return ropeDistance;
  }

  public void setStartPos(short startPos) {
    getRopeDistance().setStartPos(startPos);
  }

  public short getEndPos() {
	return getRopeDistance().getEndPos();
  }

  public void setEndPos(short endPos) {
    getRopeDistance().setEndPos(endPos);
  }
}
