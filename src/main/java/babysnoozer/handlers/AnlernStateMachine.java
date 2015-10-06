package babysnoozer.handlers;

import babysnoozer.events.*;
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


public class AnlernStateMachine {

    private static final int ANLERN_TRIGGER_TIME_IN_MS = 2000;

    private int rotiCountForSnoozing = 0;

    public enum State {
        Null, Init, StartPos, EndPos, DrawTime, ReleaseTime, Finished
    }

    private State state = State.Null;

    private State setNextState()
    {
        return this.state.ordinal() < State.values().length - 1
                ? State.values()[state.ordinal() + 1]
                : null;
    }

    private LearnCycle startPosCycle = new LearnCycle(100, 10, 100);
    private LearnCycle endPosCycle = new LearnCycle(100, 10, 100);
    private LearnCycle drawTimeCycle = new LearnCycle(100, 100);
    private LearnCycle releaseTimeCycle = new LearnCycle(1, 1);

    private int initStartPos;
    private int initEndPos;
    private long initDrawWait;
    private long initReleaseWait;

    @Subscribe
    @AllowConcurrentEvents
    public void handleRotiPressEvent(RotiPressEvent rotiPressEvent) throws TimeoutException, NotConnectedException {

        if (isDisabled()) {
            return;
        }

        if (state.equals(State.EndPos)) {
            handleRotiPressEventForEndPos();
            setNextState();
        } else if (state.equals(State.DrawTime)) {
            handleRotiPressEventForDrawWait();
            setNextState();
        } else if (state.equals(State.ReleaseTime)) {
            handleRotiPressEventForReleaseWait();
            setNextState();
        }
        else if (state.equals(State.Finished)) {
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

        else
        {
            throw new RuntimeException("State " + state + " is not known in handleRotiPressEvent");
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
            setNextState();
            handleRotiPressEventForInitState();
            setNextState();
            handleRotiPressEventForStartPos();
            setNextState();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRotiCountEvent(RotiCountEvent rotiCountEvent) {

        if (isDisabled()) {
            return;
        }

        if (this.state == State.StartPos) {
            this.startPosCycle.setRotiValue(rotiCountEvent.getCount());
        }
        else if (this.state == State.EndPos) {
            this.endPosCycle.setRotiValue(rotiCountEvent.getCount());
        }
        else if (this.state == State.DrawTime) {
            this.drawTimeCycle.setRotiValue(rotiCountEvent.getCount());
        }
        else if (this.state == State.ReleaseTime) {
            this.releaseTimeCycle.setRotiValue(rotiCountEvent.getCount());
        }
        else {
            this.rotiCountForSnoozing = rotiCountForSnoozing + rotiCountEvent.getCount();

            if (this.rotiCountForSnoozing < 0) {
                try {
                    TinkerforgeSystem.getRoti().getCount(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.rotiCountForSnoozing = 0;
            }
            else if (this.rotiCountForSnoozing > 10) {
                this.rotiCountForSnoozing = 10;
                try {
                    TinkerforgeSystem.getRoti().getCount(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                SnoozingBabyStateMachine.setCycleCount(this.rotiCountForSnoozing);
                EventBus.post(new DisplayTextEvent(String.valueOf(rotiCountEvent.getCount())));
            }
        }
    }

    private void handleRotiPressEventForInitState()
            throws TimeoutException, NotConnectedException {
        EventBus.post(new DisplayBrightnessEvent(DisplayBrightnessEvent.Brightness.FULL.getValue()));
        EventBus.post(new DisplayTextEvent("Learn"));
    }

    private void handleRotiPressEventForStartPos() throws TimeoutException, NotConnectedException {
        {
            new Thread(() -> {
                try {
                    EventBus.post(new DisplayTextEvent("SetS"));
                    Thread.sleep(2000l);
                    startPosCycle.setInitLearnValue(initStartPos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ).start();
        }
    }

    private void handleRotiPressEventForEndPos() {
        {
            new Thread(() -> {
                try {
                    EventBus.post(new DisplayTextEvent("SetE"));
                    Thread.sleep(2000l);
                    endPosCycle.setInitLearnValue(initEndPos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ).start();
        }
    }

    private void handleRotiPressEventForDrawWait() {
        {
            new Thread(() -> {
                try {
                    EventBus.post(new DisplayTextEvent("SetD"));
                    Thread.sleep(2000l);
                    drawTimeCycle.setInitLearnValue(initDrawWait);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ).start();
        }
    }

    private void handleRotiPressEventForReleaseWait() {
        {
            new Thread(() -> {
                try {
                    EventBus.post(new DisplayTextEvent("SetD"));
                    Thread.sleep(2000l);
                    releaseTimeCycle.setInitLearnValue(initReleaseWait);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ).start();
        }
    }

    private boolean isDisabled() {
        return !Arrays.asList(SetCycleCount, Null).contains(SnoozingBabyStateMachine.getState());
    }
}
