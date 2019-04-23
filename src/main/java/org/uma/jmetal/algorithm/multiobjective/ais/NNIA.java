package org.uma.jmetal.algorithm.multiobjective.ais;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Juanjo
 * Class implementing the NNIA algorithm.
 * "Maoguo Gong, Licheng Jiao, Haifeng Du and Liefeng Bo. Multiobjective Immune Algorithm with Nondominated
 * Neighbor-Based Selection"
 *
 */


public class NNIA implements Algorithm<List<DoubleSolution>> {


    private int generations;
    private int evaluations;
    private int maxGenerations;
    private int populationSize;
    private int activePopulationSize;
    private int numberOfClones;

    private Problem<DoubleSolution> problem;

    private final MutationOperator<DoubleSolution> mutationOperator;
    private final CrossoverOperator<DoubleSolution> crossoverOperator;

    private final CrowdingDistance<DoubleSolution> crowdingEstimator;
    private final NumberOfClonesAttribute clonesAttribute = new NumberOfClonesAttribute();

    private List<DoubleSolution> dominating_antibodies;

    private final SolutionListEvaluator<DoubleSolution> evaluator;


    public NNIA(Problem<DoubleSolution> problem, int populationSize, int activePopulationSize, int numberOfClones, int maxGenerations,
                MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover) {

        this.problem = problem;

        this.populationSize         = populationSize;
        this.activePopulationSize   = activePopulationSize;
        this.numberOfClones         = numberOfClones;

        this.maxGenerations = maxGenerations;
        this.evaluations    = 0;
        this.generations    = 0;

        this.mutationOperator   = mutation;
        this.crossoverOperator  = crossover;

        this.evaluator = new SequentialSolutionListEvaluator<>();

        this.crowdingEstimator = new CrowdingDistance<>();

    } // constructor

    @Override
    public void run() {
        // for references with the paper
        // antibodies_population is the population B in the paper
        // dominating_antibodies is the population D in the paper
        // active_antibodies is the population A in the paper
        // clone_population is the population C in the paper


        List<DoubleSolution> antibodies_population  = evaluateSolutionList(Utils.createInitialPopulation(this.problem,this.populationSize));
        dominating_antibodies                       = new ArrayList<>(this.populationSize);
        List<DoubleSolution> clone_population       ;
        List<DoubleSolution> active_antibodies      ;


        final Ranking<DoubleSolution> ranking = new DominanceRanking<>();
        while (generations < maxGenerations) {

            List<DoubleSolution> temporal_dominating = ranking.computeRanking(antibodies_population).getSubfront(0);

            if (temporal_dominating.size() > populationSize) {
                crowdingEstimator.computeDensityEstimator(temporal_dominating);
                temporal_dominating.sort(new CrowdingDistanceComparator<DoubleSolution>());
                temporal_dominating = temporal_dominating.subList(0,populationSize);
            }

            dominating_antibodies = temporal_dominating;

            crowdingEstimator.computeDensityEstimator(dominating_antibodies);

            if (dominating_antibodies.size() > activePopulationSize) {
                dominating_antibodies.sort(new CrowdingDistanceComparator<DoubleSolution>());
            }

            active_antibodies = dominating_antibodies.subList(0,Math.min(temporal_dominating.size(),activePopulationSize));

            clone_population = proportional_cloning(active_antibodies);
            clone_population = crossover(clone_population,active_antibodies);
            clone_population = mutation(clone_population);
            clone_population = evaluator.evaluate(clone_population,problem);
            evaluations += clone_population.size();

            antibodies_population.clear();
            antibodies_population.addAll(dominating_antibodies);
            antibodies_population.addAll(clone_population);
            generations++;

        }

    }

    private List<DoubleSolution> mutation(List<DoubleSolution> solutions) {
        for (DoubleSolution s: solutions)
            mutationOperator.execute(s);
        return solutions;
    }


