package com.goryaninaa.logger.LoggingMech;

import java.time.LocalDateTime;

public class Logger {
	
	private final String loggingClassName;
	private final LoggingMech loggingMech;
	
	public Logger(String loggingClassName, LoggingMech loggingMech) {
		this.loggingClassName = loggingClassName;
		this.loggingMech = loggingMech;
	}
	
	public void error(String message) {
		String record = generateErrorLogRecord(message);
		loggingMech.submit(record);
	}
	
	public void warn(String message) {
		String record = generateWarnLogRecord(message);
		loggingMech.submit(record);
	}

	public void info(String message) {
		if (loggingMech.isInfoLevelLoggingActive()) {
			String record = generateInfoLogRecord(message);
			loggingMech.submit(record);
		}
	}
	
	public void debug(String message) {
		if (loggingMech.isDebugLevelLoggingActive()) {
			String record = generateDebugLogRecord(message);
			loggingMech.submit(record);
		}
	}
	
	private String generateErrorLogRecord(String message) {
		return generateLogRecord(message, "ERROR");
	}
	
	private String generateWarnLogRecord(String message) {
		return generateLogRecord(message, "WARN");
	}
	
	private String generateInfoLogRecord(String message) {
		return generateLogRecord(message, "INFO");
	}
	
	private String generateDebugLogRecord(String message) {
		return generateLogRecord(message, "DEBUG");
	}
	
	private String generateLogRecord(String message, String level) {
		String logErrorRecord = "{\"localDateTime\":\"" + LocalDateTime.now().toString() + "\"," +
				"\"thread\":\"" + Thread.currentThread().getName() + "\"," +
				"\"level\":\"" + level + "\"," +
				"\"loggerName\":\"" + loggingClassName + "\"," +
				"\"message\":" + "\"" + message + "\"}";
		return logErrorRecord;
	}
}
