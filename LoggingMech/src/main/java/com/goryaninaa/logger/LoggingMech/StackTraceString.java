package com.goryaninaa.logger.LoggingMech;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceString {
	
	public static String get(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}
