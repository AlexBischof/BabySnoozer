package babysnoozer.tinkerforge;
import com.tinkerforge.*;

public class RedBrickWrapper {

    private static final String BRICK_UID = "3df9xg";

    public BrickRED brickRED;

    public RedBrickWrapper() {
    }

    public void disableStatusLED() {

//        try {
//            this.brickRED.
//        } catch (TimeoutException | NotConnectedException e) {
//            e.printStackTrace();
//        }
    }

    public void initBrick(IPConnection ipconnection) {
        this.brickRED = new BrickRED(BRICK_UID, ipconnection);
    }
}
