package babysnoozer.events;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class LearnEvent {

    public long optStartPos;
    public long optEndPos;
    public long optReleaseWait;
    public long optDrawWait;
    public boolean hasInitValues = false;

    public LearnEvent(long startPos, long endPos,  long releaseWait, long drawWait) {
        this.optStartPos = startPos;
        this.optEndPos = endPos;
        this.optReleaseWait = releaseWait;
        this.optDrawWait = drawWait;
        hasInitValues = true;
    }

    public LearnEvent()
    {

    }

}
