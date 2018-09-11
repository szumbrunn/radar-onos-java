package com.abilium.radar;

import java.util.Date;

import org.ojalgo.matrix.BasicMatrix;

public class DataLog {
	private BasicMatrix rxMatrix;
	private BasicMatrix txMatrix;
	private BasicMatrix adjMatrix;
	private Date date;
	
	public BasicMatrix getRxMatrix() {
		return rxMatrix;
	}
	public void setRxMatrix(BasicMatrix rxMatrix) {
		this.rxMatrix = rxMatrix;
	}
	public BasicMatrix getTxMatrix() {
		return txMatrix;
	}
	public void setTxMatrix(BasicMatrix txMatrix) {
		this.txMatrix = txMatrix;
	}
	public BasicMatrix getAdjMatrix() {
		return adjMatrix;
	}
	public void setAdjMatrix(BasicMatrix adjMatrix) {
		this.adjMatrix = adjMatrix;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
