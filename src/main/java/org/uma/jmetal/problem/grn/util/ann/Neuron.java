package org.uma.jmetal.problem.grn.util.ann;

import org.uma.jmetal.problem.grn.util.LogisticActivationFunction;
import org.uma.jmetal.problem.grn.util.ra.math.RNG;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Neuron {
	
	

	private double[][] weightRange;
	public Neuron(int inputCount, double[][] range) {
		
		this.inputCount = inputCount;
		this.inputWeights= new ArrayList<Double>();
		this.weightRange=range;
		
		for(int i=0;i<this.inputCount;i++)
		{
			this.inputWeights.add(RNG.randomDouble(this.weightRange[i][0], this.weightRange[i][1]));
		}
		
	}

	public Neuron(int inputCount, ArrayList<Double> inputWeights) {
		this.inputCount = inputCount;
		this.inputWeights=inputWeights;
		
	}

	private ActivationFunction function= new LogisticActivationFunction();
	public ActivationFunction getFunction() {
		return function;
	}

	public void setFunction(ActivationFunction function) {
		this.function = function;
	}

	private ArrayList<Double> inputWeights;
	private int  inputCount;
	public double[][] getWeightRange() {
		return weightRange;
	}

	public void setWeightRange(double[][] weightRange) {
		this.weightRange = weightRange;
	}

	public double getInputWeight(int i) {
		return inputWeights.get(i);
	}

	public void setInputWeight(int index,double value) {
		this.inputWeights.set(index, value);
	}

	public int getInputCount() {
		return inputCount;
	}

	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}

	public double getOutputValue() {
		return outputValue;
	}

	private double outputValue;
	
	private double backpropagationValue;
	
	public double calculateOutputValue(ArrayList<Double> inputs)
	{
		if(inputs.size()!=this.inputCount)
			throw new InvalidParameterException("Number of paramters must be equal to number of inputs");
		double sum=0;
		for(int i=0;i<inputs.size();i++)
			sum+=inputs.get(i)*inputWeights.get(i);
		this.outputValue=function.apply(sum);
		return this.outputValue;
	}

	public void setBackpropagationValue(double backpropagationValue) {
		this.backpropagationValue = backpropagationValue;
	}

	public double getBackpropagationValue() {
		return backpropagationValue;
	}

	public ArrayList<Double> getInputWeights() {
		
		return this.inputWeights;
	}
}
