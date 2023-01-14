package com.goryaninaa.logger.LoggingMech;

import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggingMech {
	
	private static volatile LoggingMech instance;
	private final Queue<String> loggingTaskQueue = new ConcurrentLinkedQueue<>();
	private volatile FileSystemAccess fsa;
	private volatile ExecutorService exec;
	private volatile Level level;
	
	private LoggingMech() {
		
	}
	
	public static LoggingMech getInstance() {
		LoggingMech localInstance = instance;
		if (localInstance == null) {
			synchronized(LoggingMech.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new LoggingMech();
				}
			}
		}
		return localInstance;
	}
	
	public synchronized void apply(Properties properties) {
		this.fsa = new FileSystemAccess(properties);
		setLevel(properties.getProperty("LoggingMech.Level"));
	}
	
	private void setLevel(String levelProperty) {
		if (Level.DEBUG.toString().equals(levelProperty)) {
			this.level = Level.DEBUG;
		} else if (Level.INFO.toString().equals(levelProperty)) {
			this.level = Level.INFO;
		} else if (Level.WARN.toString().equals(levelProperty)) {
			this.level = Level.WARN;
		} else {
			throw new IllegalArgumentException("Unsupported LoggingMech.Level propertie");
		}
	}
	
	public synchronized void startLogging() {
		if (fsa != null && level != null) {
			exec = Executors.newSingleThreadExecutor();
			exec.submit(() -> runLog());
		} else {
			throw new RuntimeException("Logger doesn't started because it was not initialized correctly");
		}
	}
	
	public synchronized void stopLogging() {
		if (exec != null) {
			exec.shutdown();
		}
	}
	
	public static Logger getLogger(String loggingClassName) {
		return new Logger(loggingClassName, getInstance());
	}

	protected void submit(String record) {
		loggingTaskQueue.add(record);
	}

	protected boolean isDebugLevelLoggingActive() {
		if (level == Level.DEBUG) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean isInfoLevelLoggingActive() {
		if (level == Level.DEBUG || level == Level.INFO) {
			return true;
		} else {
			return false;
		}
	}

	private void logRecord() {
		String record = loggingTaskQueue.poll();
		fsa.writeLog(record);
		System.out.println(record);
	}
	
	private void runLog() {
		while(true) {
			if (!loggingTaskQueue.isEmpty()) {
				logRecord();
			}
		}
	}
}
