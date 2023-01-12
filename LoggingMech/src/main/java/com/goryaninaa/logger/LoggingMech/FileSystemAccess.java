package com.goryaninaa.logger.LoggingMech;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class FileSystemAccess {
	
	private final File logsDir;
	private final long BYTESPERFILE;
	private final int AMOUNTOFLOGS;
	
	protected FileSystemAccess(String logsDirPathUrl, long bytesPerFile, int amountOfLogs) {
		this.logsDir = new File(logsDirPathUrl);
		this.BYTESPERFILE = bytesPerFile;
		this.AMOUNTOFLOGS = amountOfLogs;
		logsDir.mkdirs();
	}
	
	protected void writeLog(String record){
		prepare();
		File file = getCurrentLogFile();
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
			if (!(defineCurrentLogFileSize() == 0)) {
				writer.newLine();
			}
			writer.append(record);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void prepare() {
		if (countLogFiles() == 0) {
			createNewLogFile();
		}
		if (defineCurrentLogFileSize() > BYTESPERFILE) {
			createNewLogFile();
		}
		if (countLogFiles() > AMOUNTOFLOGS) {
			removeExcessFiles();
		}
	}

	private File getCurrentLogFile() {
		return defineCurrentLogFile();
	}

	private void removeExcessFiles() {
		String[] logFileNames = logsDir.list();
		Arrays.sort(logFileNames);
		int fileCounter = logFileNames.length;
		int i = 0;
		while (fileCounter > AMOUNTOFLOGS) {
			fileCounter--;
			new File(logsDir.getAbsolutePath() + "/" + logFileNames[i++]).delete();
		}
	}

	private long defineCurrentLogFileSize() {
		String currentLogFilePath = defineCurrentLogFile().getAbsolutePath();
		Path path = Paths.get(currentLogFilePath);
		try {
			return Files.size(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File defineCurrentLogFile() {
		String[] logFileNames = logsDir.list();
		Arrays.sort(logFileNames);
		String currentLogFileName = logFileNames[logFileNames.length - 1];
		File currentLogFile = new File(logsDir, currentLogFileName);
		return currentLogFile;
	}

	private void createNewLogFile() {
		String logFileName = generateName();
		try {
			new File(logsDir, logFileName).createNewFile();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create a log file", e);
		}
	}

	private String generateName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssn");
		String datePartOfName = LocalDateTime.now().format(formatter);
		return "ApplicationLog" + datePartOfName + ".txt";
	}

	private int countLogFiles() {
		return logsDir.list().length;
	}
}
