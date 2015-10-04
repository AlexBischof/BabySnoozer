package babysnoozer.handlers;

import babysnoozer.events.*;
import babysnoozer.handlers.commands.LearnCycle;
import babysnoozer.tinkerforge.BrickStepperWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.util.Arrays;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.handlers.SnoozingBabyStateMachine.SnoozingBabyStateMachine;
import static babysnoozer.handlers.SnoozingBabyStateMachine.State.Null;
import static babysnoozer.handlers.SnoozingBabyStateMachine.State.SetCycleCount;
import static babysnoozer.tinkerforge.BrickStepperWrapper.Acceleration;
import static babysnoozer.tinkerforge.BrickStepperWrapper.Velocity;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class AnlernStateMachine {

    private static final int ANLERN_TRIGGER_TIME_IN_MS = 1000;

    public enum State {
        Null, Init, StartPos, EndPos, DrawTime, ReleaseTime
    }

    private State state = State.Null;

    private State setNextState()
    {
        return this.state.ordinal() < State.values().length - 1
                ? State.values()[state.ordinal() + 1]
                : null;
    }

    private LearnCycle startPosCycle = new LearnCycle(100, 10);
    private LearnCycle endPosCycle = new LearnCycle(100, 10);
    private LearnCycle drawTimeCycle = new LearnCycle(100, 100);
    private LearnCycle releaseTimeCycle = new LearnCycle(1, 1);

    private int initStartPos;
    private int initEndPos;
    private long initReleaseWait;
    private long initDrawWait;

    private int currPos = 0;

    @Subscribe
    @AllowConcurrentEvents
    public void handleRotiPressEvent(RotiPressEvent rotiPressEvent) throws TimeoutException, NotConnectedException {

        if (isDisabled()) {
            return;
        }

        if (state.equals(State.Null)) {
            if (rotiPressEvent.getPressedLengthInMs() > ANLERN_TRIGGER_TIME_IN_MS) {
                setNextState();
                handleRotiPressEventForNullState();
            }
        } else if (state.equals(State.StartPos)) {
            setNextState();
            handleRotiPressEventForStartPos();
        } else if (state.equals(State.EndPos)) {
            setNextState();
            handleRotiPressEventForEndPos();
        } else if (state.equals(State.DrawTime)) {
            setNextState();
            handleRotiPressEventForDrawWait();
        } else if (state.equals(State.ReleaseTime)) {
            setNextState();
            handleRotiPressEventForReleaseWait();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleLearnEvent(LearnEvent learnEvent) throws TimeoutException, NotConnectedException {

        if (isDisabled()) {
            return;
        }

        if (learnEvent.hasInitValues) {
            this.initStartPos = learnEvent.optStartPos;
            this.initEndPos = learnEvent.optEndPos;
            this.initReleaseWait = learnEvent.optReleaseWait;
            this.initDrawWait = learnEvent.optDrawWait;
        } else {
            // set initial values
            this.initStartPos = 0;
            this.initEndPos = 0;
            this.initDrawWait = 1000l;  // ms
            this.initReleaseWait = 120000l;  // s
        }

        if (state.equals(State.Null)) {
            handleRotiPressEventForNullState();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRotiCountEvent(RotiCountEvent rotiCountEvent) {

        if (isDisabled()) {
            return;
        }

        boolean acceptRotiCountEvent = Arrays.asList(
                State.StartPos,
                State.EndPos,
                State.DrawTime,
                State.ReleaseTime).contains(state);
        if (acceptRotiCountEvent) {

            int count = rotiCountEvent.getCount();
            try {
                // reset roti
                TinkerforgeSystem.getRoti().getCount(/*reset*/ true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //TODO binden an properties
            this.currPos = fireCount(count);

            EventBus.post(new SetStepperPosEvent(
                    this.currPos,
                    Velocity.learn,
                    Acceleration.acc_learn,
                    Acceleration.deacc_learn));
        } else {
            int count = rotiCountEvent.getCount();

            if (count > 0 && count <= 10) {
                SnoozingBabyStateMachine.setCycleCount(count);
                EventBus.post(new DisplayTextEvent(String.valueOf(rotiCountEvent.getCount())));
            }
        }
    }

    private void handleRotiPressEventForNullState()
            throws TimeoutException, NotConnectedException {
        EventBus.post(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.FULL.getValue()));
        EventBus.post(new DisplayTextEvent("Learn"));
        setNextState();
    }

    private void handleRotiPressEventForStartPos() throws TimeoutException, NotConnectedException {
        {
            //Setzt learn velocity
            BrickStepperWrapper stepper = TinkerforgeSystem.getStepper();
            stepper.setVelocity(Velocity.learn);
            stepper.setAcceleration(Acceleration.acc_learn, Acceleration.deacc_learn);

            //Nach 2 Sekunden Anzeige
            new Thread(() -> {
                try {
                    Thread.sleep(2000l);
                    EventBus.post(new DisplayTextEvent("SetS"));

                    //Statuswechsel
                    AnlernStateMachine.this.state = AnlernStateMachine.State.StartPos;

                    // reset roti
                    TinkerforgeSystem.getRoti().getCount(/*reset*/ true);
                    Thread.sleep(1000l);
                    currPos = initStartPos;
                    EventBus.post(new DisplayTextEvent(String.valueOf(initStartPos)));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ).start();
        }
    }

    private void handleRotiPressEventForEndPos() {
    }

    private void handleRotiPressEventForDrawWait() {
    }

    private void handleRotiPressEventForReleaseWait() {
    }

    private void handleRotiPressEventForStartAndEndPos()
            throws TimeoutException, NotConnectedException {
        if (state.equals(State.StartPos)) {
            this.state = State.EndPos;
            EventBus.post(new DisplayTextEvent("SetE"));
            EventBus.post(new SetSnoozingStartPosEvent(TinkerforgeSystem.getStepper().getCurrentPosition()));
            this.currPos = initEndPos;
        } else if (state.equals(State.EndPos)) {
            EventBus.post(new DisplayTextEvent("End"));
            EventBus.post(new SetSnoozingEndPosEvent(TinkerforgeSystem.getStepper().getCurrentPosition()));
            EventBus.post(new SetStepperPosEvent(
                    SnoozingBabyStateMachine.getStartPos(),
                    Velocity.learn,
                    Acceleration.acc_learn,
                    Acceleration.deacc_learn));
            this.state = State.Null;

            EventBus.post(new InitSnoozingStateEvent());
        }
    }



    private boolean isDisabled() {
        return !Arrays.asList(SetCycleCount, Null).contains(SnoozingBabyStateMachine.getState());
    }

    private int fireCount(int count) {
        //TODO refac to config
        int zaehlerwert = this.currPos + 100 * count;
        zaehlerwert = Math.min(10000, Math.max(-10000, zaehlerwert));
        EventBus.post(new DisplayTextEvent(String.valueOf(zaehlerwert / 10)));
        return zaehlerwert;
    }
}
