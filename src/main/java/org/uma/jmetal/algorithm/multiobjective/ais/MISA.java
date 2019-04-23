package org.uma.jmetal.algorithm.multiobjective.ais;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.NonUniformMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * MISA (Multi-objective Immune System algorithm)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MISA implements Algorithm<List<DoubleSolution>> {
  private int populationSize;
  private List<DoubleSolution> population;
  private AdaptiveGridArchive<DoubleSolution> archive;
  private Problem<DoubleSolution> problem;

  private int maxNumberOfEvaluations;
  private int evaluations;

  private int defaultNumberOfClones;

  private NumberOfClonesAttribute<DoubleSolution> numberOfClonesAttribute;

  private SolutionListEvaluator<DoubleSolution> evaluator;

  private MutationOperator<DoubleSolution> mutationForBestAntibodies;
  private MutationOperator<DoubleSolution> mutationForWorstAntibodies;
  private RankingAndCrowdingSelection<DoubleSolution> populationSelector;

  public MISA(
          Problem<DoubleSolution> problem,
          int populationSize,
          int maxEvaluations,
          AdaptiveGridArchive<DoubleSolution> archive) {
    this.problem = problem;
    this.populationSize = populationSize;
    this.archive = archive;

    this.evaluations = 0;
    this.maxNumberOfEvaluations = maxEvaluations;

    this.defaultNumberOfClones = 6;
    this.numberOfClonesAttribute = new NumberOfClonesAttribute<>();

    this.mutationForBestAntibodies = new PolynomialMutation(1.0, 20.0);
    this.mutationForWorstAntibodies =
            new NonUniformMutation(1.0, 20.0, maxEvaluations / populationSize);

    this.evaluator = new SequentialSolutionListEvaluator<>();
    this.populationSelector = new RankingAndCrowdingSelection<>(this.populationSize);
  }

  @Override
  public void run() {
    /* Step 1: initial population */
    population = createInitialPopulation();
    population = evaluateSolutionList(population);
    evaluations += populationSize;

    while (evaluations < maxNumberOfEvaluations) {
      System.out.println("Evaluations: " + evaluations) ;
      /* Step 3: get best and worst antibodies */
      Ranking<DoubleSolution> ranking = new DominanceRanking<>();
      ranking.computeRanking(population) ;
      List<DoubleSolution> bestAntibodies = ranking.getSubfront(0);
      List<DoubleSolution> worstAntibodies = new ArrayList<>();
      for (int i = 1; i < ranking.getNumberOfSubfronts(); i++) {
        for (DoubleSolution solution : ranking.getSubfront(i)) {
          worstAntibodies.add(solution);
        }
      }

      /* Step 5: copy best antibodies to archive */
      for (DoubleSolution solution : bestAntibodies) {
        archive.add(solution);
      }

      /* Step 6: estimate the number of clones per best antibody */
      if (archive.size() < archive.getMaxSize()) {
        for (DoubleSolution solution : bestAntibodies) {
          numberOfClonesAttribute.setAttribute(solution, defaultNumberOfClones);
        }
      } else {
        double averageHypercubeOccupation = archive.getGrid().getAverageOccupation();
        int mostOccupiedCell = archive.getGrid().getMostPopulatedHypercube();

        for (DoubleSolution solution : bestAntibodies) {
          if (archive.getSolutionList().contains(solution)) {
            numberOfClonesAttribute.setAttribute(solution, 0);
          } else {
            int solutionLocationInTheGrid = archive.getGrid().location(solution);
            if (solutionLocationInTheGrid == -1) {
              numberOfClonesAttribute.setAttribute(solution, 0);
            } else {
              int solutionsInTheSameCell = archive.getGrid().getLocationDensity(solutionLocationInTheGrid);
            if (solutionLocationInTheGrid == mostOccupiedCell) {
              numberOfClonesAttribute.setAttribute(solution, 0);
            } else if (solutionsInTheSameCell > averageHypercubeOccupation) {
              numberOfClonesAttribute.setAttribute(solution, defaultNumberOfClones / 2);
            } else {
              numberOfClonesAttribute.setAttribute(solution, defaultNumberOfClones * 2);
            }
          }
        }
        }
      }

      /* Step 7: creating the clones from best antibodies */
      List<DoubleSolution> clonePopulation = new ArrayList<>();
      for (DoubleSolution solution : bestAntibodies) {
        for (int i = 0; i < numberOfClonesAttribute.getAttribute(solution); i++) {
          clonePopulation.add((DoubleSolution) solution.copy());
        }
      }

      /*  Step 8: Mutate the cloned antibodies */
      for (DoubleSolution solution : clonePopulation) {
        mutationForBestAntibodies.execute(solution);
      }

      /* Step 9: Mutate the worst antibodies */
      int currentEstimatedIteration = evaluations / populationSize;
      NonUniformMutation nonUniformMutation = (NonUniformMutation) mutationForWorstAntibodies;
      nonUniformMutation.setCurrentIteration(currentEstimatedIteration);
      for (DoubleSolution solution : worstAntibodies) {
        clonePopulation.add(nonUniformMutation.execute((DoubleSolution) solution.copy()));
      }

      /* Step 10: solution evaluation and restoring population size*/
      for (DoubleSolution solution: clonePopulation) {
        problem.evaluate(solution);
        evaluations ++ ;
      }

      population.addAll(clonePopulation) ;



      population = populationSelector.execute(population);


      /*
      List<DoubleSolution> newPopulation = new ArrayList<>() ;
      ranking = new DominanceRanking<>();
      ranking.computeRanking(population) ;

      if (ranking.getSubfront(0).size() >= populationSize) {
        for (int i = 0 ; i < populationSize; i++) {
          newPopulation.add(ranking.getSubfront(0).get(i)) ;
        }
      } else {
        int currentRank = 1 ;
        boolean populationFilled = false ;
        int i = 0 ;
        while (!populationFilled) {
          System.out.println("i = " + i) ;
          if (i < ranking.getSubfront(currentRank).size()) {
            newPopulation.add(ranking.getSubfront(currentRank).get(i)) ;
            i++ ;
            if (newPopulation.size() == populationSize) {
              populationFilled = true;
            }
          } else {
            currentRank ++ ;
            i = 0 ;
            if (currentRank == ranking.getNumberOfSubfronts()) {
              throw new JMetalException("Subrank too high") ;
            }
          }
        }
     }

        population = newPopulation ; */
      if (population.size() != populationSize) {
        throw new JMetalException("The population size of the new population is not 100") ;
      }
    }
  }

  @Override
  public List<DoubleSolution> getResult() {
    return archive.getSolutionList();
  }

  @Override
  public String getName() {
    return "MISA";
  }

  @Override
  public String getDescription() {
    return "MISA";
  }

  protected List<DoubleSolution> createInitialPopulation() {
    List<DoubleSolution> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  protected List<DoubleSolution> evaluateSolutionList(List<DoubleSolution> population) {
    List<DoubleSolution> list = evaluator.evaluate(population, problem);

    return list;
  }

  private class NumberOfClonesAttribute<S extends Solution<?>>
          extends GenericSolutionAttribute<S, Integer> {

  }
}

