package babysnoozer.handlers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RopeDistanceTest {

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
	return Arrays.asList(new Object[][] {
			{ 0, 100, 50, 50 },   //start 0, ende 100, 50 percentage, pos 50
			{ -100, 100, 50, 0 },   //start -100, ende 100, 50 percentage, pos 0
			{ 100, -100, 50, 0 },   //start 100, ende -100, 50 percentage, pos 0
	});
  }

  private RopeDistance ropeDistance;

  private int percentage;
  private short expectedPos;

  public RopeDistanceTest(int startPos, int endPos, int percentage, int expectedPos) {
	this.ropeDistance = new RopeDistance();
	this.ropeDistance.setStartPos((short) startPos);
	this.ropeDistance.setEndPos((short) endPos);
	this.percentage = percentage;
	this.expectedPos = (short) expectedPos;
  }

  @Test
  public void testGetPosBy() throws Exception {
	assertThat(ropeDistance.getPosBy(percentage)).isEqualTo(expectedPos);
  }
}