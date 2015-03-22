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

	  //Wait
	  cycle.addCommand(new WaitCommand(cycleCreationParam.getReleaseWait()));

	  //Draw command
	  cycle.addCommand(new PositionCommand(cycleCreationParam.getEndPos(), cycleCreationParam.getDrawVelocity(),
	                                       cycleCreationParam.getDrawAcceleration()));

	  //Wait
	  cycle.addCommand(new WaitCommand(cycleCreationParam.getReleaseWait()));

	  //Release
	  cycle.addCommand(new PositionCommand(cycleCreationParam.getStartPos(), cycleCreationParam.getReleaseVelocity(),
	                                       cycleCreationParam.getReleaseAcceleration()));
	}

	return cycleQueue;
  }
}
