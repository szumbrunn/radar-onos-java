package com.abilium.radar;

import java.util.Date;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;

public class DataLog {
	private PrimitiveMatrix rxMatrix;
	private PrimitiveMatrix txMatrix;
	private PrimitiveMatrix adjMatrix;
	private Date date;
	
	public PrimitiveMatrix getRxMatrix() {
		return rxMatrix;
	}
	public void setRxMatrix(PrimitiveMatrix rxMatrix) {
		this.rxMatrix = rxMatrix;
	}
	public PrimitiveMatrix getTxMatrix() {
		return txMatrix;
	}
	public void setTxMatrix(PrimitiveMatrix txMatrix) {
		this.txMatrix = txMatrix;
	}
	public PrimitiveMatrix getAdjMatrix() {
		return adjMatrix;
	}
	public void setAdjMatrix(PrimitiveMatrix adjMatrix) {
		this.adjMatrix = adjMatrix;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
