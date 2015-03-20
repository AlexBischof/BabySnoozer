package babysnoozer.handlers.commands;

import java.util.LinkedList;

/**
 * Created by Alexander Bischof on 10.03.15.
 */
public class CycleQueue extends LinkedList<Cycle> {

  public Command nextCommand() {

	Cycle cycle = this.getFirst();

	Command command = cycle.nextCommand();

	if (command == null) {
	  this.removeFirst();

	  cycle = this.getFirst();
	  if (cycle != null) {
		command = cycle.nextCommand();
	  } else {
		command = null;
	  }
	}

	return command;
  }
}

