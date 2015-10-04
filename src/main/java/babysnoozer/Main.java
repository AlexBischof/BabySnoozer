package babysnoozer;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.events.*;
import com.tinkerforge.NotConnectedException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.tinkerforge.BrickStepperWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickStepperWrapper.Velocity;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class Main implements Closeable {

    //TODO refac to conf file
    private static final long SHOW_SNOOZING_BABY_IN_MS = 2000l;

    public static void main(String[] args) throws Exception {

        try (Main main = new Main()) {

            //InitBricks
            TinkerforgeSystem.initBricks();

            //Shows 3s Snoozing Baby
            EventBus.post(new DisplayTextEvent("Snoozing Baby"));

            // TODO: Read first position from last shutdown (file access)
            // It should be set here with velocity and acc max
            // this overwrites the brick firmware init value of 0 immediately
            // (without moving)
            int initialPositionRecall = 0;
            try {
                Properties loader = new PropertiesLoader("initialpositionrecall.properties", false).load();
                initialPositionRecall = Integer.valueOf(loader.getProperty("lastPosition", "800"));
            } catch (IOException e)
            {}
            TinkerforgeSystem.getStepper().setCurrentPosition(initialPositionRecall);
            Thread.sleep(SHOW_SNOOZING_BABY_IN_MS);

            try {
                Properties stepperConfigProperties = new PropertiesLoader("cycleconfig.properties", false).load();

                SnoozingBabyStateMachine.setStartPos(Integer.valueOf(stepperConfigProperties.getProperty("startPos")));
                SnoozingBabyStateMachine.setEndPos(Integer.valueOf(stepperConfigProperties.getProperty("endPos")));

                SnoozingBabyStateMachine.setCycleCount(Integer.valueOf(stepperConfigProperties.getProperty("cycleCount")));

                EventBus.post(new InitSnoozingStateEvent());
            } catch (IOException e) {

                System.out.println("cycleconfig.properties not found. Starting learning");
                EventBus.post(new LearnEvent());
            }

            //Shows default cycle value
            System.out.println("Ready for Snooze");
            System.in.read();
        }
    }

    @Override public void close() throws IOException {
        EventBus.post(new ShutdownEvent());

        try {
            TinkerforgeSystem.getIpconnection().disconnect();
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
