package de.bischinger.cruzyfixbunny;

import com.tinkerforge.*;

/**
 * Created by Alexander Bischof on 05.12.14.
 */
public class test {

  private final static int ANSCHLAG_AUSZUG = 9000;
  private final static int ANSCHLAG_EINZUG = -9000;

  private Thread thread;

  public static void main(String[] args)
		  throws Exception {

	IPConnection ipcon = new IPConnection();
	BrickServo servo = new BrickServo("62YUe1", ipcon); // Create device object
	final BrickletSegmentDisplay4x7 display4x7 = new BrickletSegmentDisplay4x7("pPJ", ipcon);
	final BrickletRotaryEncoder roti = new BrickletRotaryEncoder("kGs", ipcon);

	//connect
	ipcon.connect("localhost", 4223);

	//Initial meldung
	display4x7.setSegments(new short[] { character('J'), character('O'), character('H'), character('A') }, (short) 5,
	                       false);

	configRoti(display4x7, roti, servo);

	//Display position
	servo.addPositionReachedListener((servonum, position) -> {
	                                   System.out.println(servonum + " " + position);
	                                   //display4x7

	                                   if (position == ANSCHLAG_AUSZUG) {
		                                 //Switch config for einzug
	                                   } else if (position == ANSCHLAG_EINZUG) {
		                                 //Switch config for auszug
	                                   }
	                                 }
	);
	servo.enablePositionReachedCallback();

	configServo(servo);

	System.in.read();

	ipcon.disconnect();
  }

  private static void configRoti(BrickletSegmentDisplay4x7 display4x7, BrickletRotaryEncoder roti,
                                 final BrickServo servo)
		  throws TimeoutException, NotConnectedException {

    roti.setDebouncePeriod(100);
	roti.addPressedListener(() -> {
	  try {
		System.out.println("Pressed and Started");
		enable(servo);
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	});

	roti.setCountCallbackPeriod(100);

	roti.addCountListener(pos -> {
	  int ziffer1 = pos / 1000;
	  int ziffer10 = pos / 100;
	  int ziffer100 = pos / 10;
	  int ziffer1000 = pos % 10;

	  System.out.println(ziffer1 + " " + ziffer10 + " " + ziffer100 + " " + ziffer1000);

	  try {
		short[] segments = {
				character(Character.forDigit(ziffer1, 10)),
				character(Character.forDigit(ziffer10, 10)),
				character(Character.forDigit(ziffer100, 10)),
				character(Character.forDigit(ziffer1000, 10))
		};
		display4x7.setSegments(segments, (short) 5, false);
	  } catch (Exception e) {
		e.printStackTrace();
	  }
	});
  }

  private static void enable(BrickServo servo) throws TimeoutException, NotConnectedException, InterruptedException {
	servo.setPosition((short) 0, (short) 0);
	servo.enable((short) 0);
	System.out.println("started");

	Thread.sleep(5000l);

	servo.setPosition((short) 0, (short) 9000); // Set to most right position
	servo.enable((short) 0);
	System.out.println("damn it");

	servo.setPosition((short) 0, (short) -9000); // Set to most left position
	servo.enable((short) 0);
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

  private static void configServo(BrickServo servo) throws TimeoutException, NotConnectedException,
		  InterruptedException {

	servo.setOutputVoltage(7200);

	servo.setDegree((short) 0, (short) -10000, (short) 10000);
	servo.setPulseWidth((short) 0, 500, 2000);
	servo.setPeriod((short) 0, 19500);
	servo.setAcceleration((short) 0, 0xFFFF); // Slow acceleration
	servo.setVelocity((short) 0, 0xFFFF); // Full speed

	//enable(servo);
  }
}
