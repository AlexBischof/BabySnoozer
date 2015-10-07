package babysnoozer.handlers;

/**
 * Created by Alexander Bischof on 30.01.15.
 */
public class RopeDistance {
    private long startPos;
    private long endPos;

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public long getPosBy(int percentage){
        if (percentage < 0 || percentage > 100){
            throw new IllegalArgumentException("percentage between 0 and 100 expected");
        }

        long distance = Math.abs(endPos-startPos);
        distance = percentage * distance /100;

        return (Math.min(endPos, startPos) + distance);
    }
}
