package org.uma.jmetal.problem.grn.util;

import java.util.ArrayList;

public class MathUtils {

	public static ArrayList<Double> AddVector(ArrayList<Double> a,
			ArrayList<Double> b) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < a.size(); i++) {
			result.add(a.get(i) + b.get(i));
		}
		return result;
	}

	public static ArrayList<Double> MultiplyVector(ArrayList<Double> a, double s) {
		ArrayList<Double> result = new ArrayList<Double>();
		for (int i = 0; i < a.size(); i++) {
			result.add(a.get(i) * s);
		}
		return result;
	}

	public static ArrayList<ArrayList<Double>> AddMatrix(
			ArrayList<ArrayList<Double>> a, ArrayList<ArrayList<Double>> b) {
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < a.size(); i++) {
			result.add(MathUtils.AddVector(a.get(i), b.get(i)));
		}
		return result;
	}

	public static ArrayList<ArrayList<Double>> MultiplyMatrix(
			ArrayList<ArrayList<Double>> a, double s) {
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < a.size(); i++) {
			result.add(MathUtils.MultiplyVector(a.get(i), s));
		}
		return result;
	}

	public static ArrayList<Double> EliminateNegatives(ArrayList<Double> vector) {
		for (int i = 0; i < vector.size(); i++)
			if (vector.get(i) < 0)
				vector.set(i, 0.00001);
		return vector;
	}
	
	public static boolean IsNullVector(ArrayList<Double> vector)
	{
		for(int i=0;i<vector.size();i++)
			if(vector.get(i)!=0)
				return false;
		return true;
				
	}

	public static double Max(ArrayList<Double> arrayList) {
		double max= -Double.MAX_VALUE;
		for(int i=0;i<arrayList.size();i++)
			if(max<arrayList.get(i))
				max=arrayList.get(i);
		return max;
	}

	public static double Min(ArrayList<Double> arrayList) {
		double min= Double.MAX_VALUE;
		for(int i=0;i<arrayList.size();i++)
			if(min>arrayList.get(i))
				min=arrayList.get(i);
		return min;
	}
	
	public static double Average(ArrayList<Double> arrayList) {
		double average=0;
		for(int i=0;i<arrayList.size();i++)
				average+=arrayList.get(i);
		return average/arrayList.size();
	}

}
