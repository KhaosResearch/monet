package org.uma.jmetal.algorithm.multiobjective.ais;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 *  Some utilities for Artificial Immune Systems
 */
public class Utils {

    public static <S extends Solution> List<S> createInitialPopulation(Problem<S> problem, int populationSize) {
        List<S> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            S newIndividual = problem.createSolution();
            population.add(newIndividual);
        }
        return population;
    }

}
