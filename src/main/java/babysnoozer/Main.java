package babysnoozer;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.events.*;
import com.tinkerforge.NotConnectedException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

public class Main implements Closeable {

    private static long snooze_display_time = 2000;
    private static long wait_after_release = 120000;
    private static long wait_after_draw = 1000;

    public static void main(String[] args) throws Exception {

        try (Main main = new Main()) {

            //InitBricks
            TinkerforgeSystem.initBricks();

            Properties programProperties;
            try {
                programProperties = new PropertiesLoader("program.properties", false).load();
                snooze_display_time = Long.valueOf(programProperties.getProperty("snooze_display_time", "2000"));
                wait_after_release = Long.valueOf(programProperties.getProperty("wait_after_release", "120000"));
                wait_after_draw = Long.valueOf(programProperties.getProperty("wait_after_draw", "2000"));
            } catch (IOException ignored)
            {}

            //Shows 3s Snoozing Baby
            EventBus.post(new DisplayTextEvent("Snoozing Baby"));

            // read the last position from stepper motor and write it to stepper API
            // that will be the starting point (TF inits with 0 commonly)
            int initialPositionRecall = 0;
            try {
                Properties loader = new PropertiesLoader("initialpositionrecall.properties", false).load();
                initialPositionRecall = Integer.valueOf(loader.getProperty("lastPosition", "800"));
            } catch (IOException ignored)
            {}
            TinkerforgeSystem.getStepper().setCurrentPosition(initialPositionRecall);
            Thread.sleep(snooze_display_time);

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
