package org.uma.jmetal.problem.grn.util.ann;

import org.uma.jmetal.problem.grn.util.MathUtils;
import org.uma.jmetal.problem.grn.util.ra.math.RNG;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class FullyConnectedAnn {

	private ArrayList<ArrayList<Neuron>> layers;

	private int layerCount;

	private int[] layerNodeCount;

	private int inputCount;
	private boolean initialised=false;
	private double[] weightRange;
	

	public FullyConnectedAnn(int layerCount, int[] layerNodeCount, int inputCount, double[] weightRange) {

		this.layerCount = layerCount;
		this.layerNodeCount = layerNodeCount;
		this.inputCount = inputCount;
		this.initialised=false;
		this.weightRange=weightRange;
		
	}

	public ArrayList<Double> computeOutput(ArrayList<Double> inputs)
	{
		if(inputs.size()!=this.inputCount)
			throw new InvalidParameterException("Incorrect number of input values.");

		if(!this.initialised)
			this.init();

		ArrayList<Double> result = (ArrayList<Double>)inputs.clone();

		for(int i=0;i<this.layerCount;i++)
		{
			result= this.computeLayerOutput(i, result);
		}

		return result;
	}

	private ArrayList<Double> computeLayerOutput(int layerIndex, ArrayList<Double> input) {

		ArrayList<Double> result= new ArrayList<Double>();
		ArrayList<Neuron> layer= this.layers.get(layerIndex);
		for(int i=0;i<this.layerNodeCount[layerIndex];i++)
			result.add(layer.get(i).calculateOutputValue(input));

		return result;
	}

	private void init()
	{
		//create layers of ann
		this.layers= new ArrayList<ArrayList<Neuron>>();

		int layerInputCount=this.inputCount;
		for(int i=0;i<this.layerCount;i++)
		{
			ArrayList<Neuron> layer= new ArrayList<Neuron>();

			//create layer i
			for(int j=0;j<this.layerNodeCount[i];j++)
			{
				//create Neuron j in layer i
				double[][] range= new double[layerInputCount][2];
				for(int k=0;k<layerInputCount;k++)
					range[k]=this.weightRange;
				
				Neuron neuron = new Neuron(layerInputCount, range);
				
				layer.add(neuron);
			}

			layerInputCount=this.layerNodeCount[i];

			this.layers.add(layer);
		}


		this.initialised=true;
	}

	public double train(ArrayList<ArrayList<Double>> inputs,ArrayList<ArrayList<Double>> outputs, 
			int epochs, double learningRate )
	{
		if(!this.initialised)
			this.init();

		double error=Double.MAX_VALUE;
		//back propagation training algorithm
		int trainingPair= RNG.randomInt(0, inputs.size() - 1);
		//forward step
		ArrayList<Double> netOutputs= this.computeOutput((ArrayList<Double>) inputs.get(trainingPair).clone());
		for(int i=0;i<epochs;i++)
		{


			

			//backward step


			for(int j=this.layerCount-1;j>=0;j--)
			{
				ArrayList<Neuron> layer= this.layers.get(j);
				for(int k=0;k<layer.size();k++)
				{
					double errorDerivative;
					if(j==this.layerCount-1){
						errorDerivative=outputs.get(trainingPair).get(k)-netOutputs.get(k);
					}
					else
					{
						errorDerivative=0;
						for(int l=0;l<this.layerNodeCount[j+1];l++)
						{
							Neuron neuron= this.layers.get(j+1).get(l);
							errorDerivative+=neuron.getBackpropagationValue()*neuron.getInputWeight(k);
						}
					}
					Neuron neuron= layer.get(k);
					neuron.setBackpropagationValue(errorDerivative*neuron.getFunction().applyFirstDerivative(neuron.getOutputValue()));

				}
			}
			//adjust weights
			ArrayList<Double> inputValues= (ArrayList<Double>) inputs.get(trainingPair).clone();
			for(int j=0;j<this.layerCount;j++)
			{
				ArrayList<Neuron> layer= this.layers.get(j);
				
				for(int k=0;k<layer.size();k++)
				{
					Neuron neuron = layer.get(k);
					
					for(int l=0;l<neuron.getInputCount();l++)
					{
						double newWeight=neuron.getInputWeight(l)+learningRate*neuron.getBackpropagationValue()*inputValues.get(l);
						if(newWeight<neuron.getWeightRange()[l][0])
							newWeight=neuron.getWeightRange()[l][0];
						if(newWeight>neuron.getWeightRange()[l][1])
							newWeight=neuron.getWeightRange()[l][1];
						
						neuron.setInputWeight(l, newWeight);
					}
					
				}
				
				for(int k=0;k<layer.size();k++)
					inputValues.set(k, layer.get(k).getOutputValue());
				
			}
			
			//compute global error
			netOutputs= this.computeOutput((ArrayList<Double>) inputs.get(trainingPair).clone());
			ArrayList<Double> difference= MathUtils.AddVector(netOutputs, MathUtils.MultiplyVector(outputs.get(trainingPair), -1));

			error=0;
			for(int j=0;j<difference.size();j++)
			{
				error+=Math.pow(difference.get(j),2);
			}
			error/=2;


			trainingPair=RNG.randomInt(0,inputs.size()-1);

		}
		
				//compute global error
		error=0;
		for(int i=0;i<inputs.size();i++)
		{
			ArrayList<Double> input = inputs.get(i);
			ArrayList<Double> output=this.computeOutput(input);
			
			ArrayList<Double> difference= MathUtils.AddVector(output, MathUtils.MultiplyVector(outputs.get(i), -1));
			for(int j=0;j<difference.size();j++)
			{
				error+=Math.pow(difference.get(j)/outputs.get(i).get(j),2);
			}
			
		}
		
		return error;
	}

	public ArrayList<ArrayList<Neuron>> getLayers() {
		return layers;
	}

	public void setLayers(ArrayList<ArrayList<Neuron>> layers) {
		this.layers = layers;
		this.initialised=true;
	}

	

}
