package babysnoozer;

import babysnoozer.events.DisplayTextEvent;
import babysnoozer.events.ShutdownEvent;
import com.tinkerforge.NotConnectedException;

import java.io.Closeable;
import java.io.IOException;

import static babysnoozer.EventBus.EventBus;
import static babysnoozer.tinkerforge.TinkerforgeSystem.TinkerforgeSystem;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public class Main implements Closeable {

  //TODO refac to conf file
  public static final long SHOW_SNOOZING_BABY_IN_MS = 5000l;

  public static void main(String[] args) throws Exception {

	try (Main main = new Main()) {

	  //InitBricks
	  TinkerforgeSystem.initBricks();

	  //Shows 3s Snoozing Baby
	  EventBus.post(new DisplayTextEvent("Snoozing Baby"));
	  Thread.sleep(SHOW_SNOOZING_BABY_IN_MS);

	  //Shows default cycle value
	  //EventBus.instance().fire(new InitSnoozingStateEvent());

	  System.out.println("Ready for Snooze");
	  System.in.read();
	}
  }

  @Override public void close() throws IOException {
	EventBus.post(new ShutdownEvent());

	try {
	  TinkerforgeSystem.getIpconnection().disconnect();
	} catch (NotConnectedException e) {
	  e.printStackTrace();
	}
  }
}
