package org.uma.jmetal.problem.grn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class IOUtils {

	public static void writeVectorLine(Writer writer, ArrayList<Double> vector) {
		for (int i = 0; i < vector.size(); i++)
			try {
				writer.write(vector.get(i) + " ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static ArrayList<double[]> readIndividuals(BufferedReader reader) {
		ArrayList<double[]> result = new ArrayList<double[]>();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				String[] splitLine = line.split(" ");
				double[] individualParams = new double[splitLine.length];
				for (int i = 0; i < splitLine.length; i++)
					individualParams[i] = Double.parseDouble(splitLine[i]);
				result.add(individualParams);

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
