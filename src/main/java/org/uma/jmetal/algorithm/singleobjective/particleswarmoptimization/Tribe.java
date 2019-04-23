//  Tribes.java
//
//  Author:
//       José M. García-Nieto <jnieto@lcc.uma.es>
//
//  Copyright (c) 2016 José M. García-Nieto
//
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

package org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization;

import org.uma.jmetal.algorithm.AbstractParticleSwarmOptimization;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.ExtendedPseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class implementing a Tribe specific for supporting multi-objective MOTRIBES
 *
 * @author José M. García-Nieto <jnieto@lcc.uma.es>
 */
public class Tribe extends AbstractParticleSwarmOptimization<DoubleSolution, DoubleSolution> {
  private DoubleProblem problem;
  private SolutionListEvaluator<DoubleSolution> evaluator;
  private Comparator<DoubleSolution> dominanceComparator ;

  private int swarmSize;
  public enum TribeBehaviour {GOOD, NEUTRAL, BAD};
  private TribeBehaviour tribeBehaviour;
  private int maxIterations;
  private int iterations;
  private List<DoubleSolution> localBest;
  private List<double[]> speed;
  private GenericSolutionAttribute<DoubleSolution, Integer> positionInSwarm;
  private double weight;
  private JMetalRandom randomGenerator;
  private DoubleSolution globalBestParticle;
  private DoubleSolution globalWorstParticle;
  private List<int[]> particlesHistory; /* to store past and current status of particles. Dimension  2 (-,+)*/
                                                        /* status: -1 is - (Deterioration); 0 is = (Neutral); 1 is + (Improvement)*/

  private int objectiveId;

  /**
   * Constructor
   *
   * @param problem
   * @param swarmSize
   * @param maxIterations
   * @param evaluator
   */
  public Tribe(DoubleProblem problem, int swarmSize, int maxIterations, SolutionListEvaluator<DoubleSolution> evaluator) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.maxIterations = maxIterations;
    this.evaluator = evaluator;

    tribeBehaviour = TribeBehaviour.BAD;
    weight = 1.0 / (2.0 * Math.log(2));

    /* Posible error, comentar a Antonio y Juanjo */
    ///localBest = new DoubleSolution[problem.getNumberOfVariables()];
    localBest = new ArrayList<DoubleSolution>(swarmSize) ;
    speed = null;
    particlesHistory = null;
    dominanceComparator = new DominanceComparator<DoubleSolution>();

    randomGenerator = JMetalRandom.getInstance() ;
    randomGenerator.setRandomGenerator(new ExtendedPseudoRandomGenerator(new JavaRandomGenerator()));

    positionInSwarm = new GenericSolutionAttribute<DoubleSolution, Integer>();

