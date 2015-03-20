package babysnoozer.handlers.commands;

import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

import static babysnoozer.tinkerforge.BrickServoWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickServoWrapper.Velocity;
import static org.assertj.core.api.Assertions.assertThat;

public class CycleQueueTest {

  private CycleQueue cycleQueue;

  @Before
  public void before() {
	cycleQueue = new CycleQueue();
  }

  @Test(expected = NoSuchElementException.class)
  public void testNextCommand_Empty_WillThrowNoSuchElementException() throws Exception {
	assertThat(cycleQueue.nextCommand()).isNull();
  }

  @Test
  public void testNextCommand_OneCycleOneCommand_WillGetCommand() {
	//Given
	WaitCommand command1 = new WaitCommand(50);
	cycleQueue.add(new Cycle().addCommand(command1));

	//when
	Command command = cycleQueue.nextCommand();

	//then
	assertThat(command).isEqualTo(command1);
  }

  @Test
  public void testNextCommand_TwoCycleOneCommandEach_WillGetBothCommands() {
	//Given
	WaitCommand command1 = new WaitCommand(50);
	PositionCommand positionCommand = new PositionCommand(50, Velocity.learn, Acceleration.learn);
	cycleQueue.add(new Cycle().addCommand(command1));
	cycleQueue.add(new Cycle().addCommand(positionCommand));

	//when
	cycleQueue.nextCommand();
	Command command = cycleQueue.nextCommand();

	//then
	assertThat(command).isEqualTo(positionCommand);
  }
}