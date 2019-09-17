package com.onesait.edge.engine.modbus.conf;

public final class GlobalConstants {

	private GlobalConstants() {
		throw new IllegalStateException("Constants class");
	}

	public static class Form {

		private Form() {
			throw new IllegalStateException("Form Constants class");
		}

		public static final String SERIALPORTS = "serialports";
		public static final String BAUDRATES = "baudrates";
		public static final String DEVICERTU = "deviceRtuForm";
		public static final String DEVICETCP = "deviceTcpForm";
		public static final String SIGNAL = "signalForm";

		public static final String TCP = "TCP";
		public static final String RTU = "RTU";

	}

	public static class WebPages {

		private WebPages() {
			throw new IllegalStateException("WebPages Constants class");
		}

		public static final String DEVICES = "devices";
		public static final String INDEX = "modbus";
		public static final String MONITOR = "monitor";
		public static final String NEWRTU = "newdevicertu";
		public static final String NEWTCP = "newdevicetcp";
		public static final String SIGNALS = "signals";
		public static final String NEWSIGNAL = "newsignal";
		public static final String ERROR = "error";
	}

	public static class Contexts {

		private Contexts() {
			throw new IllegalStateException("Contexts Constants class");
		}

		public static final String ROOT = "/";
		public static final String INDEX = "/index";
		// public static final String MODBUS = "/modbus";
		public static final String DEVICES = "/devices";
		public static final String SIGNALS = "/signals/:deviceId";
		public static final String MONITORINGDEVICE = "/monitor/:deviceId";
		public static final String NEWTCPDEVICE = "/newdevicetcp";
		public static final String NEWRTUDEVICE = "/newdevicertu";
		public static final String REMOVEDEVICE = "/removedevice/:deviceId";
		public static final String NEWSIGNALDETAIL = "/newsignal/:deviceId";
		public static final String NEWSIGNAL = "/newsignal";
		public static final String REMOVESIGNAL = "/deletesignal/:deviceId/:signalId";
		public static final String UPDATEDEVICE = "/updatedevice/:deviceId";
	}

	public static class Action {

		private Action() {
			throw new IllegalStateException("Action Constants class");
		}

		public static final String REDIRECT = "redirect:";
	}

	public static class Messages {

		private Messages() {
			throw new IllegalStateException("Messages Constants class");
		}

		public static final String ERROR = "errorMessage";
	}

	public static class Delimiters {

		private Delimiters() {
			throw new IllegalStateException("Delimiters Constants class");
		}

		public static final String SLASH = "/";
	}
	
public static class PropertiesFile {
		
		private PropertiesFile() {
		    throw new IllegalStateException("PropertiesFile Constants class");
		}
		
	    public static final String CONFIG_FILE_NAME = "application.properties";
	    public static final String MESSAGES_ENGLISH_FILE_NAME = "messages_english.properties";
	}
	
    public static class Languages {
    	
    	private Languages() {
		    throw new IllegalStateException("Languages Constants class");
		}
    	
    	// Languages
    	public static final String ENGLISH = "EN";
    	public static final String SPANISH = "ES";
    	
    	//Properties
    	public static final String LANGUAGE = "LANGUAGE";
    }
    
	public static class DataBase {
		
		private DataBase() {
		    throw new IllegalStateException("DataBase Constants class");
		}
		
		public static final String UNKNOWN_DB		     = "unknown";
		public static final String GETTING_CONNECTION_DB = "GETTING_CONNECTION_DB";
		public static final String FILTER_DATE_FORMAT    = "yyyy-MM-dd HH:mm";
		public static final String OUTPUT_DATE_FORMAT	 = "yyyy-MM-dd'T'HH:mm:ssZ";
		public static final String MEASURE_QUERY		 = "MEASURE_QUERY";
	}
	
	public static class MessagesKey {
		
		private MessagesKey() {
		    throw new IllegalStateException("MessageKey Constants class");
		}
		
		public static final String LOADED_CONFIG     = "LOADED_CONFIG";
		public static final String INTERNAL_ERROR    = "INTERNAL_ERROR";
		public static final String QUERY_ERROR       = "QUERY_ERROR";
	}
}
