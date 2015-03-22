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
	lvl1("50", "speed_lvl1"),
	lvl2("100", "speed_lvl2"),
	lvl3("200", "speed_lvl3"),
	lvl4("1000", "speed_lvl4"),
	max("65535", "speed_max"),
	learn("50", "speed_learn");

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

  public enum Acceleration {
	lvl1("50", "acc_lvl1"),
	lvl2("100", "acc_lvl2"),
	lvl3("200", "acc_lvl3"),
	lvl4("1000", "acc_lvl4"),
	max("65535", "acc_max"),
	learn("50", "acc_learn");

	private String defaultValue;
	private String propertyString;

	Acceleration(String defaultValue, String propertyString) {
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

  BrickServo brickServo;
  short servoNumber;

  public BrickServoWrapper(int servoNumber) {
	this.servoNumber = (short) servoNumber;

	try {
	  servoConfigProperties = new PropertiesLoader("servo.properties").load();
	} catch (IOException e) {
	  e.printStackTrace();
	}
  }

  public void initBrick(IPConnection ipconnection) {
	this.brickServo = new BrickServo(SERVO_BRICK_UID, ipconnection);
  }

  public void setVelocity(Velocity velocity) throws TimeoutException, NotConnectedException {
	this.brickServo.setVelocity(servoNumber, Integer.valueOf(
			servoConfigProperties.getProperty(velocity.getPropertyString(), velocity.getDefaultValue())));
  }

  public void setAcceleration(Acceleration acceleration) throws TimeoutException, NotConnectedException {
	this.brickServo.setAcceleration(servoNumber, Integer.valueOf(
			servoConfigProperties.getProperty(acceleration.getPropertyString(), acceleration.getDefaultValue())));
  }

  public void configServo() throws TimeoutException, NotConnectedException {

	//Sets nextCommand properties
	brickServo.setOutputVoltage(Integer.valueOf(servoConfigProperties.getProperty("outputVoltage", "7200")));

	brickServo.setDegree(servoNumber, Short.valueOf(servoConfigProperties.getProperty("degreeStart", "-900")),
	                     Short.valueOf(servoConfigProperties.getProperty("degreeEnd", "900")));
	brickServo.setPulseWidth(servoNumber, Short.valueOf(servoConfigProperties.getProperty("pulseWidthStart", "960")),
	                         Short.valueOf(servoConfigProperties.getProperty("pulseWidthEnd", "2040")));
	brickServo.setPeriod(servoNumber, Integer.valueOf(servoConfigProperties.getProperty("period", "19500")));
	brickServo.setAcceleration(servoNumber, Integer.valueOf(servoConfigProperties.getProperty("acceleration", "2000")));
	// velocity and acc should be max to set brick firmware immediately to first pos
	// this overwrites the initial positions of 0 in brick firmware
	setVelocity(Velocity.max);
	setAcceleration(Acceleration.max);

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

  public void disable() throws TimeoutException, NotConnectedException {
	brickServo.disable(this.servoNumber);
  }
}
