package com.abilium.radar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.netio.BufferedInputStreamReader;

public class LogFileParser {
	
	private static PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory =
            PrimitiveDenseStore.FACTORY;
	
	public static InputStream getStreamFromResourceFile(String name) {
		return LogFileParser.class.getClassLoader().getResourceAsStream(name);
	}
	
	public static DataLog getDataLogFromLine(String line) {
		DataLog dataLog = new DataLog();
		String[] array = line.split("|");
		
		dataLog.setDate(new Date((long)Integer.parseInt(array[0])*1000));
		dataLog.setAdjMatrix(getMatrixFromString(array[1]));
		return dataLog;
	}

	private static BasicMatrix getMatrixFromString(String string) {
		String[] strRows = string.split(";");
		
		return null;
	}

	public static List<DataLog> dataFromLogFile(String name) {
		List<DataLog> list = new ArrayList<>();
		
		InputStream is = getStreamFromResourceFile(name);
		BufferedReader reader = new BufferedInputStreamReader(is);
		String line = "";
		try {
			while((line = reader.readLine())!=null) {
				list.add(getDataLogFromLine(line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
}
