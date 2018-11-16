package com.abilium.radar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.ojalgo.matrix.MatrixFactory;
import org.ojalgo.matrix.PrimitiveMatrix;

public class Radar {
    
	/**
	 * Demonstration method on how to use this library
	 * @param args
	 */
    public static void main(String[] args) {
    	
    	MatrixFactory<Double, PrimitiveMatrix> matrixFactory = PrimitiveMatrix.FACTORY;
    	
    	// Adjacency matrix for a graph having 4 nodes and 4 edges
    	double[][] dA = {
                {0.0, 1.0, 0.0, 1.0},
                {1.0, 0.0, 1.0, 0.0},
                {0.0, 1.0, 0.0, 1.0},
                {1.0, 0.0, 1.0, 0.0}
            };
        
    	// Attribute matrix holding values per node
        double[][] dX = {
                {4.0, 1.0},
                {5.0, 1.0},
                {3.0, 1.0},
                {5.0, 1.0}
            };
		
        // hyperparameters
        double alpha = 0.01;
        double beta = 0.01;
        double gamma = 0.5;
        
        PrimitiveMatrix X = matrixFactory.rows(dX);
        PrimitiveMatrix A = matrixFactory.rows(dA);
        
        // define number of maximum iterations
        int niters = 20;
    	
        // return top m instances
        int m = 11;
        
        // get the anomaly score list for the graph
		
    	System.out.println(Arrays.toString(RadarImpl.scoreFromRadar(X, A, alpha, beta, gamma, niters, m).toArray()));

		
	/*	List<DataLog> data = LogFileParser.dataFromLogFile("onos-log-2.txt");
		
		List<List<Node>> scores = new ArrayList<>();
		Iterator<DataLog> it = data.iterator();
		System.out.println("Starting calculation of " + data.size() + " Samples...");
		Date date = new Date();
		while(it.hasNext()) {
			DataLog d = it.next();
			List<Node> score = RadarImpl.scoreFromRadar(d.getRxMatrix().add(d.getTxMatrix()),
					d.getAdjMatrix(), alpha, beta, gamma, niters, m);
			scores.add(score);
			System.out.println(score);
			
		}
		System.out.println("Calculation took " + (new Date().getTime()-date.getTime()) + "ms");
		
		System.out.println("Maximum value is: " + max(scores));
		System.out.println("Average value is: " + avg(scores));
		System.out.println("Median value is: " + median(scores));
		System.out.println("Minimum value is: " + min(scores));
		System.out.println(createHistogram(scores, 10, 50));*/
        
  	}
    
    private static String createHistogram(List<List<Node>> scores, int steps, int span) {
    	int min = (int)Math.floor(min(scores).getVal());
    	int max = (int)Math.ceil(max(scores).getVal());

    	double step = (max-min)/10.0;
    	
    	long[] stepsArray = new long[steps];
    	
    	for(int i=0; i<steps; i++) {
    		double stepMin = min + step*i;
    		double stepMax = stepMin + step;
    		
    		stepsArray[i] = countInBetween(scores, stepMin, stepMax);
    	}
    	    	
    	return histFromData(stepsArray, min, max, step, span);
    }    
    
    private static String histFromData(long[] stepsArray, int min, int max, double step, int span) {
	    StringBuilder builder = new StringBuilder();
	    
	    long maxArr = max(stepsArray);

    	for(int i=0;i<stepsArray.length;i++) {
    		long l = stepsArray[i];
    		builder.append(String.format("%02.3f", step * i)+"\t");
    		for (int j = 0; j < l*span/maxArr; j++) {
    	        builder.append('*');
    	    }
    		builder.append("\r\n");
    	}
	    builder.append(String.format("%02.3f", (double)max)+"\t");
	    return builder.toString();
	}
    
    private static long max(long[] array) {
    	long max = Long.MIN_VALUE;
    	for(long l : array) {
    		if(l > max) {
    			max = l;
    		}
    	}
    	return max;
    }

	private static long countInBetween(List<List<Node>> scores, double min, double max) {
    	long count = 0;
    	Iterator<List<Node>> it = scores.iterator();
    	while(it.hasNext()) {
    		Iterator<Node> i = it.next().iterator();
        	while(i.hasNext()) {
        		Node n = i.next();
        		if(n.getVal()>=min && n.getVal()<max) {
        			count++;
        		}
        	}
    	}
    	return count;
    }
    
    private static Node min(List<List<Node>> scores) {
    	Node node = null;
    	double min = Double.MAX_VALUE;
    	Iterator<List<Node>> it = scores.iterator();
    	while(it.hasNext()) {
    		List<Node> l = it.next();
    		if(l.get(0).getVal() < min) {
    			min = l.get(0).getVal();
    			node = l.get(0);
    		}
    	}
    	return node;
    }
    
    private static Node max(List<List<Node>> scores) {
    	Node node = null;
    	double max = Double.MIN_VALUE;
    	Iterator<List<Node>> it = scores.iterator();
    	while(it.hasNext()) {
    		List<Node> l = it.next();
    		if(l.get(0).getVal() > max) {
    			max = l.get(0).getVal();
    			node = l.get(0);
    		}
    	}
    	return node;
    }
    
    private static double avg(List<List<Node>> scores) {
    	double sum = Double.MIN_VALUE;
    	long count = 0;
    	Iterator<List<Node>> it = scores.iterator();
    	while(it.hasNext()) {
    		List<Node> list = it.next();
    		count += list.size();
    		for(Node n: list) {
    			sum += n.getVal();
    		}
    	}
    	return sum/count;
    }
    
    private static long total(List<List<Node>> scores) {
    	long count = 0;
    	Iterator<List<Node>> it = scores.iterator();
    	while(it.hasNext()) {
    		List<Node> list = it.next();
    		count += list.size();
    		
    	}
    	return count;
    }
    
    private static double median(List<List<Node>> scores) {
    	List<Node> nodes = new ArrayList<>();
    	Iterator<List<Node>> it = scores.iterator();
    	while(it.hasNext()) {
    		nodes.addAll(it.next());
    	}
    	return nodes.get((int)nodes.size()/2).getVal();
    }

}
