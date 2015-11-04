package babysnoozer.handlers;

import babysnoozer.events.LogEvent;
import com.google.common.eventbus.Subscribe;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class LogHandler {

    @Subscribe
    public void handle(LogEvent logEvent) {
        System.out.println(logEvent.getText());
    }
}
