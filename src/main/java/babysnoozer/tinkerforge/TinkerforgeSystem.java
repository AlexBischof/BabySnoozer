package babysnoozer.tinkerforge;

import babysnoozer.events.LogEvent;
import babysnoozer.events.RotiCountEvent;
import babysnoozer.listeners.RotiListener;
import com.tinkerforge.*;

import static babysnoozer.EventBus.EventBus;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public enum TinkerforgeSystem {

    TinkerforgeSystem;

    private IPConnection ipconnection;
    private BrickStepperWrapper stepperWrapper;
    private RedBrickWrapper redBrickWrapper;
    private BrickletSegmentDisplay4x7 display4x7;
    private BrickletRotaryEncoder roti;

    public void initBricks() throws Exception {
        EventBus.post(new LogEvent("Initbricks"));

        //TODO automatische Erkennung

        ipconnection = new IPConnection();

        stepperWrapper = new BrickStepperWrapper();
        stepperWrapper.initBrick(ipconnection);

        redBrickWrapper = new RedBrickWrapper();
        redBrickWrapper.initBrick(ipconnection);

        display4x7 = new BrickletSegmentDisplay4x7("pPJ", ipconnection);

        ipconnection.connect("localhost", 4223);

        initRoti();

        stepperWrapper.configStepper();
    }

    private void initRoti() throws TimeoutException, NotConnectedException {
        roti = new BrickletRotaryEncoder("kGs", ipconnection);

        roti.setCountCallbackPeriod(100l);

        RotiListener rotiListener = new RotiListener();
        roti.addPressedListener(rotiListener);
        roti.addReleasedListener(rotiListener);

        roti.addCountListener(count -> EventBus.post(new RotiCountEvent(count)));
    }

    public IPConnection getIpconnection() {
        return ipconnection;
    }

    public BrickStepperWrapper getStepper() {
        return stepperWrapper;
    }

    public RedBrickWrapper getRedBrick() {
        return redBrickWrapper;
    }

    public BrickletSegmentDisplay4x7 getDisplay4x7() {
        return display4x7;
    }

    public BrickletRotaryEncoder getRoti() {
        return roti;
    }

}
