package babysnoozer.config;

import java.io.*;
import java.util.Properties;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class PropertiesLoader {

  private final String filename;
  private final boolean internal;

  public PropertiesLoader(String filename, boolean internal) {
	this.internal = internal;
	this.filename = filename;
  }

  public PropertiesLoader(String filename) {
	this.filename = filename;
    this.internal = true;
  }

  public Properties load() throws IOException {
	Properties properties = new Properties();
	InputStream in = internal ? PropertiesLoader.class.getResourceAsStream(filename) : new FileInputStream(filename);
	properties.load(in);
	in.close();
	return properties;
  }

  public void store(Properties properties) throws IOException {
	File f = new File(this.filename);
	OutputStream out = new FileOutputStream(f);
	properties.store(out, "Alter, whats up");
  }
}
