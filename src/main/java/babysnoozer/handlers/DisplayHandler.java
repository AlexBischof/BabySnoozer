package babysnoozer.handlers;

import babysnoozer.Event;
import babysnoozer.EventHandler;
import babysnoozer.events.AkkuEmptyEvent;
import babysnoozer.events.DisplayBrightnessEvent;
import babysnoozer.events.DisplayTextEvent;
import com.google.common.base.Strings;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import static babysnoozer.tinkerforge.SiekooAlphabet.character;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class DisplayHandler implements EventHandler {

  private static final int TIME_FOR_INACTIVATION_IN_MS = 5000;

  private final BrickletSegmentDisplay4x7 display;

  private String lastDisplayText = "";
  private short lastBrightness = DisplayBrightnessEvent.Brightness.FULL.getValue();

  public DisplayHandler(BrickletSegmentDisplay4x7 display4x7) {
	this.display = display4x7;
  }

  @Override public void handle(Event event) {

	Class<? extends Event> eventClass = event.getClass();

	try {
	  if (eventClass.equals(DisplayTextEvent.class)) {
		//Sets display to text
		DisplayTextEvent displayTextEvent = (DisplayTextEvent) event;
		displayText(displayTextEvent.getText());

	  } else if (eventClass.equals(AkkuEmptyEvent.class)) {
		//
		AkkuEmptyEvent akkuEmptyEvent = (AkkuEmptyEvent) event;
		displayText("Ak " + akkuEmptyEvent.getVoltage());
	  } else if (eventClass.equals(DisplayBrightnessEvent.class)) {
		//
		DisplayBrightnessEvent displayBrightnessEvent = (DisplayBrightnessEvent) event;
		lastBrightness = displayBrightnessEvent.getBrightness();
		displayText(lastDisplayText);
	  }
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }

  private void displayText(String text)
		  throws TimeoutException, NotConnectedException {

	//Handling for textsize < 4
	text = Strings.padStart(text, 4, ' ');

	short[] segments = {
			//TODO Errorhandling for text length lower
			character(text.charAt(0)),
			character(text.charAt(1)),
			character(text.charAt(2)),
			character(text.charAt(3))
	};

	  /*
	   * Logic to diffentiate between inactive text and active text events
	   */
	display.setSegments(segments, lastBrightness, false);
	this.lastDisplayText = text;

  }
}
