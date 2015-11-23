package babysnoozer.handlers;

import babysnoozer.events.AkkuEmptyEvent;
import babysnoozer.events.DisplayBrightnessEvent;
import babysnoozer.events.DisplayTextEvent;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import static babysnoozer.tinkerforge.SiekooAlphabet.character;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class DisplayHandler {

    private String lastDisplayText = "";
    private short lastBrightness = DisplayBrightnessEvent.Brightness.LOW.getValue();

    @Subscribe
    public void handleDisplayEvent(DisplayBrightnessEvent displayBrightnessEvent)
            throws TimeoutException, NotConnectedException {
        lastBrightness = displayBrightnessEvent.getBrightness();
        displayText(lastDisplayText);
    }

    @Subscribe
    public void handleDisplayEvent(DisplayTextEvent displayTextEvent) throws TimeoutException, NotConnectedException {
        displayText(displayTextEvent.getText());
    }

    @Subscribe
    public void handleAkkuEmptyEvent(AkkuEmptyEvent akkuEmptyEvent) throws TimeoutException, NotConnectedException {
        displayText("Batt");
    }

    private void displayText(String text)
            throws TimeoutException, NotConnectedException {

        //Handling for textsize < 4
        text = Strings.padStart(text, 4, ' ');

        short[] segments = {
                character(text.charAt(0)),
                character(text.charAt(1)),
                character(text.charAt(2)),
                character(text.charAt(3))
        };

	  /*
       * Logic to diffentiate between inactive text and active text events
	   */
        TinkerforgeSystem.getDisplay4x7().setSegments(segments, lastBrightness, false);
        this.lastDisplayText = text;
    }
}
