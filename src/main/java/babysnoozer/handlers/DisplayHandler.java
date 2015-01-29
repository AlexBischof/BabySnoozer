package babysnoozer.handlers;

import babysnoozer.events.AkkuEmptyEvent;
import babysnoozer.events.DisplayTextEvent;
import babysnoozer.tinkerforge.SiekooAlphabet;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import babysnoozer.Event;
import babysnoozer.EventBus;
import babysnoozer.EventHandler;

import static babysnoozer.tinkerforge.SiekooAlphabet.*;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class DisplayHandler implements EventHandler {

  private static final int TIME_FOR_INACTIVATION_IN_MS = 5000;
  private final BrickletSegmentDisplay4x7 display;

  private final static short FULL_BRIGHTNESS = 7;
  private final static short LOW_BRIGHTNESS = 0;

  private long lastDisplayText = System.currentTimeMillis();

  public DisplayHandler(BrickletSegmentDisplay4x7 display4x7) {
	this.display = display4x7;
  }

  @Override public void handle(Event event) {

	Class<? extends Event> eventClass = event.getClass();

	try {
	  if (eventClass.equals(DisplayTextEvent.class)) {
		//Sets display to text
		DisplayTextEvent displayTextEvent = (DisplayTextEvent) event;
		displayText(displayTextEvent.getText(), displayTextEvent.isInactive(), displayTextEvent.getCreated());
	  } else if (eventClass.equals(AkkuEmptyEvent.class)) {
		displayText("Akku");
	  }
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }

  private void displayText(String text) throws TimeoutException, NotConnectedException {
	displayText(text, false, System.currentTimeMillis());
  }

  private void displayText(String text, boolean inactive, long eventCreated)
		  throws TimeoutException, NotConnectedException {
	short[] segments = {
			character(text.charAt(0)),
			character(text.charAt(1)),
			character(text.charAt(2)),
			character(text.charAt(3))
	};

	  /*
	   * Logic to diffentiate between inactive text and active text events
	   */
	if (inactive) {

	  //Checks if lastDisplayText is 5s away
	  if (eventCreated - lastDisplayText > TIME_FOR_INACTIVATION_IN_MS) {
		display.setSegments(segments, LOW_BRIGHTNESS, false);
	  }
	} else {
	  display.setSegments(segments, FULL_BRIGHTNESS, false);

	  //Logic to inactivate the Display after 5s
	  lastDisplayText = System.currentTimeMillis();
	  new Thread(
			  () ->
			  {
				//Waits 5s
				try {
				  Thread.sleep(TIME_FOR_INACTIVATION_IN_MS);
				} catch (InterruptedException e) {
				  e.printStackTrace();
				}

				//refire DisplayEvent with inactive
				EventBus.instance().fire(new DisplayTextEvent(text).inactive());
			  }
	  ).start();
	}
  }
}
