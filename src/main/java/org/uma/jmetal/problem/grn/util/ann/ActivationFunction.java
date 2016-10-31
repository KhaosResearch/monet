package org.uma.jmetal.problem.grn.util.ann;

public abstract class ActivationFunction {

	public abstract double apply(double parameter);
	
	public abstract double applyFirstDerivative(double parameter);
}