    private List<DoubleSolution> crossover(List<DoubleSolution> clones, List<DoubleSolution>  active_antibodies) {
        List<DoubleSolution> offsprings = new ArrayList<>(populationSize);
        for (DoubleSolution parentOne : clones) {
            DoubleSolution parentTwo = active_antibodies.get(JMetalRandom.getInstance().nextInt(0,active_antibodies.size()-1));
            List<DoubleSolution> parents = new ArrayList<>(2);
            parents.add(parentOne);
            parents.add(parentTwo);
            int selected = JMetalRandom.getInstance().nextDouble() < 0.5 ? 0:1;
            offsprings.add(crossoverOperator.execute(parents).get(selected));
        }
        return offsprings;
    }

    private double getMaxFiniteCrowdingDistance(List<DoubleSolution> solutions) {
        double result = Double.NEGATIVE_INFINITY;
        for (DoubleSolution s : solutions) {
            Double crowdingDistanceValue = crowdingEstimator.getAttribute(s);
            if (!crowdingDistanceValue.isInfinite() && crowdingDistanceValue.doubleValue() > result)
                result = crowdingDistanceValue;
        }
        return result;
    }


    /**
     * Computes a population of clones based on the information stored in the attribute NumberOfClonesAttributes
     * @param antibodies
     * @return A population of clones
     */
    private List<DoubleSolution> performCrowding(List<DoubleSolution> antibodies) {
        List<DoubleSolution> clones_population = new ArrayList<>(populationSize);
        for (DoubleSolution s : antibodies) {
            int clones = clonesAttribute.getAttribute(s);
            for (int i = 0; i < clones; i++) {
                clones_population.add((DoubleSolution)s.copy());
            }
        }
        return clones_population;
    }


    /**
     * Estimates the number of clones per each solution in antibodies. Each elemen of antibodies should have the
     * attributed crowdingDistance computed.
     * The estimated number of clones per solution is stored as an attribute of the tipe NumberOfClonesAttributes
     * @param antibodies
     */
    private void estimateClonesPerSolution(List<DoubleSolution> antibodies) {
        Double maxFiniteCrowdingDistance = getMaxFiniteCrowdingDistance(antibodies);
        if (maxFiniteCrowdingDistance.isInfinite()) {
            // there are only extreme solutions, each of them will be clone the same amount of time. Not described
            // in the paper
            for (DoubleSolution s : antibodies)
                clonesAttribute.setAttribute(s,(int) Math.ceil(numberOfClones / antibodies.size()));

        } else {
            double totalCrowding = 0.0;
            for (DoubleSolution s : antibodies) {
                Double crowdingValue = crowdingEstimator.getAttribute(s);
                double effectiveCrowdingValue = crowdingValue.isInfinite() ? maxFiniteCrowdingDistance : crowdingValue;
                totalCrowding += effectiveCrowdingValue;

                //update crowding value as well
                crowdingEstimator.setAttribute(s, effectiveCrowdingValue);
            }

            for (DoubleSolution s : antibodies) {
                Double crowdingValue = crowdingEstimator.getAttribute(s);
                clonesAttribute.setAttribute(s,(int)(Math.ceil(crowdingValue/totalCrowding)*numberOfClones));
            }


        }
    }

    /**
     * Performs the proportional_cloning step of the algorithm
     * @return a population of clones
     */
    private List<DoubleSolution> proportional_cloning(List<DoubleSolution> antibodies) {
        estimateClonesPerSolution(antibodies);
        return performCrowding(antibodies);
    }




    private List<DoubleSolution> evaluateSolutionList(List<DoubleSolution> population) {
        List<DoubleSolution> list = evaluator.evaluate(population, problem);
        return list;
    }


    @Override
    public List<DoubleSolution> getResult() {
        return dominating_antibodies;
    }

    @Override
    public String getName() {
        return "NNIA";
    }

    @Override
    public String getDescription() {
        return "Multi-objective Artificial Inmune System";
    }

    private class NumberOfClonesAttribute extends GenericSolutionAttribute<DoubleSolution,Integer> {}
} // NNIA
