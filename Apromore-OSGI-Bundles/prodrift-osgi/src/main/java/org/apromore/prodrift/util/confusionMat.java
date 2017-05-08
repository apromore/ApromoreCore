
package org.apromore.prodrift.util;

public class confusionMat {
	
	private int tP=0,fP=0,fN=0;
	private double mDelay=0;
	
	public confusionMat(int tPos, int fPos, int fNeg, double meanDelay) {
		tP = tPos;
		fP = fPos;
		fN = fNeg;
		mDelay = meanDelay;
	}
	
	
	public double getFScore() {
		return (double)(2*tP)/(2*tP+fP+fN);
	}
	
	public double getPrecision() {
		return (double)(tP)/(tP+fP);
	}
	
	public double getRecall() {
		return (double)(tP)/(tP+fN);
	}
	
	public double getMeanDelay() {
		return mDelay;
	}


	public int gettP() {
		return tP;
	}


	public void settP(int tP) {
		this.tP = tP;
	}


	public int getfP() {
		return fP;
	}


	public void setfP(int fP) {
		this.fP = fP;
	}


	public int getfN() {
		return fN;
	}


	public void setfN(int fN) {
		this.fN = fN;
	}


	public double getmDelay() {
		return mDelay;
	}


	public void setmDelay(double mDelay) {
		this.mDelay = mDelay;
	}
	
	
}
