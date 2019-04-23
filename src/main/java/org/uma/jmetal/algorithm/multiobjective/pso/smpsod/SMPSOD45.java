//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.algorithm.multiobjective.pso.smpsod;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD.FunctionType;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.algorithm.multiobjective.pso.smpsod.util.NeighborType;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class implements the SMPSO algorithm described in:
 * SMPSO: A new PSO-based metaheuristic for multi-objective optimization
 * MCDM 2009. DOI: http://dx.doi.org/10.1109/MCDM.2009.4938830
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMPSOD45 implements Algorithm<List<DoubleSolution>> {
  private DoubleProblem problem;
  private List<DoubleSolution> swarm ;

  private double c1Max;
  private double c1Min;
  private double c2Max;
  private double c2Min;
  private double r1Max;
  private double r1Min;
  private double r2Max;
  private double r2Min;
  private double weightMax;
  private double weightMin;
  private double changeVelocity1;
  private double changeVelocity2;

  private int swarmSize;
  private int maxIterations;
  private int iterations;

  /** Z vector in Zhang & Li paper */
  protected double[] idealPoint;
  /** Lambda vectors */
  protected double[][] lambda;
  /** T in Zhang & Li paper */
  protected int neighborSize;
  protected int[][] neighborhood;
  /** Delta in Zhang & Li paper */
  protected double neighborhoodSelectionProbability;
  /** nr in Zhang & Li paper */
  protected int maximumNumberOfReplacedSolutions;

  protected List<DoubleSolution> localBest ;
  protected List<DoubleSolution> globalBest ;

  protected Solution<?>[] indArray;
  protected FunctionType functionType;

  protected String dataDirectory;

  private GenericSolutionAttribute<DoubleSolution, DoubleSolution> localBestAttribute;
  private double[][] speed;

  private JMetalRandom randomGenerator;

  private Comparator<DoubleSolution> dominanceComparator;

  private MutationOperator<DoubleSolution> mutation;

  private double deltaMax[];
  private double deltaMin[];

  private SolutionListEvaluator<DoubleSolution> evaluator;

  /**
   * Constructor
   */
  public SMPSOD45(DoubleProblem problem, int swarmSize,
                  MutationOperator<DoubleSolution> mutationOperator, int maxIterations, double r1Min, double r1Max,
                  double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max,
                  double weightMin, double weightMax, double changeVelocity1, double changeVelocity2,
                  SolutionListEvaluator<DoubleSolution> evaluator) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.mutation = mutationOperator;
    this.maxIterations = maxIterations;

    this.r1Max = r1Max;
    this.r1Min = r1Min;
    this.r2Max = r2Max;
    this.r2Min = r2Min;
    this.c1Max = c1Max;
    this.c1Min = c1Min;
    this.c2Max = c2Max;
    this.c2Min = c2Min;
    this.weightMax = weightMax;
    this.weightMin = weightMin;
    this.changeVelocity1 = changeVelocity1;
    this.changeVelocity2 = changeVelocity2;

    randomGenerator = JMetalRandom.getInstance();
    this.evaluator = evaluator;

    dominanceComparator = new DominanceComparator<DoubleSolution>();
    localBestAttribute = new GenericSolutionAttribute<DoubleSolution, DoubleSolution>();
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    localBest = new ArrayList<>() ;
    globalBest = new ArrayList<>() ;

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      deltaMax[i] = (problem.getUpperBound(i) - problem.getLowerBound(i)) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }

    indArray = new Solution[problem.getNumberOfObjectives()];
    neighborhood = new int[swarmSize][neighborSize];
    idealPoint = new double[problem.getNumberOfObjectives()];
    lambda = new double[swarmSize][problem.getNumberOfObjectives()];

    functionType = FunctionType.TCHE ;
  }

  public void run() {
    swarm = createInitialSwarm() ;
    evaluateSwarm(swarm) ;
    initializeVelocity(swarm);
    initializeParticlesMemory(swarm);
    initializeLeader(swarm);

    initializeUniformWeight();
    initializeNeighborhood();
    initializeIdealPoint();

    initProgress();
    while (!isStoppingConditionReached()) {
      updateVelocity(swarm);
      updatePosition(swarm);

      for (int i = 0; i < problem.getNumberOfVariables(); i++) {
        NeighborType type ;
        if (JMetalRandom.getInstance().nextDouble() < neighborhoodSelectionProbability) {
          type = NeighborType.NEIGHBOR ;
        } else {
          type = NeighborType.POPULATION ;
        }

        updateLocalBest(swarm.get(i), i, NeighborType.NEIGHBOR);
        updateGlobalBest(i, NeighborType.POPULATION);
      }

      updateProgress();
    }
  }


  protected void initProgress() {
    iterations = 1;
  }

  protected void updateProgress() {
    iterations += 1;
  }

  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  protected void initializeLeader(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      globalBest.add(swarm.get(i));
      updateGlobalBest(i, NeighborType.POPULATION);
    }
  }

  protected void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  protected void initializeParticlesMemory(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      localBest.add((DoubleSolution) particle.copy()) ;
    }
  }

  protected void updateVelocity(List<DoubleSolution> swarm) {
    double r1, r2, c1, c2;
    double wmax, wmin;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution) localBest.get(i).copy() ;
      bestGlobal = (DoubleSolution) globalBest.get(i).copy() ;

      r1 = randomGenerator.nextDouble(r1Min, r1Max);
      r2 = randomGenerator.nextDouble(r2Min, r2Max);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);
      wmax = weightMax;
      wmin = weightMin;

      for (int var = 0; var < particle.getNumberOfVariables(); var++) {
        speed[i][var] = velocityConstriction(constrictionCoefficient(c1, c2) * (
                inertiaWeight(iterations, maxIterations, wmax, wmin) * speed[i][var] +
                    c1 * r1 * (bestParticle.getVariableValue(var) - particle.getVariableValue(var)) +
                    c2 * r2 * (bestGlobal.getVariableValue(var) - particle.getVariableValue(var))),
            deltaMax, deltaMin, var);
      }
    }
  }

  protected void updatePosition(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < particle.getNumberOfVariables(); j++) {
        particle.setVariableValue(j, particle.getVariableValue(j) + speed[i][j]);

        if (particle.getVariableValue(j) < problem.getLowerBound(j)) {
          particle.setVariableValue(j, problem.getLowerBound(j));
          speed[i][j] = speed[i][j] * changeVelocity1;
        }
        if (particle.getVariableValue(j) > problem.getUpperBound(j)) {
          particle.setVariableValue(j, problem.getUpperBound(j));
          speed[i][j] = speed[i][j] * changeVelocity2;
        }
      }
    }
  }

  protected void perturbation(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      if ((i % 6) == 0) {
        mutation.execute(swarm.get(i));
      }
    }
  }

  protected void updateParticlesMemory(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), localBestAttribute.getAttribute(swarm.get(i)));
      if (flag != 1) {
        DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
        localBestAttribute.setAttribute(swarm.get(i), particle);
      }
    }
  }

  public List<DoubleSolution> getResult() {
    return swarm;
  }

  private double velocityConstriction(double v, double[] deltaMax, double[] deltaMin,
                                      int variableIndex) {

    double result;

    double dmax = deltaMax[variableIndex];
    double dmin = deltaMin[variableIndex];

    result = v;

    if (v > dmax) {
      result = dmax;
    }

    if (v < dmin) {
      result = dmin;
    }

    return result;
  }

  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }

  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    return wma;
  }

  public String getName() {
    return "SMPSOD" ;
  }

  public String getDescription() {
    return "Speed contrained Multiobjective PSO. Aggregative variant" ;
  }

  /**
   * Initialize neighborhoods
   */
  protected void initializeNeighborhood() {
    double[] x = new double[swarmSize];
    int[] idx = new int[swarmSize];

    for (int i = 0; i < swarmSize; i++) {
      // calculate the distances based on weight vectors
      for (int j = 0; j < swarmSize; j++) {
        x[j] = MOEADUtils.distVector(lambda[i], lambda[j]);
        idx[j] = j;
      }

      // find 'niche' nearest neighboring subproblems
      MOEADUtils.minFastSort(x, idx, swarmSize, neighborSize);

      System.arraycopy(idx, 0, neighborhood[i], 0, neighborSize);
    }
  }

  protected void initializeIdealPoint() {
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      idealPoint[i] = 1.0e+30;
    }

    for (int i = 0; i < swarmSize; i++) {
      updateIdealPoint(swarm.get(i));
    }
  }

  protected void updateIdealPoint(DoubleSolution individual) {
    for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < idealPoint[n]) {
        idealPoint[n] = individual.getObjective(n);
      }
    }
  }

  /**
   * Update neighborhood method
   * @param individual
   * @param subProblemId
   * @param neighborType
   * @throws JMetalException
   */
  @SuppressWarnings("unchecked")
  protected  void updateLocalBest(DoubleSolution individual, int subProblemId, NeighborType neighborType) throws JMetalException {
    int size;
    int time;

    time = 0;

    if (neighborType == NeighborType.NEIGHBOR) {
      size = neighborhood[subProblemId].length;
    } else {
      size = swarm.size();
    }
    int[] perm = new int[size];

    MOEADUtils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (neighborType == NeighborType.NEIGHBOR) {
        k = neighborhood[subProblemId][perm[i]];
      } else {
        k = perm[i];
      }
      double f1, f2;

      f1 = fitnessFunction(localBest.get(k), lambda[k]);
      f2 = fitnessFunction(individual, lambda[k]);

      if (f2 < f1) {
        localBest.set(k, (DoubleSolution)individual.copy());
        time++;
      }

      if (time >= maximumNumberOfReplacedSolutions) {
        return;
      }
    }
  }

  double fitnessFunction(DoubleSolution individual, double[] lambda) throws JMetalException {
    double fitness;

    if (FunctionType.TCHE.equals(functionType)) {
      double maxFun = -1.0e+30;

      for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
        double diff = Math.abs(individual.getObjective(n) - idealPoint[n]);

        double feval;
        if (lambda[n] == 0) {
          feval = 0.0001 * diff;
        } else {
          feval = diff * lambda[n];
        }
        if (feval > maxFun) {
          maxFun = feval;
        }
      }

      fitness = maxFun;
    } else if (FunctionType.AGG.equals(functionType)) {
      double sum = 0.0;
      for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
        sum += (lambda[n]) * individual.getObjective(n);
      }

      fitness = sum;

    } else if (FunctionType.PBI.equals(functionType)) {
      double d1, d2, nl;
      double theta = 5.0;

      d1 = d2 = nl = 0.0;

      for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
        d1 += (individual.getObjective(i) - idealPoint[i]) * lambda[i];
        nl += Math.pow(lambda[i], 2.0);
      }
      nl = Math.sqrt(nl);
      d1 = Math.abs(d1) / nl;

      for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
        d2 += Math.pow((individual.getObjective(i) - idealPoint[i]) - d1 * (lambda[i] / nl), 2.0);
      }
      d2 = Math.sqrt(d2);

      fitness = (d1 + theta * d2);
    } else {
      throw new JMetalException(" SMPSOD.fitnessFunction: unknown type " + functionType);
    }
    return fitness;
  }

  /**
   * Initialize weight vectors
   */
  protected void initializeUniformWeight() {
    if ((problem.getNumberOfObjectives() == 2) && (swarmSize <= 300)) {
      for (int n = 0; n < swarmSize; n++) {
        double a = 1.0 * n / (swarmSize - 1);
        lambda[n][0] = a;
        lambda[n][1] = 1 - a;
      }
    } else {
      String dataFileName;
      dataFileName = "W" + problem.getNumberOfObjectives() + "D_" +
          swarmSize + ".dat";

      try {
        InputStream in = getClass().getResourceAsStream("/" + dataDirectory + "/" + dataFileName);
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);

        int i = 0;
        int j = 0;
        String aux = br.readLine();
        while (aux != null) {
          StringTokenizer st = new StringTokenizer(aux);
          j = 0;
          while (st.hasMoreTokens()) {
            double value = new Double(st.nextToken());
            lambda[i][j] = value;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        throw new JMetalException("initializeUniformWeight: failed when reading for file: "
            + dataDirectory + "/" + dataFileName, e) ;
      }
    }
  }

  void updateGlobalBest(int particleIndex, NeighborType type) {
    double gBestFitness ;
    gBestFitness = fitnessFunction(globalBest.get(particleIndex), lambda[particleIndex]) ;
    if (type == NeighborType.NEIGHBOR) {
      for (int i = 0 ; i < neighborhood[i].length; i++) {
        double v1 = fitnessFunction(swarm.get(neighborhood[particleIndex][i]), lambda[particleIndex]) ;
        double v2 = gBestFitness ;
        if (v1 < v2) {
          globalBest.set(particleIndex, (DoubleSolution) swarm.get(i).copy()) ;
          gBestFitness = fitnessFunction(globalBest.get(particleIndex), lambda[particleIndex]) ;
        }
      }
    }
    else {
      for (int i = 0 ; i < swarm.size(); i++) {
        double v1 = fitnessFunction(swarm.get(i), lambda[particleIndex]) ;
        double v2 = gBestFitness ;
        if (v1 < v2) {
          globalBest.set(particleIndex, (DoubleSolution) swarm.get(i).copy()) ;
          gBestFitness = fitnessFunction(globalBest.get(particleIndex), lambda[particleIndex]) ;
        }
      }
    }
  }

}
