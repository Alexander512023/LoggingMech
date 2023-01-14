package com.goryaninaa.logger.LoggingMech;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class LoggerTest {
	
	private final String PATH = "temp/test/logs";
	private final Properties properties = new Properties();
	
	@Before
	public void initProperties() {
		properties.setProperty("LoggingMech.logsDirPathUrl", PATH);
		properties.setProperty("LoggingMech.bytesPerFile", "10000");
		properties.setProperty("LoggingMech.amountOfLogs", "1");
		properties.setProperty("LoggingMech.Level", "DEBUG");
	}
	
	@Test
	public void loggerShouldWriteCorrectLogMessageToFS() throws InterruptedException, IOException {
		writeLogMessageToLogFileOnFS();
		String message = readWrittenMessageFromLogFileOnFS();
		deleteLogFile();
		Matcher matcher = createMatcherToAssert(message);
		assertTrue(matcher.find());
	}

	private Matcher createMatcherToAssert(String message) {
		Pattern pattern = Pattern.compile(""
				+ "\\{\"localDateTime\":\".+?\","
				+ "\"thread\":\"main\","
				+ "\"level\":\"ERROR\","
				+ "\"loggerName\":\"com.goryaninaa.logger.LoggingMech.LoggerTest\","
				+ "\"message\":\"Test\"\\}");
		Matcher matcher = pattern.matcher(message);
		return matcher;
	}

	private void deleteLogFile() {
		new File(PATH + "/" + new File(PATH).list()[0]).delete();
	}

	private String readWrittenMessageFromLogFileOnFS() throws FileNotFoundException, IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(PATH + "/" + new File(PATH).list()[0])))) {
			String message = reader.readLine();
			return message;
		}
	}

	private void writeLogMessageToLogFileOnFS() throws InterruptedException {
		LoggingMech.getInstance().apply(properties);
		LoggingMech.getInstance().startLogging();
		Logger logger = LoggingMech.getLogger(this.getClass().getCanonicalName());
		logger.error("Test");
		TimeUnit.MILLISECONDS.sleep(10);
		LoggingMech.getInstance().stopLogging();
	}

}
