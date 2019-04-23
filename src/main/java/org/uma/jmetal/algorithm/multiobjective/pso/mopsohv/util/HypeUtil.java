package org.uma.jmetal.algorithm.multiobjective.pso.mopsohv.util;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HypeUtil {

  /**
	 * Partition the population into fronts
	 * 
	 * @param solutionList
	 * @return solutionList
	 */
	public static <S extends Solution<?>> List<S> generateFrontPartition(List<S> solutionList) {
		List<S> result = null;
		if (solutionList != null && solutionList.size() > 0) {
			result = new ArrayList<>() ;
			int added = 0;
			int[] checked = new int[solutionList.size()];
			int[] actchecked = new int[solutionList.size()];
			boolean notDominated = false;
			int j = 0;
			int index = 0;
			while (added < solutionList.size() && index < solutionList.size()) {
				for (int i = 0; i < solutionList.size(); i++) {
					if (checked[i] == 1)
						continue;
					actchecked[i] = 0;
					notDominated = true;
					j = 0;
					while (notDominated && j < solutionList.size()) {
						if (i != j && checked[j] == 0
								&& dominates(solutionList.get(j), solutionList.get(i))) {
							notDominated = false;
						}
						j++;
					}
					if (notDominated) {
						actchecked[i] = 1;
						result.add(solutionList.get(i));
						added++;
					}
				}
				for (int z = 0; z < solutionList.size(); z++) {
					if (actchecked[z] == 1) {
						checked[z] = 1;
					}
				}
				index++;
			}
		}
		return result;
	}

	/**
	 * Determines if one individual dominate another. Minimizing fitness values
	 * 
	 * @param solutionA
	 * @param solutionB
	 * @return
	 */
	public static <S extends Solution<?>> boolean dominates(S solutionA, S solutionB) {
		boolean result = false;
		if (solutionA != null && solutionB != null) {
			boolean aIsWorse = false;
			boolean equal = true;
			int cont = solutionA.getNumberOfObjectives();
			for (int i = 0; i < cont && !aIsWorse; i++) {
				aIsWorse = solutionA.getObjective(i) > solutionB.getObjective(i);
				equal = (solutionA.getObjective(i) == solutionB.getObjective(i)) && equal;
			}
			result = (!equal && !aIsWorse);
		}
		return result;
	}

	/**
	 * Calculates the fitness of all individuals in population based on the hype
	 * indicator
	 * 
	 * @param population
	 * @param bound
	 * @param nrOfSamples
	 * @param dim
	 * @return
	 */
	public static <S extends Solution<?>> List<S> hypeFitnessMating(List<S> population,
                                                                    Integer bound, Integer nrOfSamples, Integer dim) {
    Fitness<S> solutionFitness = new Fitness<>();

    List<S> result = null;
		if (population != null && bound != null && nrOfSamples != null
				&& dim != null) {
			result = new ArrayList<>();
			// int [] indices = getIndices(population.size());
			double[] points = null;
			double[] val = null;
			Iterator<S> it = population.iterator();
			points = getObjectiveArray(it, population.size() * dim, dim);
			val = hypeIndicator(population.size(), 0, bound, nrOfSamples,
					population.size(), points, dim, bound);
			it = population.iterator();
			int i = 0;
			while (it.hasNext()) {
				S aux = it.next();
        solutionFitness.setAttribute(aux, val[i]);
				//aux.setFitness(val[i]);
				result.add(aux);
				i++;
			}
		}
		return result;
	}

	/**
	 * Determine the HypeIndicator \f[ \sum_{i=1}^k \left(\prod_{j=1}^{-1}
	 * \frac{k-j}{P|-j}) \frac{Leb(H_i(a))}{i}\f] if nrOfSamples < 0, then do
	 * exact calculation, else sample the indicator.
	 * 
	 * @param popSize
	 *            size of the population P
	 * @param lowerBound
	 *            scalar denoting the lower vertex of the sampling box
	 * @param upperBound
	 *            scalar denoting the upper vertex of the sampling box
	 * @param nrOfSamples
	 *            the total number of samples or, if negative, flag the exact
	 *            calculation should be used
	 * @param paramK
	 *            the variable k
	 * @param points
	 *            matrix of all objective values dim*popSize entries
	 * @return vector of all indicator values
	 */
	public static double[] hypeIndicator(int popSize, double lowerBound,
			double upperBound, int nrOfSamples, int paramK, double[] points,
			int dim, double bound) {
		double[] result = null;
		// weight coefficients
		double[] rho = new double[paramK + 1];
		// set alpha
		rho[0] = 0;
		for (int i = 1; i <= paramK; i++) {
			rho[i] = 1.0d / (i);
			for (int j = 1; j <= i - 1; j++) {
				rho[i] *= (paramK - j) / (popSize - j);
			}
		}

		if (nrOfSamples < 0) {
			result = hypeExact(result, popSize, lowerBound, upperBound, paramK,
					points, rho, dim, bound);
		} else {
			result = hypeSampling(result, popSize, lowerBound, upperBound,
					nrOfSamples, paramK, points, rho, dim);
		}
		return result;
	}

	/**
	 * Calculating the hypeIndicator \f[ \sum_{i=1}^k \left( \prod_{j=1}^{i-1}
	 * \frac{k-j}{|P|-j} \right) \frac{Leb(H_i(a))}{i} \f]
	 * 
	 * @param popSize
	 *            size of the population P
	 * @param lowerBound
	 *            scalar denoting the lower vertex of the sampling box
	 * @param upperBound
	 *            scalar denoting the upper vertex of the sampling box
	 * @param paramK
	 *            the variable k
	 * @param points
	 *            matrix of all objective values dim*popSize entries
	 * @param rho
	 *            weight coefficients
	 * @return vector of all indicator values
	 */
	public static double[] hypeExact(double[] val, int popSize,
			double lowerBound, double upperBound, int paramK, double[] points,
			double[] rho, int dim, double bound) {
		double[] result = null;
		double[] boundsVec = new double[dim];
		int[] indices = new int[popSize];
		for (int i = 0; i < dim; i++) {
			boundsVec[i] = bound;
		}
		for (int i = 0; i < popSize; i++) {
			indices[i] = i;
		}
		// recursively calculate the indicator values
		result = hypeExactRecursive(points, popSize, dim, popSize, dim - 1,
				boundsVec, indices, val, rho, paramK);
		return result;
	}

	/**
	 * Sampling the hypeIndicator \f[ \sum_{i=1}^k \left(\prod_{j=1}^{i-1}
	 * \frac{k-j} {|p|-j} \right) \frac{ Leb(H_i(a))}{i} \f]
	 * 
	 * @param val
	 *            vector of all indicators
	 * @param popSize
	 *            size of the population |P|
	 * @param lowerBound
	 *            scalar denoting the lower vertex of the sampling box
	 * @param upperBound
	 *            scalar denoting the upper vertex of the sampling bos
	 * @param nrOfSamples
	 *            the total number of samples
	 * @param paramK
	 *            the variable k
	 * @param points
	 *            points matrix of all objective values dim*popSize entries
	 * @param rho
	 *            weight coefficients
	 * @return vector of all indicators
	 */
	public static double[] hypeSampling(double[] val, int popSize,
			double lowerBound, double upperBound, int nrOfSamples, int paramK,
			double[] points, double[] rho, int dim) {

		if (popSize >= 0 && lowerBound <= upperBound && paramK >= 1
				&& paramK <= popSize) {
			int[] hitstat = new int[popSize];
			int domCount;
			double[] sample = new double[dim];

			for (int s = 0; s < nrOfSamples; s++) {
				for (int k = 0; k < dim; k++) {
					double random = JMetalRandom.getInstance().nextDouble();
					sample[k] = lowerBound
							+ (random * (upperBound - lowerBound));
				}
				domCount = 0;
				for (int i = 0; i < popSize; i++) {
					if (weaklyDominates(points, sample, dim)) {
						domCount++;
						if (domCount > paramK)
							break;
						hitstat[i] = 1;
					} else {
						hitstat[i] = 0;
					}
				}
				if (domCount > 0 && domCount <= paramK) {
					for (int i = 0; i < popSize; i++) {
						if (hitstat[i] == 1) {
							val[i] += rho[domCount];
						}
					}
				}
			}
			for (int i = 0; i < popSize; i++) {
				val[i] = val[i] * Math.pow((upperBound - lowerBound), dim)
						/ nrOfSamples;
			}
		}
		return val;
	}

	public static boolean weaklyDominates(double[] point1, double[] point2,
			int noObjectives) {
		boolean result = true;
		int i = 0;
		while (i < noObjectives && result) {
			result = point1[i] <= point2[i];
			i++;
		}
		return result;
	}

	public static double[] hypeExactRecursive(double[] inputP, int pnts,
			int dim, int nrOfPnts, int actDim, double[] bounds,
			int[] inputPVec, double[] fitness, double[] rho, int paramK) {
		// double[] result = Arrays.copyOf(fitness, fitness.length);
		double extrusion;
		int[] pVec = new int[pnts];
		double[] p = new double[pnts * dim];
		if (fitness == null) {
			fitness = new double[pnts * dim];
		}
		if (inputPVec != null && inputPVec.length > pnts * dim) {
			for (int i = 0; i < pnts * dim; i++) {
				fitness[i] = 0;
				pVec[i] = inputPVec[i];
			}
		}
		if (inputP != null && inputP.length > pnts * dim) {
			for (int i = 0; i < pnts * dim; i++) {
				p[i] = inputP[i];
			}
		}
		pVec = rearrangeIndiceByColumn(p, nrOfPnts, dim, actDim, pVec);
		for (int i = 0; i < nrOfPnts; i++) {
			if (i < nrOfPnts - 1) {
				extrusion = p[(pVec[i + 1]) * dim + actDim]
						- p[pVec[i] * dim + actDim];
			} else {
				extrusion = bounds[actDim] - p[pVec[i] * dim + actDim];
			}
			if (actDim == 0) {
				if (i + 1 <= paramK) {
					for (int j = 0; j <= i; j++) {
						fitness[pVec[j]] = fitness[pVec[j]] + extrusion
								* rho[i + 1];
					}
				}
			} else if (extrusion > 0) {
				double[] tmpFit = new double[pnts];
				tmpFit = hypeExactRecursive(p, pnts, dim, i + 1, actDim - 1,
						bounds, pVec, tmpFit, rho, paramK);
				if (tmpFit != null && tmpFit.length >= pnts) {
					for (int j = 0; j < pnts; j++) {
						fitness[j] += extrusion * tmpFit[j];
					}
				}
			}
		}
		return fitness;
	}

	/**
	 * Internal function used by hypeExact
	 */
	private static int[] rearrangeIndiceByColumn(double[] mat, int rows,
			int columns, int col, int[] ind) {
		int[] result = Arrays.copyOf(ind, ind.length);

		int MAX_LEVELS = 300;
		int[] beg = new int[MAX_LEVELS];
		int[] end = new int[MAX_LEVELS];
		int i = 0, L, R, swap;
		double pref, pind;
		double[] ref = new double[rows];
		beg[0] = 0;
		end[0] = rows;
		while (i >= 0) {
			L = beg[i];
			R = end[i] - 1;
			if (L < R) {
				pref = ref[L];
				pind = result[L];
				while (L < R) {
					while (ref[R] >= pref && L < R) {
						R--;
					}
					if (L < R) {
						ref[L] = ref[R];
						result[L++] = result[R];
					}
					while (ref[L] <= pref && L < R) {
						L++;
					}
					if (L < R) {
						ref[R] = ref[L];
						result[R--] = result[L];
					}
				}// end while
				ref[L] = pref;
				result[L] = (int) pind;
				beg[i + 1] = L + 1;
				end[i + 1] = end[i];
				end[i++] = L;
				if (end[i] - beg[i] > end[i - 1] - beg[i - 1]) {
					swap = beg[i];
					beg[i] = beg[i - 1];
					beg[i - 1] = swap;
					swap = end[i];
					end[i] = end[i - 1];
					end[i - 1] = swap;
				}
			}// end if
			else {
				i--;
			}
		}
		return result;
	}

	/**
	 * Get a objective
	 * 
	 * @param it
	 * @param size
	 * @return
	 */
	public static <S extends Solution<?>> double[] getObjectiveArray(Iterator<S> it, int size,
                                                                     int dim) {
		double[] result = null;
		if (it != null && it.hasNext()) {
			result = new double[size];
			int i = 0;
			while (it.hasNext()) {
				Solution solution = it.next();
				if (solution != null && solution.getNumberOfObjectives() > 0) {
					for (int j = 0; j < solution.getNumberOfObjectives(); j++) {
						result[i * dim + j] = solution.getObjective(j);
					}
				}
				i++;
			}
		}
		return result;
	}

	public static <S extends Solution<?>> List<S> initializationFitness(int size, Problem<S> problem) {
    Fitness<S> solutionFitness = new Fitness<>();

    List<S> result = new ArrayList<>() ;
		try {
			for (int i = 0; i < size; i++) {
				S solution = problem.createSolution();
				solutionFitness.setAttribute(solution, 0.0d);
				//solution.setFitness(0.0d);
				result.add(solution);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Iteratively remove individuals from front based on sampled hypeIndicatos
	 * value
	 * 
	 * @param population
	 *            the current front operate on
	 * @param alpha
	 *            the number of individuals in population after removal
	 * @param bound
	 *            scaler denoting the upper bound
	 * @param nrOfSample
	 *            the number of Sample per iteration.If negative, use exact
	 *            hypeIndicator calculation
	 * @pre fp.size >0
	 * @pre partp.size >= alpha
	 * @post partp.size == alpha
	 */
	public static <S extends Solution<?>> List<S> hypeReduction(List<S> population, int alpha,
                                                                double bound, int nrOfSample, int dim) {
		List<S> result = null;
		if (population != null && population.size() > 0 && dim > 0) {// pre
			double[] val = new double[population.size()];
			double[] points = new double[population.size() * dim];
			double chechRate;
			double minRate = -1;
			int sel = -1;
			result = new ArrayList<>(alpha);
			int cont = 0;
			if (population.size() > 0) {
				points = getObjectiveArray(population.iterator(),
						population.size() * dim, dim);
				while (result.size() != alpha) {
					val = hypeIndicator(population.size(), 0, bound,
							nrOfSample, population.size() - alpha, points, dim,
							bound);
					if (val != null && val.length > 0) {
						sel = -1;
						for (int i = 0; i < population.size(); i++) {
							chechRate = val[i];
							if (sel == -1 || chechRate < minRate) {
								minRate = chechRate;
								sel = i;
							}
						}
					}
					S aux = population.get(cont);
					// if(points!=null && points.length<cont){
					// aux.setFitness(points[cont]);
					// }
					result.add(aux);
					if (sel >= 0 && sel < population.size()) {
						if (result.size() <= sel && result.size() > 0) {
							result.remove(sel);
						}
					}
					cont++;
					if ((cont == population.size() - 1)
							&& (population.size() > result.size())
							&& result.size() < alpha) {
						for (int i = population.size(); i != alpha; i--) {
							result.add(population.get(i));
						}
					}
				}
			}
		}
		return result;
	}
}
