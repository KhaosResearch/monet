package org.uma.jmetal.problem.grn.util;

import org.uma.jmetal.problem.grn.util.ann.ActivationFunction;

public class LogisticActivationFunction extends ActivationFunction {


	public double apply(double parameter) {
		
		return 1/(1+Math.pow(Math.E, -parameter));
	}


	public double applyFirstDerivative(double parameter) {
		double value= this.apply(parameter);
		
		return value*(1-value);
	}

	
}
