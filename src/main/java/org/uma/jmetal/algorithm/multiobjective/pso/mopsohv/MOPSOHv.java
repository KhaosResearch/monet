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

package org.uma.jmetal.algorithm.multiobjective.pso.mopsohv;

import org.uma.jmetal.algorithm.AbstractParticleSwarmOptimization;
import org.uma.jmetal.algorithm.multiobjective.pso.mopsohv.util.HypeUtil;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

/**
 * This class implements the SMPSO algorithm described in:
 * SMPSO: A new PSO-based metaheuristic for multi-objective optimization
 * MCDM 2009. DOI: http://dx.doi.org/10.1109/MCDM.2009.4938830
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class MOPSOHv extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>> {
  private DoubleProblem problem;

  private double c1;
  private double c2;
  private double weight;
  private double changeVelocity1;
  private double changeVelocity2;
  private double ratioOfArchiveSolutionsForGlobalLeaderSelection;
  private double ratioOfArchiveSolutionsForLocalLeaderSelection ;

  private int swarmSize;
  private int maxIterations;
  private int iterations;

  private GenericSolutionAttribute<DoubleSolution, DoubleSolution> localBest;
  private double[][] speed;

  private JMetalRandom randomGenerator;

  private NonDominatedSolutionListArchive<DoubleSolution> archive;
  private Comparator<DoubleSolution> dominanceComparator;

  private MutationOperator<DoubleSolution> mutation;

  private SolutionListEvaluator<DoubleSolution> evaluator;

  private int archiveSize ;

  private HypervolumeContributionAttribute<DoubleSolution> hypervolumeContributionAttribute ;

  /**
   * Constructor
   */
  public MOPSOHv(DoubleProblem problem, int swarmSize, int archiveSize,
                 MutationOperator<DoubleSolution> mutationOperator, int maxIterations,
                 double c1, double c2, double weight,
                 double changeVelocity1, double changeVelocity2,
                 double ratioOfArchiveSolutionsForGlobalLeaderSelection,
                 double ratioOfArchiveSolutionsForLocalLeaderSelection,
                 SolutionListEvaluator<DoubleSolution> evaluator) {
    this.problem = problem;
    this.swarmSize = swarmSize;
    this.archive = new NonDominatedSolutionListArchive<>();
    this.mutation = mutationOperator;
    this.maxIterations = maxIterations;

    this.c1 = c1;
    this.c2 = c2;
    this.weight = weight;
    this.changeVelocity1 = changeVelocity1;
    this.changeVelocity2 = changeVelocity2;
    this.ratioOfArchiveSolutionsForGlobalLeaderSelection =
            ratioOfArchiveSolutionsForGlobalLeaderSelection ;
    this.ratioOfArchiveSolutionsForLocalLeaderSelection =
            ratioOfArchiveSolutionsForLocalLeaderSelection ;

    randomGenerator = JMetalRandom.getInstance();
    this.evaluator = evaluator;
    this.archiveSize = archiveSize ;

    dominanceComparator = new DominanceComparator<DoubleSolution>();
    localBest = new GenericSolutionAttribute<DoubleSolution, DoubleSolution>() ;
    speed = new double[swarmSize][problem.getNumberOfVariables()];

    this.hypervolumeContributionAttribute = new HypervolumeContributionAttribute<>() ;
  }

  protected void updateLeadersDensityEstimator() {
    this.computeHVContribution(archive);
  }

  @Override protected void initProgress() {
    iterations = 1;
    updateLeadersDensityEstimator();
  }

  @Override protected void updateProgress() {
    iterations += 1;
    updateLeadersDensityEstimator();
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);

    return swarm;
  }

  @Override protected void initializeGlobalBest(List<DoubleSolution> swarm) {
    updateGlobalBest(swarm);
  }

  @Override protected void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  @Override protected void initializeParticleBest(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      localBest.setAttribute(particle, (DoubleSolution) particle.copy());
    }
  }

  @Override protected void updateVelocity(List<DoubleSolution> swarm) {
    //computeHVContribution(archive) ;
    //sort(archive.getSolutionList(), new HypervolumeContributionComparator<DoubleSolution>());

    for (int i = 0; i < swarm.size(); i++) {

      DoubleSolution particle = swarm.get(i);

      int archiveSize = archive.getSolutionList().size() ;
      int globalBestMaxIndex = (int)(archiveSize * ratioOfArchiveSolutionsForGlobalLeaderSelection/100.0) ;
      if (globalBestMaxIndex == 0) {
        globalBestMaxIndex = 1 ;
      }

      int bestGlobalUpperIndex = JMetalRandom.getInstance().nextInt(0, globalBestMaxIndex - 1) ;
      DoubleSolution bestGlobal = archive.get(bestGlobalUpperIndex) ;

      int localBestLowerIndex = (archiveSize - (int)(archiveSize*ratioOfArchiveSolutionsForLocalLeaderSelection/100.0)) ;
      int bestLocalIndex = JMetalRandom.getInstance().nextInt(localBestLowerIndex, archiveSize-1) ;
      DoubleSolution bestLocal = archive.get(bestLocalIndex) ;

      double r1, r2;
      r1 = randomGenerator.nextDouble();
      r2 = randomGenerator.nextDouble();

      for (int var = 0; var < particle.getNumberOfVariables(); var++) {
        speed[i][var] =  weight * speed[i][var] +
                    c1 * r1 * (bestLocal.getVariableValue(var) - particle.getVariableValue(var)) +
                    c2 * r2 * (bestGlobal.getVariableValue(var) - particle.getVariableValue(var));
      }
    }
  }

  @Override protected void updatePosition(List<DoubleSolution> swarm) {
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

  @Override protected void perturbation(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
        mutation.execute(swarm.get(i));
      }
  }

  @Override protected void updateGlobalBest(List<DoubleSolution> swarm) {
    for (DoubleSolution particle : swarm) {
      archive.add((DoubleSolution)particle.copy());
    }

    if (archive.size() > archiveSize) {
      computeHVContribution(archive);
      sort(archive.getSolutionList(), new HypervolumeContributionComparator<DoubleSolution>());
      while (archive.getSolutionList().size() > archiveSize) {
        archive.getSolutionList().remove(archive.getSolutionList().size() - 1) ;
      }
    }
  }

  @Override protected void updateParticleBest(List<DoubleSolution> swarm) {
    /*
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), localBest.getAttribute(swarm.get(i)));
      if (flag != 1) {
        DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
        localBest.setAttribute(swarm.get(i), particle);
      }
    }
    */
  }

  @Override public List<DoubleSolution> getResult() {
    return archive.getSolutionList();
  }


  @Override public String getName() {
    return "MOPSOHv" ;
  }

  @Override public String getDescription() {
    return "MOPSO with Hypervolume" ;
  }

  private void computeHVContribution(NonDominatedSolutionListArchive<DoubleSolution> archive) {
    double[] points = new double[problem.getNumberOfObjectives()];
    double[] contribution = HypeUtil.hypeIndicator(
            archive.size(), 0, 2000, -1, 1, points, problem.getNumberOfObjectives(), 2000) ;

    for (int i = 0; i < archive.size(); i++) {
      hypervolumeContributionAttribute.setAttribute(
              archive.getSolutionList().get(i), contribution[i]);
    }
  }

}
