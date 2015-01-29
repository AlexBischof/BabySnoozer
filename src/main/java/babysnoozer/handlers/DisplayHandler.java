package babysnoozer.handlers;

import babysnoozer.events.AkkuEmptyEvent;
import babysnoozer.events.DisplayTextEvent;
import com.tinkerforge.BrickletSegmentDisplay4x7;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import babysnoozer.Event;
import babysnoozer.EventBus;
import babysnoozer.EventHandler;

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

  private static short character(char c) {
	switch (c) {
	//Zahlen
	case '0':
	  return 0x3f;
	case '1':
	  return 0x06;
	case '2':
	  return 0x5b;
	case '3':
	  return 0x4f;
	case '4':
	  return 0x66;
	case '5':
	  return 0x6d;
	case '6':
	  return 0x7d;
	case '7':
	  return 0x07;
	case '8':
	  return 0x7f;
	case '9':
	  return 0x6f;

	//Kleinbuchstaben
	case 'a':
	  return 0x5f;
	case 'b':
	  return 0x7c;
	case 'c':
	  return 0x58;
	case 'd':
	  return 0x5e;
	case 'e':
	  return 0x7b;
	case 'f':
	  return 0x71;
	case 'g':
	  return 0x6f;
	case 'h':
	  return 0x74;
	case 'i':
	  return 0x02;
	case 'j':
	  return 0x1e;
	case 'k':
	  return 0x00; //npr
	case 'l':
	  return 0x06;
	case 'm':
	  return 0x00; //npr
	case 'n':
	  return 0x54;
	case 'o':
	  return 0x5c;
	case 'p':
	  return 0x73;
	case 'q':
	  return 0x67;
	case 'r':
	  return 0x50;
	case 's':
	  return 0x6d;
	case 't':
	  return 0x78;
	case 'u':
	  return 0x1c;
	case 'v':
	  return 0x00;//npr
	case 'w':
	  return 0x00;//npr
	case 'x':
	  return 0x00;//npr
	case 'y':
	  return 0x6e;
	case 'z':
	  return 0x00;//npr

	//Großbuchstaben
	case 'A':
	  return 0x77;
	case 'B':
	  return 0x7c;
	case 'C':
	  return 0x39;
	case 'D':
	  return 0x5e;
	case 'E':
	  return 0x79;
	case 'F':
	  return 0x71;
	case 'G':
	  return 0x6f;
	case 'H':
	  return 0x76;
	case 'I':
	  return 0x06;
	case 'J':
	  return 0x1e;
	case 'K':
	  return 0x00; //npr
	case 'L':
	  return 0x38;
	case 'M':
	  return 0x00; //npr
	case 'N':
	  return 0x54;
	case 'O':
	  return 0x3f;
	case 'P':
	  return 0x73;
	case 'Q':
	  return 0x67;
	case 'R':
	  return 0x50;
	case 'S':
	  return 0x6d;
	case 'T':
	  return 0x78;
	case 'U':
	  return 0x3e;
	case 'V':
	  return 0x00;//npr
	case 'W':
	  return 0x00;//npr
	case 'X':
	  return 0x00;//npr
	case 'Y':
	  return 0x6e;
	case 'Z':
	  return 0x00;//npr
	}
	return 0;
  }
}
