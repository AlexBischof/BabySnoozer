package babysnoozer.handlers.commands;

/**
 * Created by Alexander Bischof on 20.03.15.
 */
public class Cycle {
  final CommandQueue commandQueue;

  public Cycle() {
	this.commandQueue = new CommandQueue();
  }

  public Command nextCommand() {
	return commandQueue.poll();
  }

  public Cycle addCommand(Command command) {
	this.commandQueue.add(command);
	return this;
  }

  @Override public String toString() {
	return "Cycle{" +
	       "commandQueue=" + commandQueue +
	       '}';
  }
}
