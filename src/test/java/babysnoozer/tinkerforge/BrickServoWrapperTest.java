package babysnoozer.tinkerforge;

import com.tinkerforge.BrickServo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BrickServoWrapperTest {

  private BrickServoWrapper servoWrapper;

  @Mock BrickServo brickServo;

  @Before
  public void before() {
	servoWrapper = new BrickServoWrapper(0);
	servoWrapper.brickServo = brickServo;
  }

  @Test
  public void testEnable_NotInit_WillDriveLastPosition() throws Exception {
	//Given
	SnoozingBabyStateMachine.setStartPos((short) 100);

	//When
	servoWrapper.enable();

	//Then
	assertThat(servoWrapper.isDrivenLastPosition).isTrue();
	verify(brickServo, times(2)).enable((short) 0);
  }

  @Test
  public void testEnable_Init_WillNotDriveLastPosition() throws Exception {
	//Given
	SnoozingBabyStateMachine.setStartPos((short) 100);
	servoWrapper.isDrivenLastPosition = true;

	//When
	servoWrapper.enable();

	//Then
	verify(brickServo, times(1)).enable((short) 0);
  }
}