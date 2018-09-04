package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class UtilPropertyManager {

	public UtilPropertyManager(String resorceName) {
		doFactory(resorceName);
	}


	private Properties prop;

	public Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}

	private void doFactory(String resorceName) {
		try {
			URL url = getResource(resorceName);
			prop = new Properties();
			prop.load(url.openStream());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public URL getResource(String target) {
		ClassLoader loader = this.getClass().getClassLoader();
		URL url = null;
		for ( ; loader != null; ) {
			url = loader.getResource(target);
			if ( url == null ) {
				loader = loader.getParent();
			} else {
				break;
			}
		}
		return url;
	}

	public String getMessage(String key) {
		return prop.getProperty(key);
	}
}
