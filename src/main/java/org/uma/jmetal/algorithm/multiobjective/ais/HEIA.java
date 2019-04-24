package org.uma.jmetal.algorithm.multiobjective.ais;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.algorithm.multiobjective.ais.clone.Clone;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of HEIA - Hybrid Evolutionary Immune Algorithm
 * This implementation is an adaptation of the original source code provided by the authors in their site
 * to this version of jMetal. This version of jMetal is specially tailored to the metaheuristic book and
 * extends jMetal 5.0
 **/

public class HEIA implements Algorithm<List<DoubleSolution>> {


    private Problem<DoubleSolution> problem;
    int populationSize;
    int maxEvaluations;
    int evaluations;
    int clonesize =  0;
    int num_obj;



    QualityIndicator indicators; // QualityIndicator object
    double Cro_pro=0.5;
    Ranking ranking;
    List<DoubleSolution> population;
    List<DoubleSolution> offspringPopulation;
    List<DoubleSolution> union;
    List<DoubleSolution> clonepopulation;
    List<DoubleSolution> Archive;
    List<DoubleSolution> front = null;
    List<DoubleSolution> SBXPop, DEPop;

    MutationOperator< DoubleSolution > mutationOperator;
    CrossoverOperator< DoubleSolution> crossoverOperator,crossoverOperator2;

    Clone< List<DoubleSolution>> cloneoperator;


    CrowdingDistance<DoubleSolution> crowdingDistance = new CrowdingDistance<>();
    private CloneNumber clone_num = new CloneNumber();


    //extends Algorithm {
    /**
     * Constructor
     *
     * @param problem
     *            Problem to solve
     */
    public HEIA (Problem<DoubleSolution> problem, int populationSize, int maxEvaluations,
                 Clone<List<DoubleSolution>> cloneoperator,
                 MutationOperator<DoubleSolution> mutationOperator,
                 CrossoverOperator<DoubleSolution> crossoverOperator1,
                 CrossoverOperator<DoubleSolution> crossoverOperator2){

        this.problem 			= problem;
        this.num_obj 			= this.problem.getNumberOfObjectives();
        this.populationSize 	= populationSize;
        this.maxEvaluations 	= maxEvaluations;
        clonesize				= populationSize/5;
        clonepopulation 		= new ArrayList<>(clonesize);
        population 				= new ArrayList<>(populationSize);
        Archive 				= new ArrayList<>(populationSize);
        offspringPopulation		= new ArrayList<>(populationSize);
        SBXPop					= new ArrayList<>(populationSize);
        DEPop					= new ArrayList<>(populationSize);

        // read the operators
        this.cloneoperator 		= cloneoperator;
        this.mutationOperator 	= mutationOperator;
        this.crossoverOperator 	= crossoverOperator1;
        this.crossoverOperator2 = crossoverOperator2;

    }





    // Archive
    public void ArchiveUpdate()
    {
        union = new ArrayList<>(Archive.size()+DEPop.size()+SBXPop.size());

        union.addAll(Archive);
        union.addAll(DEPop);
        union.addAll(SBXPop);



        Suppress(union);


        ranking = new DominanceRanking();
        ranking.computeRanking(union);
        Archive.clear();
        clonepopulation.clear();

        front = ranking.getSubfront(0);
        crowdingDistance.computeDensityEstimator(front);


        Suppress(front);

        // Remain is less than front(index).size, insert only the best one
        front.sort(new CrowdingDistanceComparator<DoubleSolution>());

        while (front.size() > populationSize){
            front.remove(front.size() - 1);
            crowdingDistance.computeDensityEstimator(front);
            front.sort(new CrowdingDistanceComparator<DoubleSolution>());
        }
        front.sort(new ObjectiveComparator((int)(JMetalRandom.getInstance().nextDouble()*num_obj)));
        for (int k = 0; k < front.size(); k++) {
            this.clone_num.setAttribute(front.get(k),k);
            Archive.add(front.get(k));
        } // for
        front.sort(new CrowdingDistanceComparator<DoubleSolution>());
        for (int k = 0; k < clonesize && k < front.size(); k++) {
            clonepopulation.add(front.get(k));


        } // for


    }


