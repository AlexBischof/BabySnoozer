package babysnoozer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class PropertiesLoader {

  private final String filename;

  public PropertiesLoader(String filename) {
	this.filename = filename;
  }

  public Properties load() throws IOException {
	Properties properties = new Properties();
	InputStream in = PropertiesLoader.class.getResourceAsStream(filename);
	properties.load(in);
	in.close();
	return properties;
  }
}
