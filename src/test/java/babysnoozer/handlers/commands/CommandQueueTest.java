package babysnoozer.handlers.commands;

import babysnoozer.tinkerforge.BrickServoWrapper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandQueueTest {

  private CommandQueue commandQueue;

  @Before
  public void before() {
	commandQueue = new CommandQueue();
  }

  @Test
  public void testAddPositionCommand() {
	commandQueue.add(new PositionCommand(100, BrickServoWrapper.Velocity.learn, BrickServoWrapper.Acceleration.learn));

	assertThat(commandQueue).hasSize(1);
  }

  @Test
  public void testAddWaitCommand() {
	commandQueue.add(new WaitCommand(1000));

	assertThat(commandQueue).hasSize(1);
  }
}