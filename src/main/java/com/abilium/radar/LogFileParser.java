package com.abilium.radar;

import java.io.InputStream;

public class LogFileParser {
	
	public static InputStream getStreamFromResourceFile(String name) {
		return LogFileParser.class.getClassLoader().getResourceAsStream(name);
	}
}
