package de.bischinger.cruzyfixbunny;

import com.tinkerforge.*;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Alexander Bischof on 19.12.14.
 */
public class CruzyfixBunny implements Closeable {

  public static final int ANLAUF_SCHWELLE = 100;
  public static final int AUFZIEH_SCHWELLE = 1000;

  private final IPConnection ipcon;
  private BrickServo servo;
  private BrickletSegmentDisplay4x7 display4x7;
  private BrickletRotaryEncoder roti;

  private boolean doStop;

  public CruzyfixBunny()
		  throws AlreadyConnectedException, IOException, NotConnectedException, InterruptedException, TimeoutException {
	ipcon = new IPConnection();
	servo = new BrickServo("6xhbGJ", ipcon);
	display4x7 = new BrickletSegmentDisplay4x7("pPJ", ipcon);
	roti = new BrickletRotaryEncoder("kGs", ipcon);

	ipcon.connect("localhost", 4223);

	configServo(servo);

	//Anfangszustand= fahre die 0 an
    servo.setVelocity((short) 0, 0xFF);
	servo.setPosition((short) 0, (short) 0);
	servo.enable((short) 0);
	System.out.println("CurrentPosition: " + servo.getCurrentPosition((short) 0) + " Catching Startposition 0");

    final BrickServo.PositionReachedListener startPositionReachedListener = (servoNum, position) -> {
	  if (position == 0) {
	    System.out.println("Position 0 found");
	   // servo.removePositionReachedListener(startPositionReachedListener);
	  }
    };
    servo.addPositionReachedListener(startPositionReachedListener);
    servo.enablePositionReachedCallback();

	Thread.sleep(10000l);
	System.out.println("CurrentPosition after sleep: " + servo.getCurrentPosition((short) 0));


    servo.setVelocity((short) 0, 0xAA);

    //fahre bis startbereit (max. x umdrehung)
    int umdrehungAnzahl = 2;
    int maxUmdrehungFuerAnlaufschwelle = umdrehungAnzahl * 3000;
	servo.setPosition((short) 0, (short) maxUmdrehungFuerAnlaufschwelle);
	boolean run = true;
    short position = 0;

	while (run && (position = servo.getCurrentPosition((short) 0))!=maxUmdrehungFuerAnlaufschwelle) {
	  int servoCurrent = 0;
	  try {
		servoCurrent = servo.getServoCurrent((short) 0);
		Thread.sleep(500l);

		if (servoCurrent > ANLAUF_SCHWELLE) {
		  position = servo.getCurrentPosition((short) 0);
		  servo.setPosition((short) 0, (short) (position - 1000));
		  System.out.println("Schwellwert überschritten. Stopping at position: " + position);

		  Thread.sleep(1000l);

		  run = false;
		  doStop = true;
		}
	  } catch (Exception e) {
		run = false;
	  }
	  System.out.println("Current: " + servoCurrent);
	}

    //Abbruch wenn Anlaufschwelle nicht gefunde#
    if (run && position >= maxUmdrehungFuerAnlaufschwelle){
      throw new RuntimeException("System broken. Anlaufschwelle not found.");
    }

    //2mal aufdrehen
    servo.setVelocity((short) 0, 0x3E8);  //ca. 1000
    short zielPosition = (short) (10000);
    servo.setPosition((short)0, zielPosition);

    //
    run =true;
     position = 0;
    while (run && (position = servo.getCurrentPosition((short) 0))!=10000) {
	  int servoCurrent = 0;
	  try {
	    servoCurrent = servo.getServoCurrent((short) 0);
	    Thread.sleep(100l);

	    if (servoCurrent > AUFZIEH_SCHWELLE) {
		  servo.setPosition((short) 0, (short) (position - 1000));
		  System.out.println("Schwellwert überschritten. Stopping at position: " + position);

		  Thread.sleep(1000l);

		  run = false;
		  doStop = true;
	    }
	  } catch (Exception e) {
	    run = false;
	  }
	  System.out.println("Current: " + servoCurrent);
    }
  }

  private static void configServo(BrickServo servo) throws TimeoutException, NotConnectedException,
		  InterruptedException {

	servo.setOutputVoltage(7200);

	servo.setDegree((short) 0, (short) -10000, (short) 10000);
	servo.setPulseWidth((short) 0, 1000, 2000);
	servo.setPeriod((short) 0, 19500);
	servo.setAcceleration((short) 0, 0xFFFF); // Slow acceleration
	servo.setVelocity((short) 0, 0xAA); // Full speed

  }

  public void fahreUndZurueck() throws TimeoutException, NotConnectedException {
	servo.setPosition((short) 0, (short) 0);
	servo.enable((short) 0);
	System.out.println("started");

	try {
	  System.out.println("Waiting");
	  Thread.sleep(5000l);
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}

	if (!doStop) {
	  servo.setPosition((short) 0, (short) 9000); // Set to most right position
	  servo.enable((short) 0);
	  System.out.println("damn it");

	  try {
		System.out.println("Waiting");
		Thread.sleep(5000l);
	  } catch (InterruptedException e) {
		e.printStackTrace();
	  }

	  if (!doStop) {
		servo.setPosition((short) 0, (short) -9000); // Set to most left position
		servo.enable((short) 0);
	  }
	}
  }

  @Override public void close() throws IOException {
	try {

		short currentPosition = servo.getCurrentPosition((short) 0);
		System.out.println("Close-Position: " + currentPosition);

		if (currentPosition != 0) {
		  servo.setVelocity((short) 0, 0xAA);
		  servo.setPosition((short) 0, (short) 0);
		  System.out.println("Endposition not 0, driving 0... ");
		  Thread.sleep(5000l);
		}

	  servo.disable((short)0);
	  System.out.println("Servo disabled.");
	  ipcon.disconnect();
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }
}
