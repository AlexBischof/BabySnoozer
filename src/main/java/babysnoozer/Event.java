package babysnoozer;

/**
 * Created by Alexander Bischof on 10.01.15.
 */
public interface Event {
  public default boolean isAsync() {
	return true;
  }
}
