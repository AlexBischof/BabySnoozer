package babysnoozer;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.events.DisplayTextEvent;
import babysnoozer.events.InitSnoozingStateEvent;
import babysnoozer.events.LearnEvent;
import babysnoozer.events.ShutdownEvent;
import com.tinkerforge.NotConnectedException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

public class Main implements Closeable {

    public static void main(String[] args) throws Exception {

        try (Main main = new Main()) {

            //InitBricks
            TinkerforgeSystem.initBricks();

            Properties programProperties;
            programProperties = new PropertiesLoader("program.properties").load();
            long snooze_display_time = Long.valueOf(programProperties.getProperty("snooze_display_time", "2000"));

            //Shows 3s Snoozing Baby
            EventBus.post(new DisplayTextEvent("Snoozing Baby"));

            // read the last position from stepper motor and write it to stepper API
            // that will be the starting point (TF inits with 0 commonly)
            PropertiesLoader propertiesLoader = new PropertiesLoader("initialpositionrecall.properties", false);
            propertiesLoader.createIfNotExist();
            Properties initialPositionRecallProperties = propertiesLoader.load();
            int initialPositionRecall = Integer
                .valueOf(initialPositionRecallProperties.getProperty("lastPosition", "0"));
            TinkerforgeSystem.getStepper().setCurrentPosition(initialPositionRecall);
            Thread.sleep(snooze_display_time);

            PropertiesLoader cycleConfigPropertiesLoader = new PropertiesLoader("cycleconfig.properties", false);
            if (cycleConfigPropertiesLoader.createIfNotExist()) {
                System.out.println("cycleconfig.properties not found. Starting learning");
                EventBus.post(new LearnEvent());
            } else {
                Properties cycleConfigProperties = cycleConfigPropertiesLoader.load();

                SnoozingBabyStateMachine
                        .setStartPos(Integer.valueOf(cycleConfigProperties.getProperty("startPos")));
                SnoozingBabyStateMachine.setEndPos(Integer.valueOf(cycleConfigProperties.getProperty("endPos")));
                SnoozingBabyStateMachine
                        .setCycleCount(Integer.valueOf(cycleConfigProperties.getProperty("cycleCount")));
                SnoozingBabyStateMachine
                        .setDrawWaitTime(Long.valueOf(cycleConfigProperties.getProperty("wait_after_draw")));
                SnoozingBabyStateMachine
                        .setReleaseWaitTime(Long.valueOf(cycleConfigProperties.getProperty("wait_after_release")));

                EventBus.post(new InitSnoozingStateEvent());
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
