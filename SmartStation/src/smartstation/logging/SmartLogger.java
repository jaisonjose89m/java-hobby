package smartstation.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import smartstation.util.SmartStationFormatter;

public class SmartLogger {
private final Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

public SmartLogger() {
	try {
		SmartStationFormatter formatter = new SmartStationFormatter();
		FileHandler fhandler = new FileHandler("smart_station.log");
		fhandler.setFormatter(formatter);
		logger.addHandler(fhandler);
		logger.setLevel(Level.INFO);
	} catch (SecurityException | IOException e) {
		// Won't create logger file
	}
}
public Logger getLogger() {
	return logger;
}

}
