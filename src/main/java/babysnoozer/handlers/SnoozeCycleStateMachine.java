package babysnoozer.handlers;

/**
 * Created by Alexander Bischof on 30.01.15.
 */
public enum SnoozeCycleStateMachine {

  SnoozeCycleStateMachine;

  public enum State {
	Draw, Release
  }

  private State targetState;
  private int cyclesLeft;

  public State getTargetState() {
	return targetState;
  }

  public void setTargetState(State targetState) {
	this.targetState = targetState;
  }

  public int getCyclesLeft() {
	return cyclesLeft;
  }

  public void setCyclesLeft(int cyclesLeft) {
	this.cyclesLeft = cyclesLeft;
  }

  public void decreaseCyclesLeft() {
	cyclesLeft--;
  }
}
