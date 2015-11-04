package babysnoozer.handlers;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

public class DeadEventHandler {

    @Subscribe
    public void handleDeadEvent(DeadEvent deadEvent) {
        System.out.println("An event occurred without a handler" + deadEvent.toString());
    }
}