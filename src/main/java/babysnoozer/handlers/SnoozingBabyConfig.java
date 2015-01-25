package babysnoozer.handlers;

/**
 * Created by Alexander Bischof on 12.01.15.
 */
public class SnoozingBabyConfig {
  private static final SnoozingBabyConfig singleton = new SnoozingBabyConfig();

  //TODO read from conf file
  private int runtimeInMinutes = 3;

  private SnoozingBabyConfig() {

  }

  public static SnoozingBabyConfig instance() {
	return singleton;
  }

  public int getRuntimeInMinutes() {
	return runtimeInMinutes;
  }

  public void setRuntimeInMinutes(int runtimeInMinutes) {
	this.runtimeInMinutes = runtimeInMinutes;
  }
}
