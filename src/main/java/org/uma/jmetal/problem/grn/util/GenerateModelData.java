package org.uma.jmetal.problem.grn.util;

import org.uma.jmetal.problem.grn.util.ann.FullyConnectedAnn;
import org.uma.jmetal.problem.grn.util.ann.Neuron;
import org.uma.jmetal.problem.grn.util.ra.math.RNG;

import java.io.*;
import java.util.ArrayList;

//import eva2.server.go.populations.Population;

public class GenerateModelData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//Generate model parameters 

		//		GenerateSSystemParameters(10,3,2,-1,1,1,1,"SS10Params.txt",0.1,0.5);
		//		//GenerateANNParameters(10,3,2,-3,3, "ANN10Params.txt",0.1,0.5);
		//
		//		GenerateSSystemParameters(20,5,5,-1,1,1,1,"SS20Params.txt",0.1,0.5);
		//		//GenerateANNParameters(20,3,2,-3,3, "ANN20Params.txt",0.1,0.5);
		//
		//		GenerateSSystemParameters(30,5,7,-1,1,1,1,"SS30Params.txt",0.1,0.5);
		//		//GenerateANNParameters(30,3,2,-3,3, "ANN30Params.txt",0.1,0.5);
		//
		//		GenerateSSystemParameters(50,5,10,-1,1,1,1,"SS50Params.txt",0.1,0.5);
		//		//GenerateANNParameters(50,5,10,-3,3, "ANN50Params.txt",0.1,0.5);
		//
		//		GenerateSSystemParameters(100,5,10,-1,1,1,1,"SS100Params.txt",0.1,0.5);
		//		//GenerateANNParameters(100,5,20,-3,3, "ANN100Params.txt",0.1,0.5);
		//
		//		GenerateSSystemParameters(500,5,20,-1,1,0.5,0.5,"SS500Params.txt",0.1,0.5, 2);
		//		//GenerateANNParameters(500,5,20,-3,3, "ANN500Params.txt",0.1,0.5);


		//generate data for parameters

		{
			ArrayList<ArrayList<Double>> parameters;


			//S7
			ArrayList<ArrayList<Double>> s7InitialValues= new ArrayList<ArrayList<Double>>();
			double[] array={0.141463415, 0.42195122, 0.092682927, 0.953658537 ,0.056097561 ,0.690243902 ,0.465853659};
			ArrayList<Double> timeSeriesInitialValues= new ArrayList<Double>();
			for(int i=0;i<array.length;i++)
				timeSeriesInitialValues.add(array[i]);

			s7InitialValues.add(timeSeriesInitialValues);

						//S7 kikuchi
						{
							parameters= ReadSSParameters("params7_1.txt");
			
							GenerateSSData("data7_1.txt", parameters,1,18,0.07,s7InitialValues);
							
							parameters= ReadSSParameters("params7_2.txt");
							
							GenerateSSData("data7_2.txt", parameters,1,18,0.07,s7InitialValues);
							
							parameters= ReadSSParameters("params7_3.txt");
							
							GenerateSSData("data7_3.txt", parameters,1,18,0.07,s7InitialValues);
							
							parameters= ReadSSParameters("params7_4.txt");
							
							GenerateSSData("data7_4.txt", parameters,1,18,0.07,s7InitialValues);
							parameters= ReadSSParameters("params7_5.txt");
							
							GenerateSSData("data7_5.txt", parameters,1,18,0.07,s7InitialValues);
						}
//			//S7 spieth
//						{
//							parameters= ReadSSParameters("S7SpiethParams.txt");
//			
//							GenerateSSData("S7SpiethGenData.txt", parameters,1,18,0.07,s7InitialValues);
//						}

			MicroarrayData mad=new MicroarrayData("S7Scaled.txt");
			mad.initMadFromFile();
			double[] CLN2InitialValues={0.056097561};
			//			//keedwel
			//			{
			//				double[] geneParams= {11.156676241209718	,-14.9968077,	0,	2.670270816	,0,	0,	1.6608119435336608};
			//				GenerateANNGeneSeries("S7ANN.txt", geneParams, 1, 18, CLN2InitialValues, mad,2);
			//			}
			//			//kimura
			//			{
			//				double[] geneParams= {2.3028471670193644,0.296688906,0,-0.125933852,-1.873911525,2.952859063,0,-1.125786441,4.820413671,0,0,-0.387315446,-0.465024254,0,15.08315198,9.963497512311715};
			//				GenerateSSGeneSeries("S7Kimura.txt", geneParams, 1, 18, 0.07, CLN2InitialValues, mad,2);
			//			}
			//			//noman
			//			{
			//double[] geneParams= {0.0768869,-4.72333,0,0,0,0,0,-1.29793,0,-1.55362,1.71925,2.79442,-1.33931,-2.35071,0.0408934,0.281036};
			//				GenerateSSGeneSeries("S7Noman.txt", geneParams, 1, 18, 0.07, CLN2InitialValues, mad,4);
			//			}
		}


		{
			ArrayList<ArrayList<Double>> parameters;


			//S6
			ArrayList<ArrayList<Double>> s6InitialValues= new ArrayList<ArrayList<Double>>();
			double[] array={0.34261242, 0.385438972, 0.286937901 ,0.561027837, 0.511777302 ,0.565310493 };
			ArrayList<Double> timeSeriesInitialValues= new ArrayList<Double>();
			for(int i=0;i<array.length;i++)
				timeSeriesInitialValues.add(array[i]);

			s6InitialValues.add(timeSeriesInitialValues);

						//S6 kikuchi
						{
							parameters= ReadSSParameters("params6_1.txt");
							
							GenerateSSData("data6_1.txt", parameters,1,18,0.07,s6InitialValues);
							
							parameters= ReadSSParameters("params6_2.txt");
							
							GenerateSSData("data6_2.txt", parameters,1,18,0.07,s6InitialValues);
							
							parameters= ReadSSParameters("params6_3.txt");
							
							GenerateSSData("data6_3.txt", parameters,1,18,0.07,s6InitialValues);
							
							parameters= ReadSSParameters("params6_4.txt");
							
							GenerateSSData("data6_4.txt", parameters,1,18,0.07,s6InitialValues);
							
							parameters= ReadSSParameters("params6_5.txt");
							
							GenerateSSData("data6_5.txt", parameters,1,18,0.07,s6InitialValues);
						}
			//S6 spieth
			//			{
			//				parameters= ReadSSParameters("s6SpiethParams.txt");
			//
			//				GenerateSSData("S6SpiethGenData.txt", parameters,1,18,0.07,s6InitialValues);
			//			}

			MicroarrayData mad=new MicroarrayData("S6Scaled.txt");
			mad.initMadFromFile();
			double[] PHO5InitialValues={0.34261242};
			//keedwel
			//			{
			//				double[] geneParams= {4.009223923635274,	1.951556181,	0	,-5.610602623	,0,	0.0};
			//				GenerateANNGeneSeries("S6ANN.txt", geneParams, 1, 18, PHO5InitialValues, mad,0);
			//			}
			//kimura
			//			{
			//				double[] geneParams= //{0.0,0,0.695460504,0,0.694988467,0.310108923,0.353943458,0.06225328,0,0,0,0,16.4758239,6.968729091667122};
			//				{-11.528876753724418,0.0,11.860940178604585,7.103739312986412,0.0,2.713514183111882,0.0,0.0,0.0,10.690037149085077,0.42448412094999555,7.073319670926798,0.7472774189111842,2.7414219407734435};
			//					GenerateSSGeneSeries("S6Kimura.txt", geneParams, 1, 18, 0.07, PHO5InitialValues, mad,0);
			//			}
			//			//noman
			//			{
			//				double[] geneParams= {5.919267725757359, -9.185078700683896, 2.0559708206885476, -2.521354689067514, 10.689454383718841, -3.492524351642617, 3.716636856502256, 7.887208619774884, 13.58179085970937};
			//				GenerateSimplifiedSSGeneSeries("S6Noman.txt", geneParams, 1, 18, 0.07, PHO5InitialValues, mad,0);
			//			}
		}




		{
			//S9
			double[] CLN1InitialValues={0.007116212,	0.021463415,0.056097561};

			MicroarrayData mad=new MicroarrayData("S9MixedScaled.txt");//yeast9Scaled.txt");
			mad.initMadFromFile();

			//noman
			//			{
			//				double[] geneParams= {0.17875,	-0.171398,	0.205862,	-0.142152,	-0.025396	,-0.131739	,0.407031	,0.322246,	0.326289			,				0.835721	,		192.562	,208.998};
			//				GenerateSimplifiedSSGeneSeries("yeast9NomansSS4.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,6);
			//			}
			//			{
			//				double[] geneParams= {0.729287,	0,	1.37573,	-0.467775,	0.345795,	0.651026	,-2.99608,	2.0264,	0.0591169			,				0.243078	,		13.164,	20};
			//				GenerateSimplifiedSSGeneSeries("yeast9NomansSS2.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,6);
			//			}
			//			{
			//				double[] geneParams= {0.202967,	-0.196789,	0.234826	,-0.152751,	-0.0268818,	-0.144404,	0.34911,	0.358332,	0.371168,							0.827467	,		171.347	,183.443};
			//				GenerateSimplifiedSSGeneSeries("yeast9NomansSS3.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,6);
			//			}
			//			{
			//double[] geneParams= {3,2.6083,0,0,-1.52544,0.354486,0,0,0,2.46337,0,0,0,-3,0,3,-1.75073,1.07422,4.07894,20};
			//				GenerateSSGeneSeries("yeast9NomanSS1.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,6);
			//			}
			//			{
			//double[] geneParams= {1.22966,2.4365,0,0.163719,0,0,0,0,-1.72336,0,0.338032,0,-0.320129,-2.46956,0,3,-1.76322,0,10.1405,20};
			//				GenerateSSGeneSeries("yeast9NomanSS2.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,6);
			//			}
			//			{
			//double[] geneParams= {3,0,0,0.343185,2.17819,0,-3,2.48623,0,0,0,-2.62725,0,0,0,3,-1.12247,-0.277143,2.33505,0.203299};
			//				GenerateSSGeneSeries("yeast9NomanSS3.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,6);
			//			}
						{
			double[] geneParams= {-2.04361,0.633026 ,2.13727 ,-0.310948, -0.595326, 1.44875 ,-5, 3.34246 ,0.521709, 0.485767, 17.0461, 20};
							GenerateSimplifiedSSGeneSeries("yeast9mixedNoman.txt", geneParams, 3, 15, 0.016, CLN1InitialValues, mad,6);
						}
		}

		{
			//S17
			double[] CLN1InitialValues={0.2329,	0.2904};

			MicroarrayData mad=new MicroarrayData("yeast17Scaled.txt");
			mad.initMadFromFile();
			//			
//			//noman
//			{
//double[] geneParams= {0,0,0,3,-2.85696,0,-1.51573,0,0,0,0,2.4,0,0,0,0,0,0,0,0,0,0,0,-2.4,0,2.64835,-0.215298,0,0,0,-1.92,0,0,0,20,12.5916};
//				GenerateSSGeneSeries("yeast17NomanSS1.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,8);
//			}
//			//noman
//			{
//double[] geneParams= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.09048,0,0,0,0,0,0.636141,0,0.974465,0,0,3,-2.27774,-0.480677,0,0,0,1.18526,0.822782,-0.827259,11.7958,206.672};
//				GenerateSSGeneSeries("yeast17NomanSS2.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,8);
//			}
//			//noman
//			{
//double[] geneParams= {0.873521,0,0.634995,-0.247952,0,-0.683673,0,1.02793,0.073689,0,0.146295,0,0,0,0.691212,0,0.329298,0.491663,36.6945,24.7029};
//				GenerateSimplifiedSSGeneSeries("yeast17NomansSS1.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,8);
//			}
//			//noman
//			{
//double[] geneParams= {1.41745,2.97576,0,1.18533,0,-1.364,0,2.88825,-0.994579,0,0,-0.0132342,0,-1.37206,0.542882,0,0,0.269324,3.0061,20};
//				GenerateSimplifiedSSGeneSeries("yeast17NomansSS2.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,8);
//			}
			//noman
//			{
//double[] geneParams= {3,-3,0,-3,0,0,0,2.59777,-3,1.81946,0,0,0,3,0,1.78781,1.72935,3,0.0234597,0.0001};
//				GenerateSimplifiedSSGeneSeries("yeast17NomansSS5.txt", geneParams, 2, 15, 0.016, CLN1InitialValues, mad,8);
//			}
		}

		{
			ArrayList<ArrayList<Double>> parameters;


			//S24
			ArrayList<ArrayList<Double>> s24InitialValues= new ArrayList<ArrayList<Double>>();
			double[] array={0.690243902,0.465853659,0.141463415,0.42195122,0.341463415,0.390243902,0.99999,
					0.502439024,0.414634146,0.0000001,0.492682927,0.092682927,0.197560976,0.953658537,0.448780488,
					0.275609756,0.056097561,0.412195122,0.34261242,0.385438972,0.286937901,0.561027837,0.511777302,0.565310493 };
			ArrayList<Double> timeSeriesInitialValues= new ArrayList<Double>();
			for(int i=0;i<array.length;i++)
				timeSeriesInitialValues.add(array[i]);

			s24InitialValues.add(timeSeriesInitialValues);

			//			//S24 kikuchi
			//			{
			//			parameters= ReadSSParameters("S24KikuchiParams.txt");
			//			
			//			GenerateSSData("S24KikuchiGenData.txt", parameters,1,18,0.07,s6InitialValues);
			//			}
			//S24 spieth
			//			{
			//				parameters= ReadSSParameters("S24SpiethParams.txt");
			//
			//				GenerateSSData("S24SpiethGenData.txt", parameters,1,18,0.07,s24InitialValues);
			//			}

			MicroarrayData mad=new MicroarrayData("S24Scaled.txt");
			mad.initMadFromFile();
			double[] PHO5InitialValues={0.34261242};
			//			//keedwel
			//			{
			//				double[] geneParams= {0.0,-3.917122515,0,0,0,0,0,1.815159669,0,0,0,0,0,0,0,0,0,0,
			//						4.394157742,0,0,0.169620891,0,-2.3742067784309655};
			//				GenerateANNGeneSeries("S24PHO5ANN.txt", geneParams, 1, 18, PHO5InitialValues, mad,18);
			//			}
			//kimura
			//			{
			//				double[] geneParams= {0.0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-15,-2.406979158
			//						,8.550827201,1.71874281,0,8.538426313,7.832307965,-15,0,0,0,0,0,0,0,0,-2.252837234,0,
			//						10.2337562,0,4.607334572,0,0,0,0,12.73725794,0,0,4.387358967,0,0,13.56083258,7.098845563491216};
			//				GenerateSSGeneSeries("S24PHO5Kimura.txt", geneParams, 1, 18, 0.07, PHO5InitialValues, mad,18);
			//			}
			//noman
			{
				//				double[] geneParams= {0.0, 0.0, 0.0, 0.0, 0.0, 0.8827036722017191, 0.212047779092658, 0.0, 0.0, 0.0, 0.0,
				//				0.0, 0.0, 0.0, 0.0, -2.0359475342751994, 0.0, -9.732983651788649, 0.0, 6.559831733855925, 0.0, 0.0,
				//				4.056539967177378, 0.0, 0.08702415946851502, 0.4161079204953598, 2.4739234264660284};
				//				GenerateSimplifiedSSGeneSeries("S24PHO5Noman.txt", geneParams, 1, 18, 0.07, PHO5InitialValues, mad,18);

			}

			double[] CLN2InitialValues={0.056097561};
			//			//keedwel
			//			{
			//				double[] geneParams= {5.422857640478998,0,8.99993399,0,0,0,0,-9.130933479,0,0
			//						,0,0,0,0,0,0,0,0,0,0,-3.644749878,0,0,-0.9829396278515242};
			//				GenerateANNGeneSeries("S24CLN2ANN.txt", geneParams, 1, 18, CLN2InitialValues, mad,11);
			//			}
			//			//kimura
			//			{
			//				double[] geneParams= {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -7.529660601824614, 1.0793686273689065,
			//						-0.011922619710504958, 0.029291185114946883, 0.18610132867229615, 0.19533926097928372,
			//						0.01494823245225499, 0.139341984, -0.10803688733657733, 0.48495662870035317, 0.2419271089690075,
			//						3.7401624557164586, 3.2939434224700475, 0.3465332905354497, 0.29402148834680253, 1.2367981421968286,
			//						3.6879940639235707, 0.9925476525672093, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			//						0.0, 0.0, 0.0, -2.8916965052901813, 0.0, 2.444043864701652, 0.2735620494410798, 0.9408639240209506,
			//						10.524175372993803, 0.15980632664452082,
			//						0.015773339100395523, 0.04580414754342341, 3.346574356361953, 19.625702885672048};
			//				GenerateSSGeneSeries("S24CLN2Kimura.txt", geneParams, 1, 18, 0.07, CLN2InitialValues, mad,11);
			//			}
//			//noman
//			{
//double[] geneParams= {0,0,0,0.0401916,0,0,0,0,0,0,0.0552709,0,0,0,-0.123394,0,0,0,0,0.0534013,0,0,0,0,0,0,-0.0727253,0,0,0,0,0,0,0,0,0,0,0,0,0,0.0549722,0,0,0,0,0,0.0595241,0.0231245,71.6628,73.5919};
//				GenerateSSGeneSeries("S24CLN1NomanSS2.txt", geneParams, 1, 18, 0.07, CLN2InitialValues, mad,16);
//
//			}{
//double[] geneParams= {0,0,0,0,0,0,0,0,-5,0,0.910759,2.31755,4.80569,0,0,0.950103,-5,0,0,0,0,0,0,0,0,0,0,0,0,-0.422797,0,0,0,0,0,0,0,0,1.55448,0,0,0,0,0,0,0,0,0,0.886697,6.43423};
//				GenerateSSGeneSeries("S24CLN1NomanSS1.txt", geneParams, 1, 18, 0.07, CLN2InitialValues, mad,16);
//			}{
//double[] geneParams= {0,0,3.8972,0,0,0,0.0478162,0,0,0,0,0.0195226,-1.67906,0.924813,0,-0.756814,0,0,0,0,-2.12962,0,0,0.399119,1.01958,5.71108,6.32612};
//				GenerateSimplifiedSSGeneSeries("S24CLN1NomansSS1.txt", geneParams, 1, 18, 0.07, CLN2InitialValues, mad,16);
//			}{
//double[] geneParams= {4.04382,0,5,0,0,-4.13003,0,0,0,0,0,0,0,1.15346,-0.510643,1.03804,-2.83154,0,0,0,0,0,0.130659,0,-0.562244,10,0.940744};
//				GenerateSimplifiedSSGeneSeries("S24CLN1NomansSS2.txt", geneParams, 1, 18, 0.07, CLN2InitialValues, mad,16);
//
//			}
		}





		//SS5
		//		parameters= ReadSSParameters("Tominaga5SSParams.txt");
		//		GenerateSSData("Tominaga5SSGeneratedData.txt", parameters,3,20,0.05);

		//SS10
		//		parameters= ReadSSParameters("SS10Params.txt");
		//		GenerateSSData("SS10GeneratedData.txt", parameters,3,20,0.05);
		//
		//		//SS20
		//		parameters= ReadSSParameters("SS20Params.txt");
		//		GenerateSSData("SS20GeneratedData.txt", parameters,3,20,0.05);
		//
		//		//SS30
		//		parameters= ReadSSParameters("SS30Params.txt");
		//		GenerateSSData("SS30GeneratedData.txt", parameters,4,20,0.01);
		//
		//		//SS50
		//		parameters= ReadSSParameters("SS50Params.txt");
		//		GenerateSSData("SS50GeneratedData.txt", parameters, 5,25,0.01);
		//
		//		//SS100
		//		parameters= ReadSSParameters("SS100Params.txt");
		//		GenerateSSData("SS100GeneratedData.txt", parameters,3,20,0.01);
		//
		//		//SS500
		//		parameters= ReadSSParameters("SS500Params.txt");
		//		GenerateSSData("SS500GeneratedData.txt", parameters,3,20,0.01);
		//

		//		//ANN5
		//		parameters=ReadPerceptronParameters("Perceptron5Params.txt");
		//		GeneratePerceptronData("Perceptron5GeneratedData.txt", parameters,3,20,0.05);
		//
		//		//ANN10
		//		parameters=ReadPerceptronParameters("Ann10Params.txt");
		//		GeneratePerceptronData("Ann10GeneratedData.txt", parameters,3,20,0.05);
		//		
		//		//ANN20
		//		parameters=ReadPerceptronParameters("Ann20Params.txt");
		//		GeneratePerceptronData("Ann20GeneratedData.txt", parameters,3,20,0.05);
		//		
		//		//ANN30
		//		parameters=ReadPerceptronParameters("Ann30Params.txt");
		//		GeneratePerceptronData("Ann30GeneratedData.txt", parameters,3,20,0.05);
		//
		//		//ANN50
		//		parameters=ReadPerceptronParameters("Ann50Params.txt");
		//		GeneratePerceptronData("Ann50GeneratedData.txt", parameters,3,20,0.05);
		//
		//		//ANN100
		//		parameters=ReadPerceptronParameters("Ann100Params.txt");
		//		GeneratePerceptronData("Ann100GeneratedData.txt", parameters,3,20,0.01);
		//
		//		//ANN500
		//		parameters=ReadPerceptronParameters("Ann500Params.txt");
		//		GeneratePerceptronData("Ann500GeneratedData.txt", parameters,3,20,0.01);
	}







	private static void GenerateLinearData(String fileName, ArrayList<ArrayList<Double>> parameters) {

		//ArrayList<ArrayList<Double>> parameters = new ArrayList<ArrayList<Double>>();
		try {

			BufferedReader reader= new BufferedReader(new
					FileReader("Linear5Params.txt"));

			String line = reader.readLine();
			int geneCount = Integer.parseInt(line);
			for (int i = 0; i < geneCount; i++) {
				line = reader.readLine();
				String[] splitLine = line.split(" ");
				ArrayList<Double> geneParams = new ArrayList<Double>();
				for (int j = 0; j <  geneCount ; j++) {
					geneParams.add(Double.parseDouble(splitLine[j]));

				}
				parameters.add(geneParams);

			}
			GRNLinearModel model = new GRNLinearModel(parameters);
			BufferedWriter writer= new BufferedWriter(new
					FileWriter("Linear5GeneratedData.txt"));

			writer.write(geneCount);
			int timeSeries=1;
			writer.write(" " + timeSeries);
			for(int k=0;k<timeSeries;k++)
			{

				int expCount = 110;

				double timeSpan= 0.01;

				writer.newLine();
				writer.write( expCount);
				writer.newLine();
				writer.write("0");
				for (int i = 1; i < expCount; i++) {
					writer.write(" " + timeSpan);
				}

				ArrayList<Double> startValues = new ArrayList<Double>();

				startValues.add(RNG.randomDouble(0.1, 0.4)); startValues.add(RNG.randomDouble(0.1, 0.4)); startValues.add(RNG.randomDouble(0.1, 0.4));
				startValues.add(RNG.randomDouble(0.1, 0.4)); startValues.add(RNG.randomDouble(0.1, 0.4));




				writer.newLine();

				IOUtils.writeVectorLine(writer, startValues);
				for (int i = 1; i < expCount; i++) {
					writer.newLine();
					startValues = model.getValuesAfterTimeSpan(startValues,
							timeSpan);
					IOUtils.writeVectorLine(writer, startValues);

				}
			}
			writer.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void GeneratePerceptronData(String fileName, ArrayList<ArrayList<Double>> parameters,
			int timeSeriesCount, int expCount,double timeSpan) {


		try {

			int geneCount=parameters.size();

			int[] annLayersNodeCount=new int[1];
			annLayersNodeCount[0]=1;

			double minWeightValue=-2;
			double maxWeightValue=2;

			double[] range={minWeightValue,maxWeightValue};
			ArrayList<FullyConnectedAnn> models= new ArrayList<FullyConnectedAnn>();
			for(int i=0;i<geneCount;i++)
			{
				FullyConnectedAnn model = new FullyConnectedAnn(1,annLayersNodeCount,geneCount, range);

				Neuron neuron= new Neuron(geneCount,parameters.get(i));

				ArrayList<ArrayList<Neuron>> layers = new ArrayList<ArrayList<Neuron>>();
				ArrayList<Neuron> layer= new ArrayList<Neuron>();
				layer.add(neuron);
				layers.add(layer);

				model.setLayers(layers);

				models.add(model);
			}
			BufferedWriter writer= new BufferedWriter(new
					FileWriter(fileName));

			writer.write(String.valueOf(geneCount));

			writer.write(" " + String.valueOf(timeSeriesCount));
			for(int k=0;k<timeSeriesCount;k++)
			{




				writer.newLine();
				writer.write(String.valueOf(expCount));
				writer.newLine();
				writer.write("0");
				for (int i = 1; i < expCount; i++) {
					writer.write(" " + String.valueOf(timeSpan));
				}

				ArrayList<Double> startValues = new ArrayList<Double>();

				for(int i=0;i<geneCount;i++)
					startValues.add(RNG.randomDouble(0.1, 0.4));




				writer.newLine();

				IOUtils.writeVectorLine(writer, startValues);
				for (int i = 1; i < expCount; i++) {
					writer.newLine();
					ArrayList<Double> newValues=  new ArrayList<Double>();
					for(int j=0;j<geneCount;j++)
					{
						newValues.add( models.get(j).computeOutput(startValues).get(0));

					}
					IOUtils.writeVectorLine(writer, newValues);
					startValues=newValues;
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void GenerateSSData(String fileName, ArrayList<ArrayList<Double>> parameters,
			int timeSeriesCount, int expCount,double timeSpan, ArrayList<ArrayList<Double>> initialValues)
	{

		try {


			GRNSSystemModel ssystem = new GRNSSystemModel(parameters);
			BufferedWriter writer= new BufferedWriter(new
					FileWriter(fileName));
			//BufferedWriter writer = new BufferedWriter(new FileWriter(
			//	"Tominaga2SSGeneratedData.txt"));
			writer.write(String.valueOf(parameters.size()));


			writer.write(" "+String.valueOf(timeSeriesCount));
			for(int k=0;k<timeSeriesCount;k++)
			{

				// Tominaga5d

				// Tominaga2d
				//double timeSpan = 0.04;
				writer.newLine();
				writer.write(String.valueOf(expCount));
				writer.newLine();
				writer.write("0");
				for (int i = 1; i < expCount; i++) {
					writer.write(" " + String.valueOf(timeSpan));
				}

				ArrayList<Double> startValues = new ArrayList<Double>();
				if(initialValues==null)
				{
					for(int i=0;i<parameters.size();i++)
						startValues.add(RNG.randomDouble(0.1, 0.7)); 

				}
				else
				{
					startValues=initialValues.get(k);
				}

				writer.newLine();

				IOUtils.writeVectorLine(writer, startValues);
				for (int i = 1; i < expCount; i++) {
					writer.newLine();
					startValues = ssystem.getValuesAfterTimeSpan(startValues,
							timeSpan);
					IOUtils.writeVectorLine(writer, startValues);

				}
			}
			writer.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static ArrayList<ArrayList<Double>>  ReadSSParameters(String fileName)
	{


		ArrayList<ArrayList<Double>> parameters= new ArrayList<ArrayList<Double>>();
		try {

			BufferedReader reader= new BufferedReader(new
					FileReader(fileName));
			//BufferedReader reader = new BufferedReader(new FileReader(
			//	"Tominaga2SSParams.txt"));
			String line = reader.readLine();
			int geneCount = Integer.parseInt(line);
			for (int i = 0; i < geneCount; i++) {
				line = reader.readLine();
				String[] splitLine = line.split("\\s");
				ArrayList<Double> geneParams = new ArrayList<Double>();
				for (int j = 0; j < 2 * geneCount + 2; j++) {
					geneParams.add(Double.parseDouble(splitLine[j]));

				}
				parameters.add(geneParams);

			}
		} catch(Exception e){e.printStackTrace();}
		return parameters;
	}

	private static ArrayList<ArrayList<Double>>  ReadPerceptronParameters(String fileName)
	{

		ArrayList<ArrayList<Double>> parameters= new ArrayList<ArrayList<Double>>();
		try{
			BufferedReader reader= new BufferedReader(new
					FileReader(fileName));

			String line = reader.readLine();
			int geneCount = Integer.parseInt(line);
			for (int i = 0; i < geneCount; i++) {
				line = reader.readLine();
				String[] splitLine = line.split("\\s");
				ArrayList<Double> geneParams = new ArrayList<Double>();
				for (int j = 0; j <  geneCount ; j++) {
					geneParams.add(Double.parseDouble(splitLine[j]));

				}
				parameters.add(geneParams);

			}
		}
		catch(Exception e)
		{e.printStackTrace();}
		return parameters;
	}

	private static void GenerateSSystemParameters(int geneCount, int maxConnectivity, int hubCount,
			double minKinetic, double maxKinetic, double minRate, double maxRate, String fileName,
			double lessConnectionsProbability, double hubProbability, double degradationKineticCoeff)
	{

		ArrayList<ArrayList<Double>> result= new ArrayList<ArrayList<Double>>();


		ArrayList<Integer> hubs= GenerateHubs(hubCount, geneCount);


		for(int i=0;i<geneCount;i++)
		{
			ArrayList<Double> geneParameters= new ArrayList<Double>();
			for(int j=0;j<geneCount*2+2;j++)
				geneParameters.add(0.0);

			ArrayList<Integer> regulators= GenerateRegulators(geneCount, maxConnectivity,
					lessConnectionsProbability, hubProbability, hubs);




			for(int j=0;j<regulators.size();j++)
			{

				//choose between activating and repressing terms

				geneParameters.set(regulators.get(j),RNG.randomDouble(minKinetic, maxKinetic));



			}

			//set parameter for self inhibition factor
			geneParameters.set(i+geneCount,degradationKineticCoeff);

			geneParameters.set(geneCount*2, RNG.randomDouble(minRate, maxRate));
			geneParameters.set(geneCount*2+1, RNG.randomDouble(minRate, maxRate));

			result.add(geneParameters);
		}
		//write parameters to file
		WriteParameters(fileName, result);
	}

	private static void WriteParameters(String fileName,ArrayList<ArrayList<Double>> parameters)
	{
		try {
			BufferedWriter writer= new BufferedWriter(new
					FileWriter(fileName));
			writer.write(String.valueOf(parameters.size()));

			for(int i=0;i<parameters.size();i++)
			{
				writer.newLine();
				int j;
				for(j=0;j<parameters.get(i).size()-1;j++)
				{
					writer.write(parameters.get(i).get(j)+" ");

				}
				writer.write(parameters.get(i).get(j).toString());
			}
			writer.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private static ArrayList<Integer> GenerateRegulators(int geneCount, int maxConnectivity, 
			double lessConnectionsProbability, double hubProbability, ArrayList<Integer> hubs)
			{
		ArrayList<Integer> regulators= new ArrayList<Integer>();


		for(int j=0;j<maxConnectivity;j++)
		{
			//the prob space is split in 3: [0,lessConnectionsProbability] means this connection does not exist
			// (lessConnectionsProbability,lessConnectionsProbability+hubProbability] means this connection is to a hub
			//(lessConnectionsProbability+hubProbability,1] means this connection is to a non hub
			double randomValue= RNG.randomDouble(0, 1);

			if(randomValue>lessConnectionsProbability)
			{//connection exists
				int regulator;
				do{
					if(randomValue<=lessConnectionsProbability+hubProbability && regulators.containsAll(hubs))
					{//connection to a hub
						regulator=hubs.get(RNG.randomInt(hubs.size()));
					}
					else
					{//connection to a normal gene
						regulator=RNG.randomInt(geneCount);
						while(hubs.contains(regulator))
							regulator=RNG.randomInt(geneCount);

					}
				}
				while(regulators.contains(regulator));
				regulators.add(regulator);
			}
		}
		return regulators;
			}


	private static ArrayList<Integer> GenerateHubs(int hubCount, int geneCount)
	{
		ArrayList<Integer> hubs= new ArrayList<Integer>();
		for(int i=0;i<hubCount;i++)
		{
			int hub=RNG.randomInt(0, geneCount-1);
			while(hubs.contains(hub))
				hub=RNG.randomInt(0, geneCount-1);
			hubs.add(hub);
		}
		return hubs;
	}

	private static void GenerateANNParameters(int geneCount, int connectivity, int hubCount,
			int min, int max, String fileName,double lessConnectionsProbability, double hubProbability) 
	{
		ArrayList<ArrayList<Double>> result= new ArrayList<ArrayList<Double>>();


		ArrayList<Integer> hubs= GenerateHubs(hubCount, geneCount);


		for(int i=0;i<geneCount;i++)
		{
			ArrayList<Double> geneParameters= new ArrayList<Double>(geneCount);

			for(int j=0;j<geneCount;j++)
				geneParameters.add(0.0);

			ArrayList<Integer> regulators= GenerateRegulators(geneCount, connectivity,
					lessConnectionsProbability, hubProbability, hubs);

			for(int j=0;j<regulators.size();j++)
				geneParameters.set(regulators.get(j),RNG.randomDouble(min, max));

			result.add(geneParameters);
		}

		//write parameters to file
		WriteParameters(fileName, result);

	}

	private static void GenerateSSGeneSeries(String fileName, double[] geneParams, int timeSeriesCount, int expCount,
			double timeSpan, double[] initialValues, MicroarrayData mad,int gene)
	{//generate from single gene model
		try {

			ArrayList<Double> parameters= new ArrayList<Double>();
			for(int i=0;i<geneParams.length;i++)
				parameters.add(geneParams[i]);

			SingleGeneGrnSSystemModel ssystem = new SingleGeneGrnSSystemModel(mad,gene,parameters);
			BufferedWriter writer= new BufferedWriter(new
					FileWriter(fileName));


			for(int k=0;k<timeSeriesCount;k++)
			{

				IOUtils.writeVectorLine(writer, ssystem.simulate(k));

			}
			writer.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void GenerateSimplifiedSSGeneSeries(String fileName, double[] geneParams, int timeSeriesCount, int expCount,
			double timeSpan, double[] initialValues, MicroarrayData mad,int gene)
	{//generate from single gene model
		try {

			ArrayList<Double> parameters= new ArrayList<Double>();
			for(int i=0;i<geneParams.length;i++)
				parameters.add(geneParams[i]);

			SingleGeneSimplifiedSSModel ssystem = new SingleGeneSimplifiedSSModel(mad,gene,parameters);
			BufferedWriter writer= new BufferedWriter(new
					FileWriter(fileName));


			for(int k=0;k<timeSeriesCount;k++)
			{

				IOUtils.writeVectorLine(writer, ssystem.simulate(k));

			}
			writer.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void GenerateANNGeneSeries(String fileName, double[] geneParams, int timeSeriesCount, int expCount,
			double[] initialValues, MicroarrayData mad,int gene)
	{//generate from single gene model

		try {



			int[] annLayersNodeCount=new int[1];
			annLayersNodeCount[0]=1;




			ArrayList<Double> parameters= new ArrayList<Double>();
			for(int i=0;i<geneParams.length;i++)
				parameters.add(geneParams[i]);

			FullyConnectedAnn model = new FullyConnectedAnn(1,annLayersNodeCount,mad.getGeneCount(), null);

			Neuron neuron= new Neuron(mad.getGeneCount(),parameters);

			ArrayList<ArrayList<Neuron>> layers = new ArrayList<ArrayList<Neuron>>();
			ArrayList<Neuron> layer= new ArrayList<Neuron>();
			layer.add(neuron);
			layers.add(layer);

			model.setLayers(layers);




			BufferedWriter writer= new BufferedWriter(new
					FileWriter(fileName));



			writer.write(" " + String.valueOf(timeSeriesCount));
			for(int k=0;k<timeSeriesCount;k++)
			{


				writer.write(String.valueOf(initialValues[k]));
				ArrayList<Double> startValues = new ArrayList<Double>();
				for(int i=0;i<mad.getGeneCount();i++)
					startValues.add(mad.getMad().get(k).get(i).get(0));

				for (int i = 1; i < expCount; i++) {


					double newValue= model.computeOutput(startValues).get(0);


					writer.write(" "+String.valueOf(newValue));
					startValues.clear();
					for(int j=0;j<mad.getGeneCount();j++)
						startValues.add(mad.getMad().get(k).get(j).get(i));
					startValues.set(gene, newValue);
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
