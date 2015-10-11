package babysnoozer.handlers;

import babysnoozer.events.*;
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

    public enum State {
        Null, Init, StartPos, EndPos, DrawTime, ReleaseTime, Finished
    }

    private State state = State.Null;

    private void setNextState()
    {
        this.state = this.state.ordinal() < State.values().length - 1
                ? State.values()[state.ordinal() + 1]
                : null;
    }

    private LearnCycle startPosCycle = new LearnCycle(100, 10, 100, -10000, 10000);
    private LearnCycle endPosCycle = new LearnCycle(100, 10, 100, -10000, 10000);
    private LearnCycle drawTimeCycle = new LearnCycle(100, 100, 0, 3000);
    private LearnCycle releaseTimeCycle = new LearnCycle(1000, 1, 0, 180000);

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

        setNextState();

        if (state.equals(State.Null)) {
            // do nothing, this is handled by learn event
        }
        else if (state.equals(State.EndPos)) {
            handleRotiPressEventForEndPos();
        } else if (state.equals(State.DrawTime)) {
            handleRotiPressEventForDrawWait();
        } else if (state.equals(State.ReleaseTime)) {
            handleRotiPressEventForReleaseWait();
        }
        else if (state.equals(State.Finished)) {
            EventBus.post(new DisplayTextEvent("End"));
            EventBus.post(new SetSnoozingStartPosEvent((int)startPosCycle.getLearnValue()));
            EventBus.post(new SetSnoozingEndPosEvent((int)endPosCycle.getLearnValue()));
            EventBus.post(new SetSnoozingDrawWaitTimeEvent(drawTimeCycle.getLearnValue()));
            EventBus.post(new SetSnoozingReleaseWaitTimeEvent(releaseTimeCycle.getLearnValue()));
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

        if (state.equals(State.Null))
            setNextState();

        if (state.equals(State.Init)) {
            handleRotiPressEventForInitState();
            setNextState();
            handleRotiPressEventForStartPos();
        }
    }

    @Subscribe
    // @AllowConcurrentEvents
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
            if (SnoozingBabyStateMachine.getState() == SetCycleCount) {
                int currCycleCount = SnoozingBabyStateMachine.getCycleCount();
                currCycleCount = currCycleCount + rotiCountEvent.getCount();

                if (currCycleCount < 0)
                    currCycleCount = 0;
                else if (currCycleCount > 10)
                    currCycleCount = 10;

                SnoozingBabyStateMachine.setCycleCount(currCycleCount);

                try {
                    TinkerforgeSystem.getRoti().getCount(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EventBus.post(new DisplayTextEvent(String.valueOf(currCycleCount)));
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
                    EventBus.post(new DisplayTextEvent("Draw"));
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
                    EventBus.post(new DisplayTextEvent("Rele"));
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
