package babysnoozer.handlers.commands;

import babysnoozer.tinkerforge.BrickStepperWrapper;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CycleTest {

    private Cycle cycle;

    @Before
    public void before() {
        cycle = new Cycle();
    }

    @Test
    public void testNextCommand_NoCommand_WillReturnNull() throws Exception {
        assertThat(cycle.nextCommand()).isNull();
    }

    @Test
    public void testNextCommand_OneCommand_WillReturnCommand() throws Exception {
        WaitCommand command = new WaitCommand(50);
        cycle.addCommand(command);
        assertThat(cycle.nextCommand()).isEqualTo(command);
    }

    @Test
    public void testNextCommand_MultipleCommands_WillReturnFirstAddedCommand() throws Exception {
        WaitCommand command = new WaitCommand(50);
        PositionCommand positionCommand = new PositionCommand(
                10,
                BrickStepperWrapper.Velocity.learn,
                BrickStepperWrapper.Acceleration.acc_learn,
                BrickStepperWrapper.Acceleration.deacc_learn);
        cycle.addCommand(command);
        cycle.addCommand(positionCommand);
        assertThat(cycle.nextCommand()).isEqualTo(command);
    }
}