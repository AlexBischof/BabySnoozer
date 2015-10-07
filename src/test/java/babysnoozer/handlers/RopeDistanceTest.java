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
  private long expectedPos;

  public RopeDistanceTest(long startPos, long endPos, int percentage, long expectedPos) {
	this.ropeDistance = new RopeDistance();
	this.ropeDistance.setStartPos((int) startPos);
	this.ropeDistance.setEndPos((int) endPos);
	this.percentage = percentage;
	this.expectedPos = (int) expectedPos;
  }

  @Test
  public void testGetPosBy() throws Exception {
	assertThat(ropeDistance.getPosBy(percentage)).isEqualTo(expectedPos);
  }
}