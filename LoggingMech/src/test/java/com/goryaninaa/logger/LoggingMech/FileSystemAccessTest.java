package com.goryaninaa.logger.LoggingMech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileSystemAccessTest {
	
	private final String PATH = "temp/test/logs";
	private final Properties properties = new Properties();
	
	@Before
	public void initProperties() {
		properties.setProperty("LoggingMech.logsDirPathUrl", PATH);
		properties.setProperty("LoggingMech.bytesPerFile", "100");
	}
	
	@Test
	public void fsaShouldWriteMessageToLogFile() throws FileNotFoundException, IOException {
		properties.setProperty("LoggingMech.amountOfLogs", "1");
		FileSystemAccess fsa = new FileSystemAccess(properties);
		String expected = "Hello";
		fsa.writeLog("Hello");
		String actual = readMessageFromLogFileOnFS();
		assertEquals(expected, actual);
	}
	
	@Test
	public void fsaShouldCreateLogFileIfThereIsNoOne() {
		properties.setProperty("LoggingMech.amountOfLogs", "1");
		FileSystemAccess fsa = new FileSystemAccess(properties);
		boolean amountOfFilesBeforeIsZero = new File(PATH).list().length == 0;
		fsa.writeLog("Hello");
		boolean amountOfFilesAfterIsOne = new File(PATH).list().length == 1;
		assertTrue(amountOfFilesBeforeIsZero && amountOfFilesAfterIsOne);
	}

	@Test
	public void fsaShouldCreateNewLogFileIfCurrentHasLengthMoreThan100() {
		properties.setProperty("LoggingMech.amountOfLogs", "10");
		FileSystemAccess fsa = new FileSystemAccess(properties);
		boolean amountOfFilesBeforeIsZero = new File(PATH).list().length == 0;
		for(int i = 0; i < 27; i++) {
			fsa.writeLog("Hello" + i);
		}
		boolean amountOfFilesAfterIsTwo = new File(PATH).list().length == 2;
		assertTrue(amountOfFilesBeforeIsZero && amountOfFilesAfterIsTwo);
	}
	
	@Test
	public void fsaShouldKeepAmountOfLogFilesAccordingToParamether() {
		properties.setProperty("LoggingMech.amountOfLogs", "2");
		FileSystemAccess fsa = new FileSystemAccess(properties);
		boolean amountOfFilesBeforeIsZero = new File(PATH).list().length == 0;
		for(int i = 0; i < 100; i++) {
			fsa.writeLog("Hello" + i);
		}
		boolean amountOfFilesAfterIsTwo = new File(PATH).list().length == 2;
		assertTrue(amountOfFilesBeforeIsZero && amountOfFilesAfterIsTwo);
	}
	
	@After
	public void deleteTestLogFiles() {
		String[] logFilesNames = new File(PATH).list();
		for (String logFileName : logFilesNames) {
			new File(PATH + "/" + logFileName).delete();
		}
	}
	
	private String readMessageFromLogFileOnFS() throws IOException, FileNotFoundException {
		String actual;
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(PATH + "/" + new File(PATH).list()[0])))) {
			actual = reader.readLine();
		}
		return actual;
	}
}
