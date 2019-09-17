package com.onesait.edge.engine.zigbee.influx;

import java.text.MessageFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.util.GlobalConstants;

public class Configuration {

	Properties properties = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

	private Configuration() {
		this.properties = new Properties();
		try {

			String defaultLanguageFile = GlobalConstants.PropertiesFile.MESSAGES_ENGLISH_FILE_NAME;
			properties.load(Configuration.class.getClassLoader().getResourceAsStream(GlobalConstants.PropertiesFile.CONFIG_FILE_NAME));
			properties.load(Configuration.class.getClassLoader().getResourceAsStream(defaultLanguageFile));

			LOGGER.info(this.getProperty(GlobalConstants.MessagesKey.LOADED_CONFIG));

		} catch (Exception ex) {
			LOGGER.error(this.getProperty(GlobalConstants.MessagesKey.INTERNAL_ERROR), ex.getMessage());
		}
	}

	public static Configuration getInstance() {
		return ConfigurationHolder.INSTANCE;
	}

	private static class ConfigurationHolder {

		private static final Configuration INSTANCE = new Configuration();
	}

	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}

	public String getProperty(String key, Object... params) {
		return MessageFormat.format(this.getProperty(key), params);
	}
}
