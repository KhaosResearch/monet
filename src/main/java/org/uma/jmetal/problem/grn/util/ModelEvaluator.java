package org.uma.jmetal.problem.grn.util;

import java.io.Serializable;

public abstract class ModelEvaluator implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1479473682713499418L;
	MicroarrayData mad;
	
	public MicroarrayData getMad() {
		return mad;
	}

	public void setMad(MicroarrayData mad) {
		this.mad = mad;
	}

	
	
}