    globalBestParticle = null;
    globalWorstParticle = null;
  }


  /*public Tribe(DoubleProblem problem, int swarmSize, int maxIterations,
               SolutionListEvaluator<DoubleSolution> evaluator) {
    this(problem, swarmSize, maxIterations, evaluator);
  }*/

  public TribeBehaviour getTribeBehaviour(){
    return tribeBehaviour;
  }

  public void setTribeBehaviour(TribeBehaviour t){
    tribeBehaviour=t;
  }

  @Override
  public void initProgress() {
    iterations = 1;
  }

  @Override
  public void updateProgress() {
    iterations += 1;
  }

  @Override
  public boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  public List<DoubleSolution> createInitialSwarm() {
    return null;
  }


  public List<DoubleSolution> createInitialSwarm(int numberOfParticles) {
    swarmSize = numberOfParticles;
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);
    particlesHistory = new ArrayList<>(swarmSize);
    speed = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    int[] newHistory;
    double[] newSpeed;
    for (int i = 0; i < swarmSize; i++) {
      newHistory = new int[]{0, 0};
      newSpeed = new double[problem.getNumberOfVariables()];
      newSolution = problem.createSolution();
      positionInSwarm.setAttribute(newSolution, i);
      swarm.add(newSolution);
      particlesHistory.add(i,newHistory);
      speed.add(i,newSpeed);
    }

    return swarm;
  }

  /* evaluate swarms and determine particle's status*/
  @Override
  public List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    List<DoubleSolution> swarmTemp = new ArrayList<>(swarm.size());
    for (int i=0;i<swarm.size();i++) {
      swarmTemp.add(i, (DoubleSolution) swarm.get(i).copy());
    }

    swarm = evaluator.evaluate(swarm, problem);

    /* update particles histories: past and current status */
    for (int i=0; i < swarm.size(); i++) {
      particlesHistory.get(i)[0] =  particlesHistory.get(i)[1];
      int flag = dominanceComparator.compare(swarm.get(i), swarmTemp.get(i));
        /* status == 0 neutral*/
      if (flag == 0)
        particlesHistory.get(i)[1]=0;
      else if(flag < 0) /*improvement*/
        particlesHistory.get(i)[1]=1;
      else /* degradation */
        particlesHistory.get(i)[1]=-1;
    }

    return swarm;
  }

  /* initialize SHAMAN OF TRIBE */
  @Override
  public void initializeGlobalBest(List<DoubleSolution> swarm) {
    globalBestParticle = (DoubleSolution) swarm.get(0).copy() ;
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), globalBestParticle);
      if (flag != 1) {
        globalBestParticle = (DoubleSolution)swarm.get(i).copy() ;
      }
    }
  }

  public void initializeGlobalWorst(List<DoubleSolution> swarm) {
    globalWorstParticle = swarm.get(0) ;
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), globalWorstParticle);
      if (flag == 1) {
        globalWorstParticle = (DoubleSolution)swarm.get(i).copy() ;
      }
    }
  }

  @Override
  public void initializeParticleBest(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      localBest.add(i,(DoubleSolution) swarm.get(i).copy());
    }
  }

  @Override
  public void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = swarm.get(i);
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed.get(i)[j] = 0.0;
      }
    }
  }

  @Override
  public void updateVelocity(List<DoubleSolution> swarm) {

  }

  /* function lambda of Equation (9) in paper of Cooren, Clerc and Siarry 2011*/
  private double lambda(DoubleSolution x, int numberOfObjectives){
    double result = 0.0;
    int maxObjectiveIndex = 0;

    for (int i=0;i<numberOfObjectives;i++){
      if (x.getObjective(maxObjectiveIndex)<=x.getObjective(i)){
        maxObjectiveIndex = i;
      }
    }

    for (int i=0;i<numberOfObjectives;i++){
      result = result +  x.getObjective(i)/x.getObjective(maxObjectiveIndex);
    }

    return result/2.0;
  }


  /* uses a possible leader particle coming from the archive */
  public void updateVelocity(List<DoubleSolution> swarm, DoubleSolution leader) {
    DoubleSolution global = leader;
    double c1, c2, aux, mean, std, std2, lambda_p, lambda_g;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = swarm.get(i);



      if (particle != globalBestParticle) // is not the SHAMAN
              global = globalBestParticle;

      lambda_p = lambda(particle,particle.getNumberOfObjectives());
      lambda_g = lambda(global,global.getNumberOfObjectives());

      c1 = lambda_p/(lambda_p+lambda_g);
      c2 = lambda_g/(lambda_p+lambda_g);
      std2 = (lambda_p-lambda_g)==0 ? c2 : (lambda_p-lambda_g)/(lambda_p+lambda_g);

      /* Strategy of displacement = local by independent */
      if ((particlesHistory.get(i)[0] != -1) && (particlesHistory.get(i)[1] == 1)){
        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
          mean = (global.getVariableValue(var)-particle.getVariableValue(var));
          std = Math.abs(global.getVariableValue(var)-particle.getVariableValue(var));
          aux = ((ExtendedPseudoRandomGenerator)randomGenerator.getRandomGenerator()).randNormal(mean,std);
          speed.get(i)[var] = global.getVariableValue(var) + aux;
        }

       /* Strategy of displacement = disturbed pivot */
      }else if (((particlesHistory.get(i)[0] == 1) && (particlesHistory.get(i)[1] == 0))
              || (particlesHistory.get(i)[0] == -1) && (particlesHistory.get(i)[1] == 1)) {
        double radius = SolutionUtils.distanceBetweenSolutionsInObjectiveSpace(particle,global);
        double[] sphere = ((ExtendedPseudoRandomGenerator)randomGenerator.getRandomGenerator()).randSphere(problem.getNumberOfVariables());

        for (int var = 0; var < particle.getNumberOfVariables(); var++) {
          speed.get(i)[var] = (c1*(particle.getVariableValue(var)+sphere[var]*radius) +c2*(global.getVariableValue(var)+sphere[var]*radius));
        }
       /* Strategy of displacement = pivot */
      }else{
        if (randomGenerator.nextDouble(0, 1)<0.5){
          double radius = SolutionUtils.distanceBetweenSolutionsInObjectiveSpace(particle,global);
          double[] sphere = ((ExtendedPseudoRandomGenerator)randomGenerator.getRandomGenerator()).randSphere(problem.getNumberOfVariables());

          for (int var = 0; var < particle.getNumberOfVariables(); var++) {
            speed.get(i)[var] = (c1*(particle.getVariableValue(var)+sphere[var]*radius) +c2*(global.getVariableValue(var)+sphere[var]*radius));
          }
        }else{
          for (int var = 0; var < particle.getNumberOfVariables(); var++) {
            aux = ((ExtendedPseudoRandomGenerator)randomGenerator.getRandomGenerator()).randNormal(0,std2);
            speed.get(i)[var] = (1+aux)*particle.getVariableValue(var);
          }
        }
      }
    }/* end for */
  }

  @Override
  public void updatePosition(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int var = 0; var < particle.getNumberOfVariables(); var++) {
        particle.setVariableValue(var, particle.getVariableValue(var) + speed.get(i)[var]);

        if (particle.getVariableValue(var) < problem.getLowerBound(var)) {
          particle.setVariableValue(var, problem.getLowerBound(var));
          speed.get(i)[var] = 0.0;
        }
        if (particle.getVariableValue(var) > problem.getUpperBound(var)) {
          particle.setVariableValue(var, problem.getUpperBound(var));
          speed.get(i)[var] = 0.0;
        }
      }
    }
  }


  @Override
  protected void perturbation(List<DoubleSolution> swarm) {

  }

  @Override
  public void updateGlobalBest(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), globalBestParticle);
      if (flag != 1) {
        globalBestParticle = (DoubleSolution)swarm.get(i).copy() ;
      }
    }
  }


  public void updateGlobalWorst(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), globalWorstParticle);
      if (flag == 1) {
        globalWorstParticle = (DoubleSolution)swarm.get(i).copy() ;
      }
    }
  }

  @Override
  public void updateParticleBest(List<DoubleSolution> swarm) {
    tribeBehaviour = TribeBehaviour.BAD;
    for (int j = 0; j < swarm.size(); j++) {
      int flag = dominanceComparator.compare(swarm.get(j), localBest.get(j));
      if (flag == -1) {
        localBest.add(j, (DoubleSolution) swarm.get(j).copy());
        tribeBehaviour = TribeBehaviour.GOOD;
      }
    }

  }

  public void removeWorstParticle(List<DoubleSolution> swarm){
    if ((tribeBehaviour== TribeBehaviour.BAD)){
      updateGlobalWorst(swarm);
      int pos = positionInSwarm.getAttribute(globalWorstParticle);
      swarm.remove(pos);
      particlesHistory.remove(pos);
      speed.remove(pos);
    }
  }

  @Override
  public DoubleSolution getResult() {
    return globalBestParticle;
  }

  /* Getters */
  /*public double[][]getSwarmSpeedMatrix() {
    return speed ;
  }*/

  public List<DoubleSolution> getLocalBest() {
    return localBest ;
  }

  @Override
  public String getName() {
    return "TRIBE";
  }

  @Override
  public String getDescription() {
    return "TRIBE based PSO";
  }
}