package org.uma.jmetal.problem.grn.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

//import eva2.server.go.individuals.AbstractEAIndividual;
//import eva2.server.go.populations.Population;

public class TextFileDataFormatter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


//		//kikuchi
		ArrayList<Integer> rateConstants= new ArrayList<Integer>();
		int geneCount=5;

		for(int i=0;i<geneCount;i++)
		{
		rateConstants.add((2*geneCount+2)*(i+1)-2);
		rateConstants.add((2*geneCount+2)*(i+1)-1);
		}
		//MicroarrayData mad= new MicroarrayData("SS5GeneratedData5Noise.txt");
        MicroarrayData mad= new MicroarrayData("/datasets-gnw/comparative/Tominaga5SSGeneratedData.txt");
		mad.initMadFromFile();
//

			String[] files= {"VAR.tsv"};//, "VAR-cmaes-1", "VAR-cmaes-2"};
            files[0] = args[0];

            //System.out.println(files[0]+" "+args[1]);

			double[] parameters=//new double[geneCount*(geneCount*2+2)];
					{0, 0,1,0,-1.0,2,0,0,0,0,5,10,
                     2,0,0,0,0,0,2,0,0,0,10,10,
                     0,-1.0,0,0,0,0,-1.0,2,0,0,10,10,
                      0,0,2,0,-1,0,0,0,2,0,8,10,
                      0,0,0,2,0,0,0,0,0,2,10,10};

			simpleMultigeneRunsStatistics(files, args[1], parameters, rateConstants, mad);
		
	}


	public static void spiethStatistics(String[] files, 
			String outputFileName, ArrayList<ArrayList<Double>> parameters, MicroarrayData mad
	)
	{
		ArrayList<ArrayList<ArrayList<Double>>> solutions;
		ArrayList<Double> fitness;


		solutions= new ArrayList<ArrayList<ArrayList<Double>>>();
		fitness=new ArrayList<Double>();

		for(int i=0;i<files.length;i++)
		{
			try {
				BufferedReader file= new BufferedReader(new FileReader(files[i]));

				boolean finished=false;

				while(!finished)
				{
					try{
						String line= file.readLine();
						String parameterLine=null;
						while(!line.startsWith(" Termination"))
						{
							parameterLine=line;
							line=file.readLine();
						}
						line=file.readLine();
						line=file.readLine();
						line=file.readLine();
						line=line.replace('[',  ' ').replace(']', ' ').trim();
						ArrayList<Integer> structure= new ArrayList<Integer>();
						for(int j=0;j<line.length();j++)
						{
							char[] characterString = {line.charAt(j)};
							structure.add(Integer.parseInt(new String(characterString)));
						}



						String[] splitLine=parameterLine.split("\\s");
						ArrayList<Double> parametersForStructure= new ArrayList<Double>();
						int j=9;
						char[] characterString = {'['};
						while(!splitLine[splitLine.length-j+1].contains(new String(characterString)))
						{
							parametersForStructure.add(Double.parseDouble(
									splitLine[splitLine.length-j].
									replace('[',  ' ').replace(']', ' ').replace(',', ' ').replace(';', ' ').trim()));	
							j++;
						}


						ArrayList<ArrayList<Double>> solution= new ArrayList<ArrayList<Double>>();
						int parameterIndex=0;
						for( j=0;j<mad.getGeneCount();j++)
						{
							ArrayList<Double> gene= new ArrayList<Double>();

							for(int k=0;k<mad.getGeneCount()*2;k++)
							{
								if(structure.get(j*2*mad.getGeneCount()+k)==0)
									gene.add(0.0);
								else
								{
									gene.add(parametersForStructure.get(parametersForStructure.size()-1-parameterIndex));
									parameterIndex++;
								}
							}
							gene.add(parametersForStructure.get(mad.getGeneCount()*2-j*2-1));
							gene.add(parametersForStructure.get(mad.getGeneCount()*2-j*2-2));

							solution.add(gene);
						}


						solutions.add(solution);
						line=file.readLine();
						fitness.add(Double.parseDouble(line.split(" ")[4].replace(',', ' ').trim()));
					}
					catch(NullPointerException e)
					{
						finished=true;
					}
				}
			}catch (Exception e) {

				e.printStackTrace();
			}
		}

		//compute statistics
		ArrayList<Double> sensitivities= new ArrayList<Double>();
		ArrayList<Double> specificities= new ArrayList<Double>();
		ArrayList<Double> ses= new ArrayList<Double>();
		ArrayList<Double> dataSEs= new ArrayList<Double>();

		double pos=0, neg=0;
		for(int i=0;i<parameters.size();i++)
		{
			for(int j=0;j<parameters.get(i).size()-2;j++)
				if(parameters.get(i).get(j)==0)
					neg++;
				else

					pos++;
		}

		double sens=0,spec=0,se=0;
		
		for(int i=0;i<solutions.size();i++)
		{

			for(int j=0;j<solutions.get(i).size();j++)
			{
				int k;
				for( k=0;k<solutions.get(i).get(j).size()-2;k++)
				{
					if(solutions.get(i).get(j).get(k)==0 && parameters.get(j).get(k)==0 )
						spec++;

					if(solutions.get(i).get(j).get(k)!=0 && parameters.get(j).get(k)!=0 )
						sens++;

					se+=Math.pow(solutions.get(i).get(j).get(k)-parameters.get(j).get(k),2);
				
				}
				se+=Math.pow(solutions.get(i).get(j).get(k)-parameters.get(j).get(k),2);
				se+=Math.pow(solutions.get(i).get(j).get(k+1)-parameters.get(j).get(k+1),2);
			}

			spec/=neg;
			sens/=pos;


			sensitivities.add(sens);
			specificities.add(spec);
			ses.add(se);

		}
		ArrayList<Double> variances= new ArrayList<Double>();

		double koRobustness=0,rcRobustness=0;
		for(int i=0;i<parameters.size();i++)
		{
			for(int j=0;j<parameters.get(i).size();j++)
			{
				double mean =0, variance=0;
				for(int k=0;k<solutions.size();k++)
					mean+=solutions.get(k).get(i).get(j);
				mean/=solutions.size();
				for(int k=0;k<solutions.size();k++)
					variance+=Math.pow(mean-solutions.get(k).get(i).get(j), 2);
				variance/=solutions.size();
				variances.add(variance);
				if(j<mad.getGeneCount()*2)
					koRobustness+=variance;
				else
					rcRobustness+=variance;
			}


		}


		koRobustness/=mad.getGeneCount()*mad.getGeneCount();
		rcRobustness/=mad.getGeneCount()*2;


		int dataPoints=0;
		for(int i=0;i<mad.getTimeSeriesCount();i++)
			dataPoints+=mad.getExperimentCount().get(i);
		
		dataPoints*=mad.getGeneCount();

		CompleteSumOfSquaredProcentualErrors evaluator= new CompleteSumOfSquaredProcentualErrors();
		evaluator.setMad(mad);

		for(int i=0;i<solutions.size();i++)
		{

			GRNSSystemModel model =new GRNSSystemModel(solutions.get(i));
			dataSEs.add(evaluator.evaluate(model)/dataPoints);
		}

		//write to file
		try {
			BufferedWriter file= new BufferedWriter(new FileWriter(outputFileName));
			for(int i=0;i<parameters.size();i++)
			{
				for(int j=0;j<parameters.get(i).size();j++)
					file.write(String.valueOf(parameters.get(i).get(j))+ " ");

			}
			file.write("Fitness Se Sensitivity Specificity DataSE ");
			file.newLine();

			for(int i=0;i<solutions.size();i++)
			{ 
				for(int k=0;k<solutions.get(i).size();k++)
				{
					for(int j=0;j<solutions.get(i).get(k).size();j++)
						file.write(String.valueOf(solutions.get(i).get(k).get(j))+ " ");


				}
				file.write(" "+fitness.get(i) +" "+ses.get(i)+" "+sensitivities.get(i)+
						" "+specificities.get(i)+" "+dataSEs.get(i));
				file.newLine();
			}

			file.write(variances.toString());
			file.newLine();
			file.write("Fitness: " + MathUtils.Min(fitness)+"/"+MathUtils.Average(fitness));
			file.newLine();
			file.write("ParameterMSE: " + MathUtils.Min(ses)+"/"+MathUtils.Average(ses));
			file.newLine();
			file.write("DataMSE: " + MathUtils.Min(dataSEs)+"/"+MathUtils.Average(dataSEs));
			file.newLine();
			file.write("Sensitivity: " + MathUtils.Max(sensitivities)+"/"+MathUtils.Average(sensitivities));
			file.newLine();
			file.write("Specificity: " +  MathUtils.Max(specificities)+"/"+MathUtils.Average(specificities));
			file.newLine();
			file.write("Robustness: " +koRobustness+"/"+rcRobustness);

			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void perceptronStatistics(String[] files, 
			String outputFileName, double[] parameters
	)
	{//MSE computed by hand in excel file, this gives SE
		ArrayList<ArrayList<Double>> solutions;
		ArrayList<Double> fitness;

		double robustness=0;
		solutions= new ArrayList<ArrayList<Double>>();
		fitness=new ArrayList<Double>();

		for(int i=0;i<files.length;i++)
		{
			try {
				BufferedReader file= new BufferedReader(new FileReader(files[i]));

				boolean finished=false;

				while(!finished)
				{
					try{
						String line= file.readLine();
						String parameterLine=null;
						while(!line.startsWith(" Termination"))
						{
							parameterLine=line;
							line=file.readLine();
						}
						line=file.readLine();
						line=file.readLine();
						line=file.readLine();
						String[] splitLine= line.split("\\; ");
						ArrayList<Integer> structure= new ArrayList<Integer>();
						for(int j=0;j<splitLine.length;j++)
						{
							structure.add(Integer.parseInt(splitLine[j].replace('[',  ' ').replace(']', ' ').trim()));
						}

						ArrayList<Integer> correctStructure= new ArrayList<Integer>();
						for(int j=0;j<structure.size();j++)
							if(!correctStructure.contains(structure.get(j)))
								correctStructure.add(structure.get(j));

						Integer[] orderedStructure= new Integer[0];
						orderedStructure=correctStructure.toArray(orderedStructure);
						Arrays.sort(orderedStructure);

						splitLine=parameterLine.split("\\s");
						ArrayList<Double> parametersForStructure= new ArrayList<Double>();
						for(int j=0;j<orderedStructure.length;j++)
						{
							parametersForStructure.add(Double.parseDouble(
									splitLine[splitLine.length-orderedStructure.length+j].
									replace('[',  ' ').replace(']', ' ').replace(',', ' ').trim()));
							//look in file if you have the right no of params for structure
						}
						ArrayList<Double> solution= new ArrayList<Double>();
						for(int j=0;j<parameters.length;j++)
						{

							solution.add(0.0);
						}
						for(int j=0;j<orderedStructure.length;j++)
						{
							solution.set(orderedStructure[j], parametersForStructure.get(j));
						}

						solutions.add(solution);
						line=file.readLine();
						fitness.add(Double.parseDouble(line.split(" ")[4].replace(',', ' ').trim()));
					}
					catch(NullPointerException e)
					{
						finished=true;
					}
				}
			}catch (Exception e) {

				e.printStackTrace();
			}
		}

		//compute statistics
		ArrayList<Double> sensitivities= new ArrayList<Double>();
		ArrayList<Double> specificities= new ArrayList<Double>();
		ArrayList<Double> ses= new ArrayList<Double>();

		double pos=0, neg=0;
		for(int i=0;i<parameters.length;i++)
		{
			if(parameters[i]==0)
				neg++;
			else

				pos++;
		}
		for(int i=0;i<solutions.size();i++)
		{
			double sens=0,spec=0,se=0;
			for(int j=0;j<solutions.get(i).size();j++)
			{
				if(solutions.get(i).get(j)==0 && parameters[j]==0 )
					spec++;

				if(solutions.get(i).get(j)!=0 && parameters[j]!=0 )
					sens++;

				se+=Math.pow(solutions.get(i).get(j)-parameters[j],2);
			}
			spec/=neg;
			sens/=pos;


			sensitivities.add(sens);
			specificities.add(spec);
			ses.add(se);
		}
		ArrayList<Double> variances= new ArrayList<Double>();

		for(int i=0;i<parameters.length;i++)
		{
			double mean =0, variance=0;
			for(int j=0;j<solutions.size();j++)
				mean+=solutions.get(j).get(i);
			mean/=solutions.size();
			for(int j=0;j<solutions.size();j++)
				variance+=Math.pow(mean-solutions.get(j).get(i), 2);
			variance/=solutions.size();
			variances.add(variance);

		}


		for(int i=0;i<variances.size();i++)
		{

			robustness+=variances.get(i);

		}

		robustness/=variances.size();



		//write to file
		try {
			BufferedWriter file= new BufferedWriter(new FileWriter(outputFileName));
			for(int i=0;i<parameters.length;i++)
			{
				file.write(String.valueOf(parameters[i])+ " ");

			}
			file.write("Fitness Se Sensitivity Specificity ");
			file.newLine();

			for(int i=0;i<solutions.size();i++)
			{
				file.write(solutions.get(i).toString());
				file.write(" "+fitness.get(i) +" "+ses.get(i)+" "+sensitivities.get(i)+
						" "+specificities.get(i));
				file.newLine();
			}
			file.write(variances.toString());
			file.newLine();
			file.write("Fitness: " + MathUtils.Min(fitness)+"/"+MathUtils.Average(fitness));
			file.newLine();
			file.write("SE: " + MathUtils.Min(ses)+"/"+MathUtils.Average(ses));

			file.newLine();
			file.write("Sensitivity: " + MathUtils.Max(sensitivities)+"/"+MathUtils.Average(sensitivities));
			file.newLine();
			file.write("Specificity: " +  MathUtils.Max(specificities)+"/"+MathUtils.Average(specificities));
			file.newLine();
			file.write("Robustness: " +robustness);

			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void simpleRunsStatistics(String[] files, 
			String outputFileName, Double[] parameters, ArrayList<Integer> rateConstants,
			MicroarrayData mad, int gene)
	{
		ArrayList<ArrayList<Double>> solutions;
		ArrayList<Double> fitness;

		double koRobustness=0, rcRobustness=0;
		solutions= new ArrayList<ArrayList<Double>>();
		fitness=new ArrayList<Double>();

		for(int i=0;i<files.length;i++)
		{
			try {
				BufferedReader file= new BufferedReader(new FileReader(files[i]));

				boolean finished=false;

				while(!finished)
				{
					try{
						String line= file.readLine();
						while(!line.startsWith(" Best solution"))
							line=file.readLine();
						line=file.readLine();
						String[] splitLine= line.split("\\; ");
						ArrayList<Double> solution= new ArrayList<Double>();
						for(int j=0;j<splitLine.length;j++)
						{
							solution.add(Double.parseDouble(splitLine[j].replace('[',  ' ').replace(']', ' ').trim()));
						}
						solutions.add(solution);
						line=file.readLine();
						fitness.add(Double.parseDouble(line.split(" ")[4].replace(',', ' ').trim()));
					}
					catch(NullPointerException e)
					{
						finished=true;
					}
				}
			}catch (Exception e) {

				e.printStackTrace();
			}
		}

		//compute statistics
		ArrayList<Double> sensitivities= new ArrayList<Double>();
		ArrayList<Double> specificities= new ArrayList<Double>();
		ArrayList<Double> ses= new ArrayList<Double>();

		double pos=0, neg=0;
		for(int i=0;i<parameters.length;i++)
		{
			if(parameters[i]==0)
				neg++;
			else
				if(!rateConstants.contains(i))
					pos++;
		}
		for(int i=0;i<solutions.size();i++)
		{
			double sens=0,spec=0,se=0;
			for(int j=0;j<solutions.get(i).size();j++)
			{
				if(solutions.get(i).get(j)==0 && parameters[j]==0 && !rateConstants.contains(j))
					spec++;

				if(solutions.get(i).get(j)!=0 && parameters[j]!=0 && !rateConstants.contains(j))
					sens++;

				se+=Math.pow(solutions.get(i).get(j)-parameters[j],2);
			}
			spec/=neg;
			sens/=pos;


			sensitivities.add(sens);
			specificities.add(spec);
			ses.add(se);
		}
		ArrayList<Double> variances= new ArrayList<Double>();

		for(int i=0;i<parameters.length;i++)
		{
			double mean =0, variance=0;
			for(int j=0;j<solutions.size();j++)
				mean+=solutions.get(j).get(i);
			mean/=solutions.size();
			for(int j=0;j<solutions.size();j++)
				variance+=Math.pow(mean-solutions.get(j).get(i), 2);
			variance/=solutions.size();
			variances.add(variance);

		}


		for(int i=0;i<variances.size();i++)
		{
			if(rateConstants.contains(i))
				rcRobustness+=variances.get(i);
			else
				koRobustness+=variances.get(i);
		}
		rcRobustness/=rateConstants.size();
		koRobustness/=variances.size()-rateConstants.size();

		int dataPoints=0;
		for(int i=0;i<mad.getTimeSeriesCount();i++)
			dataPoints+=mad.getExperimentCount().get(i);
		//data se single gene
		ArrayList<Double> dataSE=new ArrayList<Double>();
		SingleGeneSumOfSquaredProcentualErrorsEvaluator evaluator= new SingleGeneSumOfSquaredProcentualErrorsEvaluator();
		evaluator.setMad(mad);
		for(int i=0;i<solutions.size();i++)
		{

			SingleGeneGrnSSystemModel model =new SingleGeneGrnSSystemModel(mad,gene,solutions.get(i));
			dataSE.add(evaluator.evaluate(model)/dataPoints);
		}

		//data SE multi gene
		/*
		ArrayList<Double> dataSE=new ArrayList<Double>();
		CompleteSumOfSquaredErrorsEvaluator evaluator= new CompleteSumOfSquaredErrorsEvaluator();
		evaluator.setMad(mad);
		for(int i=0;i<solutions.size();i++)
		{
			ArrayList<ArrayList<Double>> solution=new ArrayList<ArrayList<Double>>();
			for(int i=0;i<)
			GRNSSystemModel model =new GRNSSystemModel(solution);
			dataSE.add(evaluator.evaluate(model));
		}*/

		//write to file
		try {
			BufferedWriter file= new BufferedWriter(new FileWriter(outputFileName));
			for(int i=0;i<parameters.length;i++)
			{
				file.write(String.valueOf(parameters[i])+ " ");

			}
			file.write("Fitness ParameterMSe Sensitivity Specificity DATAMSE");
			file.newLine();

			for(int i=0;i<solutions.size();i++)
			{
				file.write(solutions.get(i).toString());
				file.write(" "+fitness.get(i) +" "+ses.get(i)+" "+sensitivities.get(i)+
						" "+specificities.get(i)+" "+dataSE.get(i));
				file.newLine();
			}
			file.write(variances.toString());
			file.newLine();
			file.write("Fitness: " + MathUtils.Min(fitness)+"/"+MathUtils.Average(fitness));
			file.newLine();
			file.write("SE: " + MathUtils.Min(ses)+"/"+MathUtils.Average(ses));
			file.newLine();
			file.write("dataSE: " + MathUtils.Min(dataSE)+"/"+MathUtils.Average(dataSE));
			file.newLine();
			file.write("Sensitivity: " + MathUtils.Max(sensitivities)+"/"+MathUtils.Average(sensitivities));
			file.newLine();
			file.write("Specificity: " +  MathUtils.Max(specificities)+"/"+MathUtils.Average(specificities));
			file.newLine();
			file.write("Robustness: " + koRobustness+"/"+rcRobustness);

			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static void simpleMultigeneRunsStatistics(String[] files,  String outputFileName, double[] parameters, ArrayList<Integer> rateConstants, MicroarrayData mad)
	{
		ArrayList<ArrayList<Double>> solutions;
		ArrayList<Double> fitness;

		double koRobustness=0, rcRobustness=0;
		solutions= new ArrayList<ArrayList<Double>>();
		fitness=new ArrayList<Double>();

		for(int i=0;i<files.length;i++)
		{
			try {
				BufferedReader file= new BufferedReader(new FileReader(files[i]));

				boolean finished=false;

				while(!finished)
				{
					try{
                        String line= file.readLine();
						//while(!line.startsWith(" Best solution"))
						//	line=file.readLine();
						//line=file.readLine();
						String[] splitLine= line.split(" ");
						ArrayList<Double> solution= new ArrayList<Double>();
						for(int j=0;j<splitLine.length;j++)
						{
                            //System.out.println(splitLine[j]);
                            double var = Double.parseDouble(splitLine[j].trim());
							solution.add(Math.abs(var) > 0.01? var : 0.0);
                            //solution.add(var > 0.1? var : 0.0);
						}
						solutions.add(solution);
						//line=file.readLine();
						//fitness.add(Double.parseDouble(line.split(" ")[1].replace(',', ' ').trim()));
					}
					catch(NullPointerException e)
					{
						finished=true;
					}
				}
			}catch (Exception e) {

				e.printStackTrace();
			}
		}

		//compute statistics
		ArrayList<Double> sensitivities= new ArrayList<Double>();
		ArrayList<Double> specificities= new ArrayList<Double>();
		ArrayList<Double> ses= new ArrayList<Double>();

		double pos=0, neg=0;
		for(int i=0;i<parameters.length;i++)
		{
			if(parameters[i]==0)
				neg++;
			else
				if(!rateConstants.contains(i))
					pos++;
		}
		for(int i=0;i<solutions.size();i++)
		{
			double sens=0,spec=0,se=0;
			for(int j=0;j<solutions.get(i).size();j++)
			{
				if(solutions.get(i).get(j)==0 && parameters[j]==0 && !rateConstants.contains(j))
					spec++;

				if(solutions.get(i).get(j)!=0 && parameters[j]!=0 && !rateConstants.contains(j))
					sens++;

				se+=Math.pow(solutions.get(i).get(j)-parameters[j],2);
			}
			spec/=neg;
			sens/=pos;


			sensitivities.add(sens);
			specificities.add(spec);
			ses.add(se);
		}
		ArrayList<Double> variances= new ArrayList<Double>();

		for(int i=0;i<parameters.length;i++)
		{
			double mean =0, variance=0;
			for(int j=0;j<solutions.size();j++)
				mean+=solutions.get(j).get(i);
			mean/=solutions.size();
			for(int j=0;j<solutions.size();j++)
				variance+=Math.pow(mean-solutions.get(j).get(i), 2);
			variance/=solutions.size();
			variances.add(variance);

		}


		for(int i=0;i<variances.size();i++)
		{
			if(rateConstants.contains(i))
				rcRobustness+=variances.get(i);
			else
				koRobustness+=variances.get(i);
		}
		rcRobustness/=rateConstants.size();
		koRobustness/=variances.size()-rateConstants.size();

		int dataPoints=0;
		for(int i=0;i<mad.getTimeSeriesCount();i++)
			dataPoints+=mad.getExperimentCount().get(i);
		

		dataPoints*=mad.getGeneCount();
		//data SE multi gene
		
		ArrayList<Double> dataSE=new ArrayList<Double>();
		CompleteSumOfSquaredErrorsEvaluator evaluator= new CompleteSumOfSquaredErrorsEvaluator();
		evaluator.setMad(mad);
		for(int i=0;i<solutions.size();i++)
		{
			int index=0;
			ArrayList<ArrayList<Double>> solution=new ArrayList<ArrayList<Double>>();
			for(int k=0;k<mad.getGeneCount();k++)
			{
				ArrayList<Double> gene= new ArrayList<Double>();
				for(int j=0;j<mad.getGeneCount()*2+2;j++)
				{
					gene.add(solutions.get(i).get(index));
					index++;
				}
				solution.add(gene);
			}
			GRNSSystemModel model =new GRNSSystemModel(solution);
			dataSE.add(evaluator.evaluate(model)/dataPoints);
		}

		//write to file
		try {
			BufferedWriter file= new BufferedWriter(new FileWriter(outputFileName));
			for(int i=0;i<parameters.length;i++)
			{
				file.write(String.valueOf(parameters[i])+ " ");

			}
			file.write("\nFitness ParameterMSe Sensitivity Specificity DATAMSE\n");
			file.newLine();

			for(int i=0;i<solutions.size();i++)
			{
				file.write("SOLUTION (" + i  + ") " + solutions.get(i).toString());
				//file.write(" "+fitness.get(i) +" "+ses.get(i)+" "+sensitivities.get(i)+
				//		" "+specificities.get(i)+" "+dataSE.get(i));
				file.newLine();

                System.out.println(sensitivities.get(i)+" "+specificities.get(i)+" "+dataSE.get(i));
			}
			file.write("VARIANCES: " + variances.toString());
			file.newLine();
			//file.write("Fitness: " + MathUtils.Min(fitness)+"/"+MathUtils.Average(fitness));
			//file.newLine();
			file.write("SE: " + MathUtils.Min(ses)+"/"+MathUtils.Average(ses));
			file.newLine();
			file.write("dataSE: " + MathUtils.Min(dataSE)+"/"+MathUtils.Average(dataSE));
			file.newLine();
			file.write("Sensitivity: " + MathUtils.Max(sensitivities)+"/"+MathUtils.Average(sensitivities));
			file.newLine();
			file.write("Specificity: " +  MathUtils.Max(specificities)+"/"+MathUtils.Average(specificities));
			file.newLine();
			file.write("Robustness: " + koRobustness+"/"+rcRobustness);

            /*System.out.println("Sensitivity: " + MathUtils.Max(sensitivities)+"/"+MathUtils.Average(sensitivities));
            System.out.println("Specificity: " +  MathUtils.Max(specificities)+"/"+MathUtils.Average(specificities));
            System.out.println("Robustness: " + koRobustness+"/"+rcRobustness);*/

            //System.out.println(MathUtils.Min(dataSE)+" "+MathUtils.Max(sensitivities)+" "+MathUtils.Max(specificities));

            file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static void multipleEARunsToExcelCompatibleFile(String[] files, 
			String outputFileName)
	{
		ArrayList<Integer> functionCalls= new ArrayList<Integer>();
		ArrayList<ArrayList<Double>> bestFitnessRuns= new ArrayList<ArrayList<Double>>();
		ArrayList<String> bestSolutions= new ArrayList<String>();
		ArrayList<String> bestFitness= new ArrayList<String>();

		for(int i=0;i<files.length;i++)
		{
			try {
				BufferedReader file= new BufferedReader(new FileReader(files[i]));

				String line= file.readLine();
				while(!line.startsWith(" Runs performed"))
				{
					ArrayList<Double> run= new ArrayList<Double>();
					while(!line.startsWith("Fit.-calls"))
						line=file.readLine();
					line=file.readLine();

					while(!line.startsWith(" Termination"))
					{
						double fitness=0;
						String[] values=line.split("\t");
						if(bestFitnessRuns.isEmpty())
							functionCalls.add(Integer.parseInt(values[0].trim()));
						values=values[1].split("[;\\[\\] ]");
						for(int j=0;j<values.length;j++)
						{
							if(values[j]!=null&& !values[j].isEmpty())
								fitness+=Double.parseDouble(values[j]);
						}
						run.add(fitness);
						line=file.readLine();
					}
					bestFitnessRuns.add(run);
					line=file.readLine();
					line=file.readLine();
					line=file.readLine();

					line.replace("[", "");
					line.replace("]", "");
					line.replace(";", "");
					bestSolutions.add(line);
					line=file.readLine();
					line.replace(" Best Fitness: [", "");
					line.replace("]", "");
					line.replace(";", "");
					bestFitness.add(line);
					line=file.readLine();
					line=file.readLine();
				}
				file.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		//write to file
		try {
			BufferedWriter file= new BufferedWriter(new FileWriter(outputFileName));
			for(int i=0;i<functionCalls.size();i++)
			{
				file.write(functionCalls.get(i).toString());
				for(int j=0;j<bestFitnessRuns.size();j++)
					file.write(" "+bestFitnessRuns.get(j).get(i).toString());
				file.newLine();

			}
			file.newLine();
			for(int i=0;i<bestSolutions.size();i++)
			{
				file.write(bestSolutions.get(i)+" "+bestFitness.get(i));
				file.newLine();
			}
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void paretoFrontMultipleRunsToExcelCompatibleFile(String[] files, 
			String outputFileName, int maxFunctionCalls)
	{
		ArrayList<Integer> functionCalls= new ArrayList<Integer>();
		ArrayList<ArrayList<Double>> bestFitnessRuns= new ArrayList<ArrayList<Double>>();//best data RMS from pareto front
		ArrayList<ArrayList<String>> bestSolutionRuns= new ArrayList<ArrayList<String>>();

		for(int i=0;i<files.length;i++)
		{
			try {
				BufferedReader file= new BufferedReader(new FileReader(files[i]));


				String line= file.readLine();
				while(line!=null && !line.isEmpty())
				{
					double minFitness=Double.MAX_VALUE;
					String bestInParetorFront="";
					int functionCall=0;
					ArrayList<Double> run = new ArrayList<Double>(); 
					ArrayList<String> runBest = new ArrayList<String>(); 
					while(functionCall!=maxFunctionCalls)
					{
						while(!line.startsWith("*"))
						{
							String[] splitLine= line.split(" ");

							double fitness=0;
							int j=0;
							while(!splitLine[j].contains("fitness"))
							{j++;}
							j+=2;
							while(!splitLine[j].contains("]"))
							{
								fitness+=Double.parseDouble(splitLine[j].split("\\;")[0]);
								j++;
							}

							if(fitness<minFitness)
							{
								minFitness=fitness;
								bestInParetorFront=line;
							}

							line=file.readLine();
						}

						functionCall=Integer.parseInt(line.split("\\*\\*\\*\\*\\*\\*\\*\\*")[1]);
						if(!functionCalls.contains(functionCall))
							functionCalls.add(functionCall);

						run.add(minFitness);
						runBest.add(bestInParetorFront);


						line=file.readLine();
					}

					bestFitnessRuns.add(run);
					bestSolutionRuns.add(runBest);

				}

				file.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		//write to file
		try {
			BufferedWriter file= new BufferedWriter(new FileWriter(outputFileName));
			for(int i=0;i<functionCalls.size();i++)
			{
				file.write(functionCalls.get(i).toString());
				for(int j=0;j<bestFitnessRuns.size();j++)
				{
					file.write(" "+bestFitnessRuns.get(j).get(i).toString());
					//file.write(" "+bestSolutionRuns.get(j).get(i));

				}
				file.newLine();

			}
			for(int j=0;j<bestSolutionRuns.size();j++)
			{
				file.write(bestSolutionRuns.get(j).get(bestSolutionRuns.get(j).size()-1));
				file.newLine();
			}

			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void iteratedEARunsToExcelCompatibleFile(String[] files, 
			String outputFileName, int selectedRun, int generationCount, int[] selectedIterationsCount)
	{
		ArrayList<Integer> functionCalls= new ArrayList<Integer>();
		ArrayList<ArrayList<ArrayList<Double>>> bestFitnessRuns= new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<String> bestSolutions= new ArrayList<String>();
		ArrayList<String> bestFitness= new ArrayList<String>();

		for(int i=0;i<files.length;i++)
		{
			try {
				BufferedReader file= new BufferedReader(new FileReader(files[i]));

				String line= file.readLine();
				int k=0;
				while(line!=null&&!line.startsWith(" Runs performed"))
				{
					try{
						ArrayList<ArrayList<Double>> run= new ArrayList<ArrayList<Double>>();
						while(!line.startsWith("Fit.-calls"))
							line=file.readLine();
						line=file.readLine();

						int selectedIterations=0;
						while(selectedIterations<selectedIterationsCount[k])
						{
							ArrayList<Double> iteration= new ArrayList<Double>();
							for(int j=0;j<selectedRun-1;j++)
							{
								line=file.readLine();//jump over first stage
							}
							int generation=0;
							while(generation<generationCount)
							{
								double fitness=0;
								String[] values=line.split("\t");
								int currentFitnessCalls=Integer.parseInt(values[0].trim());
								if(bestFitnessRuns.isEmpty()&& run.isEmpty())
									functionCalls.add(currentFitnessCalls);
								values=values[1].split("[;\\[\\] ]");
								for(int j=0;j<values.length;j++)
								{
									if(values[j]!=null&& !values[j].isEmpty())
										fitness+=Double.parseDouble(values[j]);
								}
								iteration.add(fitness);
								line=file.readLine();
								generation++;
							}
							run.add(iteration);
							selectedIterations++;
						}
						bestFitnessRuns.add(run);

						while(!line.startsWith(" Best"))
							line=file.readLine();

						line=file.readLine();

						line.replace("[", "");
						line.replace("]", "");
						line.replace(";", "");
						bestSolutions.add(line);
						line=file.readLine();
						line.replace(" Best Fitness: [", "");
						line.replace("]", "");
						line.replace(";", "");
						bestFitness.add(line);

						line=file.readLine();
						line=file.readLine();

						k++;
					} catch(Exception e)//if one run is not finished the reading stops
					{line=null;}
				}
				file.close();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		//write to file
		try {
			BufferedWriter file= new BufferedWriter(new FileWriter(outputFileName));
			for(int k=0;k<bestFitnessRuns.size();k++)
			{
				for(int i=0;i<functionCalls.size();i++)
				{
					file.write(functionCalls.get(i).toString());
					for(int j=0;j<bestFitnessRuns.get(k).size();j++)
						file.write(" "+bestFitnessRuns.get(k).get(j).get(i).toString());
					file.newLine();

				}
			}
			file.newLine();
			for(int i=0;i<bestSolutions.size();i++)
			{
				file.write(bestSolutions.get(i)+" "+bestFitness.get(i));
				file.newLine();
			}
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

