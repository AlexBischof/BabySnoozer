package babysnoozer.handlers;

/**
 * Created by Alexander Bischof on 30.01.15.
 */
public class RopeDistance {
  private short startPos;
  private short endPos;

  public short getStartPos() {
	return startPos;
  }

  public void setStartPos(short startPos) {
	this.startPos = startPos;
  }

  public short getEndPos() {
	return endPos;
  }

  public void setEndPos(short endPos) {
	this.endPos = endPos;
  }

  public short getPosBy(int percentage){
    if (percentage < 0 || percentage > 100){
      throw new IllegalArgumentException("percentage between 0 and 100 expected");
    }

    int distance = Math.abs(endPos-startPos);
    distance = percentage * distance /100;

    return (short) (Math.min(endPos, startPos) + distance);
  }
}
