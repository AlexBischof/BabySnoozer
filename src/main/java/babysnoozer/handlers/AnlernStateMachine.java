package babysnoozer.handlers;

import babysnoozer.events.RotiPressEvent;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class AnlernStateMachine {
  public enum State {
	Null, Init, Run, Post;

    public void next(RotiPressEvent rotiPressEvent) {

    }
  }

  private State state;

  public AnlernStateMachine() {
	state = State.Null;
  }

  public State getState() {
	return state;
  }
}