    // DEPop
    public void DEUpdate() {
        DoubleSolution[] offSpring = new DoubleSolution[2];
        for (int i = 0; i < DEPop.size(); i++) {
            if (evaluations < maxEvaluations) {
                DoubleSolution[] parents = new DoubleSolution[3];
                parents[2] = DEPop.get(i);
                if(clonepopulation.size()<20){
                    if(clonepopulation.size()>1){
                        int[] permutation = new int[clonepopulation.size()];
                        MOEADUtils.randomPermutation(permutation,clonepopulation.size());
                        int seleted=permutation[0];
                        int seleted2=permutation[1];

                        parents[0] = clonepopulation.get(seleted);
                        parents[1] = clonepopulation.get(seleted2);
                    }
                    else{
                        parents[0] = clonepopulation.get(0);
                        parents[1] = clonepopulation.get(0);
                    }
                }
                else{
                    if(0.1< JMetalRandom.getInstance().nextDouble()){		// 0.9

                        int neighbors= this.clone_num.getAttribute(DEPop.get(i));
                        //System.out.println(neighbors);
                        int[] permutation = new int[20];
                        MOEADUtils.randomPermutation(permutation,20);
                        int seleted=permutation[0];
                        int seleted2=permutation[1];
                        if(neighbors<10){
                            parents[1] = Archive.get(seleted);
                            parents[0] = Archive.get(seleted2);
                        }
                        else if(neighbors>(Archive.size()-10)){
                            parents[1] = Archive.get(Archive.size()-20+seleted);
                            parents[0] = Archive.get(Archive.size()-20+seleted2);
                        }
                        else{
                            parents[1] = Archive.get(neighbors-10+seleted);
                            parents[0] = Archive.get(neighbors-10+seleted2);
                        }
                    }
                    else{
                        int[] permutation = new int[clonepopulation.size()];
                        MOEADUtils.randomPermutation(permutation,clonepopulation.size());
                        int seleted=permutation[0];
                        int seleted2=permutation[1];
                        parents[0] = clonepopulation.get(seleted);
                        parents[1] = clonepopulation.get(seleted2);
                    }
                }

                List<DoubleSolution> crossoverArguments;
                crossoverArguments = new ArrayList<>();
                ((DifferentialEvolutionCrossover) crossoverOperator).setCurrentSolution(parents[2]);
                for (DoubleSolution s : parents)
                    crossoverArguments.add(s);


                offSpring[0] = crossoverOperator.execute(crossoverArguments).get(0);



                mutationOperator.execute(offSpring[0]);
                problem.evaluate(offSpring[0]);

                DEPop.set(i, offSpring[0]);
                evaluations += 1;
            }
        }
    }

    //SBXPop
    public void SBXUpdate()  {
        List<DoubleSolution> offSpring;
        for (int i = 0; i < SBXPop.size(); i++) {
            if (evaluations < maxEvaluations) {
                DoubleSolution[] parents = new DoubleSolution[2];
                parents[0] = SBXPop.get(i);
                parents[1] = clonepopulation.get((int) Math
                        .floor(JMetalRandom.getInstance().nextDouble()* clonepopulation.size()));

                List<DoubleSolution> crossoverArguments = new ArrayList<>(parents.length);
                for (DoubleSolution s : parents)
                    crossoverArguments.add(s);

                offSpring = crossoverOperator2.execute(crossoverArguments);

                mutationOperator.execute(offSpring.get(0));
                problem.evaluate(offSpring.get(0));

                SBXPop.set(i, offSpring.get(0));
                evaluations += 1;
            }
        }
    }


    @Override
    public void run() {

        //int[][] neighborhood_= new int[populationSize][20];
        evaluations = 0;
        //requiredEvaluations = 0;

        //selectionOperator = operators_.get("selection");

        // Create the initial solutionSet
        DoubleSolution newSolution;
        for (int i = 0; i < populationSize; i++) {
            newSolution = problem.createSolution();
            problem.evaluate(newSolution);
            evaluations++;
            population.add(newSolution);
        } // for

        ranking = new DominanceRanking();
        ranking.computeRanking(population);
        front 				 = ranking.getSubfront(0);
        front.sort(new ObjectiveComparator(0));//

        for (int k = 0; k < front.size(); k++) {		// archive
            this.clone_num.setAttribute(front.get(k),k);
            Archive.add(front.get(k));
        } // for

        crowdingDistance.computeDensityEstimator(front);
        front.sort(new CrowdingDistanceComparator<>());
        for (int k = 0; k < clonesize && k < front.size(); k++) {
            clonepopulation.add(front.get(k));
        } // for


        // Generations
        while (evaluations < maxEvaluations) {
            offspringPopulation = cloneoperator.execute(clonepopulation);
            SBXPop.clear();
            DEPop.clear();
            for (int i = 0; i < offspringPopulation.size(); i++) {
                if(Cro_pro< JMetalRandom.getInstance().nextDouble()) {
                    SBXPop.add(offspringPopulation.get(i));
                }
                else{
                    DEPop.add(offspringPopulation.get(i));
                }
            }
            DEUpdate();
            SBXUpdate();
            ArchiveUpdate();

        } // while

    }

    @Override
    public List<DoubleSolution> getResult() {
        return Archive;
    }

    @Override
    public String getName() {
        return "HEIA";
    }

    @Override
    public String getDescription() {
        return "HEIA Algorithm";
    }


    public void Suppress(List<DoubleSolution> solutionSet) {
        int decisionnum = problem.getNumberOfObjectives();
        double diff;
        for (int k = 0; k < solutionSet.size(); k++) {
            for (int l = k + 1; l < solutionSet.size(); l++) {
                int m = 0;
                for (m = 0; m < decisionnum; m++) {
                    diff=solutionSet.get(k).getObjective(m)-solutionSet.get(l).getObjective(m);
                    if(diff<0)
                        diff=-diff;
                    if(diff>0.000001){
                        break;
                    }
                }
                if (m == decisionnum) {
                    solutionSet.remove(l);
                    l--;
                }
            }
        }

    }


    private class CloneNumber extends GenericSolutionAttribute<DoubleSolution,Integer> {}

}

