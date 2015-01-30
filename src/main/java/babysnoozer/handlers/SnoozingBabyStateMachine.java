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

  private short startPos;
  private short endPos;
  private int cycleCount = 1;

  public short getStartPos() {
	return startPos;
  }

  public short getEndPos() {
	return endPos;
  }

  public State getState() {
	return state;
  }

  public int getCycleCount() {
	return cycleCount;
  }

  public void setState(State state) {
	this.state = state;
  }

  public void setStartPos(short startPos) {
	this.startPos = startPos;
  }

  public void setEndPos(short endPos) {
	this.endPos = endPos;
  }

  public void setCycleCount(int cycleCount) {
	this.cycleCount = cycleCount;
  }
}
