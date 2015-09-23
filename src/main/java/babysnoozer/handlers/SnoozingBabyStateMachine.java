package babysnoozer.handlers;

import babysnoozer.handlers.commands.CycleQueue;

public enum SnoozingBabyStateMachine {

    SnoozingBabyStateMachine;

    public enum State {
        Null, SetCycleCount, Snooze, ShutDown
    }

    private State state = State.Null;

    private RopeDistance ropeDistance;
    private int cycleCount = 1;
    private CycleQueue cycles;

    public void setReleaseWaitTime(long releaseWaitTime) {
        this.releaseWaitTime = releaseWaitTime;
    }

    public long getReleaseWaitTime() {
        return releaseWaitTime;
    }

    private long releaseWaitTime = 120000l;

    public State getState() {
        return state;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
    }

    public int getStartPos() {
        return getRopeDistance().getStartPos();
    }

    public RopeDistance getRopeDistance() {
        if (ropeDistance == null) {
            ropeDistance = new RopeDistance();
        }
        return ropeDistance;
    }

    public CycleQueue getCycles() {
        return cycles;
    }

    public void setCycles(CycleQueue cycles) {
        this.cycles = cycles;
    }

    public void setStartPos(int startPos) {
        getRopeDistance().setStartPos(startPos);
    }

    public int getEndPos() {
        return getRopeDistance().getEndPos();
    }

    public void setEndPos(int endPos) {
        getRopeDistance().setEndPos(endPos);
    }
}
