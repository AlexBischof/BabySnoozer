package babysnoozer.handlers.commands;

/**
 * Created by Alexander Bischof on 20.03.15.
 */
public class CycleCreator {

  public CycleQueue create(CycleCreationParam cycleCreationParam) {

	CycleQueue cycleQueue = new CycleQueue();

    /*
     * Creates commands per cycle
     */
	for (int i = 0; i < cycleCreationParam.getCycleCount(); i++) {

	  Cycle cycle = new Cycle();
	  cycleQueue.add(cycle);

	  //Draw command
	  cycle.addCommand(new PositionCommand(cycleCreationParam.getEndPos(), cycleCreationParam.getDrawVelocity(),
	                                       cycleCreationParam.getDrawAcceleration()));

	  //Draw-Wait
	  cycle.addCommand(new WaitCommand(cycleCreationParam.getDrawWait()));

	  //Release
	  cycle.addCommand(new PositionCommand(cycleCreationParam.getStartPos(), cycleCreationParam.getReleaseVelocity(),
	                                       cycleCreationParam.getReleaseAcceleration()));

	  //Release-Wait
	  cycle.addCommand(new WaitCommand(cycleCreationParam.getReleaseWait()));
	}

	return cycleQueue;
  }
}
