package babysnoozer.config;

import java.io.*;
import java.util.Properties;

/**
 * Created by Alexander Bischof on 25.01.15.
 */
public class PropertiesLoader {

    private final String filename;
    private final boolean useResoureceAsStream;

    public PropertiesLoader(String filename, boolean useResoureceAsStream) {
        this.useResoureceAsStream = useResoureceAsStream;
        this.filename = filename;
    }

    public PropertiesLoader(String filename) {
        this(filename, true);
    }

    public boolean createIfNotExist() {
        boolean created = false;
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
                created = true;
            } catch (IOException e) {
                System.err.println("Could not create propertyfile" + filename + ", " + e.getMessage());
            }
        }
        return created;
    }

    public Properties load() throws IOException {

        Properties properties = new Properties();
        try (InputStream in = useResoureceAsStream ?
                Thread.currentThread().getContextClassLoader().getResourceAsStream(filename) :
                new FileInputStream(filename)) {
            properties.load(in);
        }
        return properties;
    }

    public void store(Properties properties) throws IOException {
        File f = new File(this.filename);
        OutputStream out = new FileOutputStream(f);
        properties.store(out, "Alter, whats up");
    }
}
