package babysnoozer.tinkerforge;

import babysnoozer.config.PropertiesLoader;
import babysnoozer.listeners.ServoListener;
import com.tinkerforge.BrickServo;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Alexander Bischof on 31.01.15.
 */
public class BrickServoWrapper {

  private static final String SERVO_BRICK_UID = "62Bpyf";

  private Properties servoConfigProperties;

  public enum Velocity {
	Learn("50", "learn_velocity"), Draw("200", "snooze_velocity"), Release("200", "snooze_velocity");

	private String defaultValue;
	private String propertyString;

	Velocity(String defaultValue, String propertyString) {
	  this.defaultValue = defaultValue;
	  this.propertyString = propertyString;
	}

	public String getDefaultValue() {
	  return defaultValue;
	}

	public String getPropertyString() {
	  return propertyString;
	}
  }

  private BrickServo brickServo;
  private short servoNumber;

  public BrickServoWrapper(IPConnection ipconnection, short servoNumber) {
	this.servoNumber = servoNumber;

	this.brickServo = new BrickServo(SERVO_BRICK_UID, ipconnection);

	try {
	  servoConfigProperties = new PropertiesLoader("servo.properties").load();
	} catch (IOException e) {
	  e.printStackTrace();
	}
  }

  public void setVelocity(Velocity velocity) throws TimeoutException, NotConnectedException {
	this.brickServo.setVelocity(servoNumber, Integer.valueOf(
			servoConfigProperties.getProperty(velocity.getPropertyString(), velocity.getDefaultValue())));
  }

  public void configServo() throws TimeoutException, NotConnectedException {

	//Sets next properties
	brickServo.setOutputVoltage(Integer.valueOf(servoConfigProperties.getProperty("outputVoltage", "7200")));

	brickServo.setDegree(servoNumber, Short.valueOf(servoConfigProperties.getProperty("degreeStart", "-900")),
	                     Short.valueOf(servoConfigProperties.getProperty("degreeEnd", "900")));
	brickServo.setPulseWidth(servoNumber, Short.valueOf(servoConfigProperties.getProperty("pulseWidthStart", "960")),
	                         Short.valueOf(servoConfigProperties.getProperty("pulseWidthEnd", "2040")));
	brickServo.setPeriod(servoNumber, Integer.valueOf(servoConfigProperties.getProperty("period", "19500")));
	brickServo.setAcceleration(servoNumber, Integer.valueOf(servoConfigProperties.getProperty("acceleration", "2000")));
	setVelocity(Velocity.Draw);

	//Sets servolistener
	ServoListener servoListener = new ServoListener();
	brickServo.addUnderVoltageListener(servoListener);
	brickServo.enablePositionReachedCallback();
	brickServo.addPositionReachedListener(servoListener);
  }

  public short getCurrentPosition() throws TimeoutException, NotConnectedException {
	return brickServo.getCurrentPosition(this.servoNumber);
  }

  public void setPosition(short position) throws TimeoutException, NotConnectedException {
	brickServo.setPosition(this.servoNumber, position);
  }

  public void enable() throws TimeoutException, NotConnectedException {
	brickServo.enable(this.servoNumber);
  }
}
