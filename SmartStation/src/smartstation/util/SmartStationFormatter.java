package smartstation.util;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class SmartStationFormatter extends SimpleFormatter{
@Override
public synchronized String format(LogRecord record) {
	//System.out.println(record.getMessage());
	return super.format(record);
}
}
