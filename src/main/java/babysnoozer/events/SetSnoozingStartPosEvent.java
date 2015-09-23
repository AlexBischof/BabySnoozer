package babysnoozer.events;

public class SetSnoozingStartPosEvent {
    private final int startPos;

    public SetSnoozingStartPosEvent(int startPos) {
        this.startPos = startPos;
    }

    public int getStartPos() {
        return startPos;
    }
}

