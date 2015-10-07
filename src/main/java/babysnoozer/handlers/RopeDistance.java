package babysnoozer.handlers;

/**
 * Created by Alexander Bischof on 30.01.15.
 */
public class RopeDistance {
    private int startPos;
    private int endPos;

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public int getPosBy(int percentage){
        if (percentage < 0 || percentage > 100){
            throw new IllegalArgumentException("percentage between 0 and 100 expected");
        }

        long distance = Math.abs(endPos-startPos);
        distance = percentage * distance /100;

        return (int)(Math.min(endPos, startPos) + distance);
    }
}
