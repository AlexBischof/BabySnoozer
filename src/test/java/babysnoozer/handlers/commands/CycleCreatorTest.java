package babysnoozer.handlers.commands;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CycleCreatorTest {

  private CycleCreator cycleCreator;

  @Before
  public void before() {
	cycleCreator = new CycleCreator();
  }

  @Test
  public void testCreate() throws Exception {

	//Given
	//when
	List<Cycle> cycles = cycleCreator.create(new CycleCreationParam(2, 50l, 50l, 0, 100));

	//then
	assertThat(cycles).hasSize(2);

    Cycle cycle = cycles.get(0);
    assertThat(cycle.commandQueue).hasSize(5);

  }
}