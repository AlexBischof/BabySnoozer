package babysnoozer;

import com.tinkerforge.*;
import babysnoozer.events.CloseEvent;
import babysnoozer.events.DisplayEvent;
import babysnoozer.events.LogEvent;
import babysnoozer.handlers.DisplayHandler;
import babysnoozer.handlers.LogHandler;
import babysnoozer.listeners.RotiListener;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class Main implements Closeable {

  private IPConnection ipcon;
  private BrickServo servo;
  private BrickletSegmentDisplay4x7 display4x7;
  private BrickletRotaryEncoder roti;

  public static void main(String[] args) throws IOException, AlreadyConnectedException, InterruptedException {

	try (Main main = new Main()) {

	  //InitBricks
	  main.initBricks();

	  //Register Handlers
	  main.registerHandlers();

	  //Show 3s Snoozing Baby
	  EventBus.instance().fire(new DisplayEvent("Snoozing Baby"));

	  Thread.sleep(3000l);

	  //Show Defaulttime
	  EventBus.instance().fire(new DisplayEvent("   3"));

	  //TODO Zeit Bis dauern
	  System.out.println("Press key to exit");
	  System.in.read();
	}
  }

  private void registerHandlers() {

	//TODO repitiv

	EventBus.instance().registerHandler(new LogHandler());
	EventBus.instance().registerHandler(new DisplayHandler(display4x7));
  }

  private void initBricks() throws AlreadyConnectedException, IOException {
	EventBus.instance().fire(new LogEvent("Initbricks"));

	//TODO automatische Erkennung

	ipcon = new IPConnection();
	servo = new BrickServo("62Bpyf", ipcon);
	display4x7 = new BrickletSegmentDisplay4x7("pPJ", ipcon);

	initRoti();

	ipcon.connect("localhost", 4223);

  }

  private void initRoti() {
	roti = new BrickletRotaryEncoder("kGs", ipcon);

	RotiListener rotiListener = new RotiListener();
	roti.addPressedListener(rotiListener);
	roti.addReleasedListener(rotiListener);
  }

  @Override public void close() throws IOException {
	EventBus.instance().fire(new CloseEvent());

    EventBus.instance().fire(new LogEvent("ClosingEvent by console"));

	try {
	  ipcon.disconnect();
	} catch (NotConnectedException e) {
	  e.printStackTrace();
	}

	/*try {
	  short currentPosition = servo.getCurrentPosition((short) 0);
	  System.out.println("Close-Position: " + currentPosition);

	  if (currentPosition != 0) {
		servo.setVelocity((short) 0, 0xAA);
		servo.setPosition((short) 0, (short) 0);
		System.out.println("Endposition not 0, driving 0... ");
		Thread.sleep(5000l);
	  }

	  servo.disable((short) 0);
	  System.out.println("Servo disabled.");
	  ipcon.disconnect();
	} catch (Exception e) {
	  e.printStackTrace();
	}*/
  }
}
