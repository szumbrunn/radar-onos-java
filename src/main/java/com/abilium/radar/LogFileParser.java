package com.abilium.radar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.MatrixFactory;
import org.ojalgo.matrix.PrimitiveMatrix;

/**
 * parses log file from Onos ML-Controller 
 * @author S. Zumbrunn
 *
 */
public class LogFileParser {
	
	private static MatrixFactory<Double, PrimitiveMatrix> matrixFactory = PrimitiveMatrix.FACTORY;
	
	public static InputStream getStreamFromResourceFile(String name) {
		return LogFileParser.class.getClassLoader().getResourceAsStream(name);
	}
	
	public static DataLog getDataLogFromLine(String line) {
		DataLog dataLog = new DataLog();
		String[] array = line.split("\\|");
		dataLog.setDate(new Date((long)Long.parseLong(array[0])*1000));
		dataLog.setAdjMatrix(getMatrixFromString(array[1]));
		dataLog.setRxMatrix(getMatrixFromString(array[2]));
		dataLog.setTxMatrix(getMatrixFromString(array[3]));
		return dataLog;
	}

	private static PrimitiveMatrix getMatrixFromString(String string) {
		List<double[]> matrix = new ArrayList<>();
		String[] strRows = string.split("\\;");
		int colSize = 0;
		for(String row : strRows) {
			List<Double> rowList = new ArrayList<>();
			String[] strCols = row.split("[\\ ]");
			for(String strCol : strCols) {
				if(!strCol.equals(""))
					rowList.add(new Double(Integer.parseInt(strCol)));
			}
			colSize = strCols.length;
			Double[] arr = new Double[rowList.size()];
			matrix.add(Stream.of(rowList.toArray(arr)).mapToDouble(Double::doubleValue).toArray());
 		}
		
		double[][] matrArray = new double[strRows.length][colSize];
		for (int i = 0; i < matrix.size(); i++) {
		    matrArray[i] = matrix.get(i).clone();
		}
		matrArray = matrix.toArray(matrArray);
		
		return matrixFactory.rows(matrArray);
	}

	public static List<DataLog> dataFromLogFile(String name) {
		List<DataLog> list = new ArrayList<>();
		
		InputStream is = getStreamFromResourceFile(name);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
