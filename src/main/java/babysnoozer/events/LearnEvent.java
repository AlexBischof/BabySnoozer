package babysnoozer.events;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class LearnEvent {

    public int optStartPos;
    public int optEndPos;
    public long optReleaseWait;
    public long optDrawWait;
    public boolean hasInitValues = false;

    public LearnEvent(int startPos, int endPos,  long releaseWait, long drawWait) {
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
