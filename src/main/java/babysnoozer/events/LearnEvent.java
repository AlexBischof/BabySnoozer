package babysnoozer.events;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class LearnEvent {

    public int optStartPos;
    public int optEndPos;
    public long optReleaseWait;
    public boolean hasInitValues = false;

    public LearnEvent(int startPos, int endPos,  long releaseWait) {
        optStartPos = startPos;
        optEndPos = endPos;
        optReleaseWait = releaseWait;
        hasInitValues = true;
    }

    public LearnEvent()
    {

    }

}
