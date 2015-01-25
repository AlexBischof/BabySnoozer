package babysnoozer;

import babysnoozer.events.CloseEvent;
import babysnoozer.events.DisplayEvent;
import babysnoozer.tinkerforge.TinkerforgeSystem;
import com.tinkerforge.NotConnectedException;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class Main implements Closeable {

  //TODO refac to conf file
  public static final long SHOW_SNOOZING_BABY_IN_MS = 3000l;

  public static void main(String[] args) throws Exception {

	try (Main main = new Main()) {

	  //InitBricks
	  TinkerforgeSystem.instance().initBricks();

	  //Register Handlers
	  TinkerforgeSystem.instance().registerHandlers();

	  //Show 3s Snoozing Baby
	  EventBus.instance().fire(new DisplayEvent("Snoozing Baby"));
	  Thread.sleep(SHOW_SNOOZING_BABY_IN_MS);

	  System.out.println("Press key to exit");
	  System.in.read();
	}
  }

  @Override public void close() throws IOException {
	EventBus.instance().fire(new CloseEvent());

	try {
	  TinkerforgeSystem.instance().getIpconnection().disconnect();
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
