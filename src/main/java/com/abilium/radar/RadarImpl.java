package com.abilium.radar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.function.PrimitiveFunction;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.MatrixFactory;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.Scalar;

/**
 * Implementation according to the paper of J. Li, et al (2017) 
 * Radar: Residual Analysis for Anomaly Detection in Attributed Networks 
 * 
 * Calculates anomaly rates for given graph
 * @author S. Zumbrunn
 *
 */
public class RadarImpl {

	
	private static MatrixFactory<Double, PrimitiveMatrix> matrixFactory = PrimitiveMatrix.FACTORY;
	private static PhysicalStore.Factory<Double, PrimitiveDenseStore> storeFactory =
            PrimitiveDenseStore.FACTORY;
	
	/**
	 * Wrapper method to calculate ordered score list with
	 * @param X the attribute matrix of size nxm
	 * @param A the adjacency matrix of size nxn
	 * @param alpha (hyperparameter)
	 * @param beta (hyperparameter)
	 * @param gamma (hyperparameter)
	 * @param niters maximum number of iterations
	 * @return ordered score list
	 */
	public static List<Node> scoreFromRadar(PrimitiveMatrix X, PrimitiveMatrix A, double alpha, double beta, double gamma, int niters, int m) {
		long n = X.columns().count();
		PrimitiveMatrix colVector = matrixFactory.makeZero(n, 1).add(1); // make 1 row vector
		
		PrimitiveMatrix R = RadarImpl.radar(X, A, alpha, beta, gamma, niters);

		List<Double> scoreList = R.multiplyElements(R)
								.multiply(colVector).getSingularValues();
		List<Node> score = new ArrayList<>();
		for(int i=0;i<scoreList.size();i++) {
 			score.add(new Node(i,scoreList.get(i)));
		}
		Collections.sort(score);
		if(m>score.size()) {
			m = score.size();
		}
		return score.subList(0, m);
	}
	
	/**
	 * Computes the anomaly score for a given graph with 
	 * @param X the attribute matrix of size nxm
	 * @param A the adjacency matrix of size nxn
	 * @param alpha (hyperparameter)
	 * @param beta (hyperparameter)
	 * @param gamma (hyperparameter)
	 * @param niters maximum number of iterations
	 * @return anomaly score as matrix
	 */
	public static PrimitiveMatrix radar(PrimitiveMatrix X, PrimitiveMatrix A, double alpha, double beta, double gamma, int niters) {
		
		long n = A.rows().count();
		long m = A.columns().count();
		PrimitiveMatrix vector = matrixFactory.makeZero(n, 1).add(1); // make 1 row vector
		PrimitiveMatrix vectorCol = matrixFactory.makeZero(m, 1).add(1); // make 1 row vector
		
		PrimitiveMatrix D = makeDiagonal(A.multiply(vector));
		
		PrimitiveMatrix L = D.subtract(A);
		
		PrimitiveMatrix Dr = matrixFactory.makeEye(n, n);
		PrimitiveMatrix Dw = matrixFactory.makeEye(n, n);
		
		PrimitiveMatrix R = matrixFactory.makeEye(n, n)
									.add(Dr.multiply(beta))
									.add(L.multiply(gamma))
									.invert()
									.multiply(X);
		
		List<Double> obj = new ArrayList<>();
		for(int i=0; i<niters; i++) {
			// update w
			PrimitiveMatrix W = X.multiply(X.transpose())
							.add(Dw.multiply(alpha))
									.invert()
									.multiply(X.multiply(X.transpose())
													.subtract(X.multiply(R.transpose())));
			PhysicalStore<Double> Wtmp = storeFactory.copy(W.multiplyElements(W)
										.multiply(vector));
			Wtmp.modifyAll(PrimitiveFunction.SQRT);
			PhysicalStore<Double> WtmpCopy = Wtmp.copy();
			WtmpCopy.modifyAll(PrimitiveFunction.INVERT);
			Dw = makeDiagonal(matrixFactory.copy(WtmpCopy.multiply(0.5)));

			// update r
			R = matrixFactory.makeEye(n, n)
					.add(Dr.multiply(beta))
					.add(L.multiply(gamma))
					.invert()
					.multiply(X.subtract(W.transpose()
												.multiply(X)));
			
			PhysicalStore<Double> Rtmp = storeFactory.copy(R.multiplyElements(R)
										.multiply(vectorCol));
			Rtmp.modifyAll(PrimitiveFunction.SQRT);
			PhysicalStore<Double> RtmpCopy = Rtmp.copy();
			RtmpCopy.modifyAll(PrimitiveFunction.INVERT);
			Dr = makeDiagonal(matrixFactory.copy(RtmpCopy).multiply(0.5));
		
			obj.add(X.subtract(W.transpose().multiply(X)).subtract(R).norm()+
					sum(Wtmp)*alpha+
					sum(Rtmp)*beta+
					R.transpose().multiply(L).multiply(R).getTrace().multiply(gamma).get());
			if(i >= 2 && (Math.abs(obj.get(i)-obj.get(i-1)))<0.001) {
				break;
			}
		}
		
		return R;
	}
	
	/**
	 * Compute the sum over a vector
	 * @param <N>
	 * @param matrix
	 * @return sum
	 */
	@SuppressWarnings("unchecked")
	private static Double sum(PhysicalStore<Double> vector) {	
		Double scalar = 0.0;
		List<Double> list = (List<Double>) vector.asList();
		for(int i=0;i<list.size();i++) {
			scalar += list.get(i);
		}
		return scalar;
	}
	
	/**
	 * Convert row-vector to diagonal matrix
	 * @param rowVector
	 * @return diagonal matrix
	 */
	private static PrimitiveMatrix makeDiagonal(PrimitiveMatrix rowVector) {
		PhysicalStore<Double> matrix = storeFactory.makeZero(rowVector.columns().count(), rowVector.columns().count());
		PhysicalStore<Double> store = storeFactory.copy(rowVector);
		List<Double> list = store.asList();
		for(int i=0;i<list.size(); i++) {
			for(int j=0;j<list.size();j++) {
				if(i==j)	
					matrix.set(i, j, list.get(i));
			}
		}
		return matrixFactory.copy(matrix);
	}
	
	/**
	 * Print matrix dimensions for debugging purposes
	 * @param bm
	 */
	public static void printSize(PrimitiveMatrix bm) {
		System.out.println(bm.rows().count() + "x" + bm.columns().count());
	}
	
    /**
     * Print matrix for debugging purposes
     * @param bm
     */
    public static void printMatrix(PrimitiveMatrix bm) {
    	PhysicalStore<Double> store = storeFactory.copy(bm);
    	for(int i=0;i<store.columns().count();i++) {
    		for(int j=0;j<store.rows().count();j++) {
    			System.out.print(store.get(i, j)+" ");
    		}
    		System.out.println("");
    	}
    }

}
