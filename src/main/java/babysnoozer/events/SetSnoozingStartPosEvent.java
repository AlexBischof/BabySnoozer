package babysnoozer.events;

public class SetSnoozingStartPosEvent {
    private final long startPos;

    public SetSnoozingStartPosEvent(long startPos) {
        this.startPos = startPos;
    }

    public long getStartPos() {
        return startPos;
    }
}

