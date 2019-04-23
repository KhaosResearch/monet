package org.uma.jmetal.algorithm.multiobjective.ais;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MOAISHV implements Algorithm<List<DoubleSolution>> {

    private final Problem<DoubleSolution> problem;
    private final int populationSize;
    private final int maxNumberOfEvaluations;
    private final MutationOperator<DoubleSolution> mutationOperator;
    private final SolutionListEvaluator<DoubleSolution> evaluator;
    private final AffinityAttribute affinityAttribute;

    private List<DoubleSolution> population;
    private List<DoubleSolution> antibodies;
    private List<DoubleSolution> antigen;
    private int generations;
    private double nb_cl_parameter = 0.5;
    private RankingAndCrowdingSelection<DoubleSolution> populationSelector;
    private Comparator<DoubleSolution> affinityComparator;

    private int maxGenerations;

    public MOAISHV(Problem<DoubleSolution> problem, int populationSize, int maxEvaluations) {
        this.problem        = problem;
        this.populationSize = populationSize;
        this.population = new ArrayList<>(this.populationSize);
        this.antibodies     = new ArrayList<>(this.populationSize);
        this.antigen = new ArrayList<>(this.populationSize);

        this.generations = 0;
        this.maxNumberOfEvaluations = maxEvaluations;
        this.maxGenerations = (int)Math.ceil(this.maxNumberOfEvaluations/this.populationSize);
        this.mutationOperator = new GaussianMutation(1.0 / problem.getNumberOfVariables(),maxGenerations);
        //this.mutationOperator = new PolynomialMutation(1.0 / (double) problem.getNumberOfVariables(),20.0);
        this.evaluator = new SequentialSolutionListEvaluator<>();
        this.affinityAttribute = new AffinityAttribute();
        this.populationSelector = new RankingAndCrowdingSelection<>(this.populationSize);

        affinityComparator = Comparator.comparingDouble(this.affinityAttribute::getAttribute).reversed();
    }


    /*
     * Determines the number of clones for regular elements within the population
     */

    private int computeNC() {
        int NC = (int) (populationSize * 0.1 + nb_cl_parameter * populationSize * 0.3);
        return NC;
    }

    @Override
    public void run() {

        int NC                          = computeNC();
        population                      = evaluateSolutionList(createInitialPopulation());
        Ranking<DoubleSolution> ranking = new DominanceRanking<>();


        while (!this.terminationCriteriaMet()) {

            antibodies.clear();
            antigen.clear();


            ranking.computeRanking(this.population);

            // Split the population population into two sets: antibodies, antigenes
            antigen.addAll(ranking.getSubfront(0));

            for (int i = 1; i < ranking.getNumberOfSubfronts(); i++)
                antibodies.addAll(ranking.getSubfront(i));


            //affinity computations
            computeAffinity();

            // Determining the number of clones per regular and extreme solutions
            int numberOfClonesForRegular    = determineClonesNoExtremes();
            int numberOfClonesForExtreme    = determineClonesExtremes(numberOfClonesForRegular);

            List<DoubleSolution> candidate = new ArrayList<>();
            antigen.sort(this.affinityComparator);
            antibodies.sort(this.affinityComparator);
            candidate.addAll(antigen);
            candidate.addAll(antibodies);


            List<DoubleSolution> pool = new ArrayList<>();





            /* This part is not described in the paper. I copy it from the code */
            // For the first nobjth individuals, clone them such that
            // the total number of clones is "nb_ext_clone"
            int toBeCloned = (int) Math.ceil(numberOfClonesForExtreme / problem.getNumberOfObjectives());
            for (int i=0;i<problem.getNumberOfObjectives();i++) {
                for (int j=0;j<toBeCloned;j++) {
                    pool.add((DoubleSolution) candidate.get(i).copy());
                }
            }


            /* This is how is told in the paper
            double sumAffinityExtreme = 0; // this does not seem to be used in the code
            double sumAffinityNoExtreme = 0; // this does not seem to be used in the

            for (DoubleSolution s : this.population) {
                double tmp = this.affinityAttribute.getAttribute(s);
                if (extremeAttribute.getAttribute(s))
                    sumAffinityExtreme += tmp;
                else
                    sumAffinityNoExtreme += tmp;
            }

            for (int i = this.problem.getNumberOfObjectives(); i <NC; i++) {
                DoubleSolution s = this.population.get(i);
                int clones = determineCloneCandidates(s, (extremeAttribute.getAttribute(s) ? p1 : p2), (extremeAttribute.getAttribute(s) ? sumAffinityExtreme : sumAffinityNoExtreme));
                pool.add(((DoubleSolution) s.copy()));
                for (int j = 0; j < clones; j++)
                    pool.add((DoubleSolution) s.copy());

            } */

            /* This is how it is done in the code */
            double affinity = 0.0;
            for (int i = problem.getNumberOfObjectives(); i < NC; i++)
                affinity += affinityAttribute.getAttribute(candidate.get(i));

            for (int i = problem.getNumberOfObjectives(); i < NC && pool.size() < populationSize; i++) {
                toBeCloned = (int) Math.ceil(numberOfClonesForRegular * (affinityAttribute.getAttribute(candidate.get(i)))/affinity);
                for (int j = 0; j < toBeCloned && pool.size()<populationSize; j++) {
                    pool.add((DoubleSolution) candidate.get(i).copy());
                }

                // this guard ensures the pool has as many solutions as in the poulation (this is not in the paper)
                if (i==NC-1) {
                    while (pool.size() < populationSize) {
                        pool.add((DoubleSolution) candidate.get(i).copy());
                    }
                }
            }


            // Steps 6 and 7. Mutation. Step 6 is setting the mutation probability. Not necessary here. Step 7 is to perform the mutation




            ((GaussianMutation)this.mutationOperator).setCurrentGeneration(generations);
            for (DoubleSolution s : pool) {
                mutationOperator.execute(s);
            }

            // Step 8. Evaluate the pool
            for (DoubleSolution s : pool) {
                problem.evaluate(s);
            }

            // Step 9. Add antigenes also to the pool
            pool.addAll(this.antigen);

            // Step 10. Update the population
            population = this.populationSelector.execute(pool);
            /*
            if (antigen.size() == populationSize && (generations > ((maxGenerations*2) / 3)))
                population = selectWithFirstMethod(pool);
            else
                population = selectWithSecondMethod(pool);
            //*/
            generations++;


        }
    }



    private List<DoubleSolution> selectWithFirstMethod(List<DoubleSolution> list) {
        // so far I call the other method
        return this.selectWithSecondMethod(list);
    }


    private List<DoubleSolution> selectWithSecondMethod(List<DoubleSolution> list) {
        // This method implements selection quasy based on crowding. It is not purely crowding+ranking since
        // the affinity of solutions is taken into account too

        Ranking<DoubleSolution> ranking = new DominanceRanking<>();

        ranking.computeRanking(list);

        List<DoubleSolution> result = new ArrayList<>(this.populationSize);
        int rank = 0;
        while (result.size() != this.populationSize) {
            List<DoubleSolution> nextFront = ranking.getSubfront(rank++);
            if (nextFront.size()+result.size() <= this.populationSize) {
                result.addAll(nextFront);
            } else {
                crowding(nextFront);
                nextFront.sort(affinityComparator);
                for (int i = 0; i < populationSize-result.size();i++) {
                    result.add(nextFront.get(i));
                }
            }
        }

        return result;
    }


    // Distance between objectives (replicated from SolutionUtil since it has only package visibility there)
    private double distanceBetweenObjectives(DoubleSolution firstSolution, DoubleSolution secondSolution) {

        double diff;
        double distance = 0.0;
        //euclidean distance
        for (int nObj = 0; nObj < firstSolution.getNumberOfObjectives();nObj++){
            double norm1 = firstSolution.getObjective(nObj) ;
            double norm2 = secondSolution.getObjective(nObj);

            diff = norm1 - norm2;
            distance += Math.pow(diff,2.0);
        } // for

        return Math.sqrt(distance);
    }

    // Determines the probability of cloning for extreme solutions
    private int determineClonesExtremes(int noExtremeClones) {
        /* This is how is told in the paper
        if (this.generations == 0)
            return (int) (this.populationSize * 0.5);
        return (int) (this.populationSize * 0.1);
        */

        /* This is how is done in the code */
        return populationSize - noExtremeClones;
    }

    // Determines probability of cloning for non extreme solutions
    private int determineClonesNoExtremes() {
        /* This is how is told in the paper
        if (this.generations == 0)
            return (int) (this.populationSize * 0.5);
        return (int) (this.populationSize * 0.9);
        */

        /* This is how is done in the code */
        return (int) ((((double) generations / (double) maxGenerations) * 0.4 * populationSize) + (0.5 * populationSize));
    }

    private List<DoubleSolution> createInitialPopulation() {
        List<DoubleSolution> population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newIndividual = problem.createSolution();
            population.add(newIndividual);
        }
        return population;
    }

    private List<DoubleSolution> evaluateSolutionList(List<DoubleSolution> population) {
        List<DoubleSolution> list = evaluator.evaluate(population, problem);
        return list;
    }



    @Override
    public List<DoubleSolution> getResult() {
        Ranking<DoubleSolution> ranking = new DominanceRanking<>();
        ranking.computeRanking(this.population);
        System.out.println("Ranking "+ this.population.size());
        return ranking.getSubfront(0);

    }

    @Override
    public String getName() {
        return "MOAISHV";
    }

    @Override
    public String getDescription() {
        return "Multi-objective Artificial Intelligent System based algorithm";
    }


    /**
     * Re-computes the affinity of solutions based on crowding distance. I duplicate the code here since there are
     * quite a few differences with the crowding distance method we have already
     * @param list
     */
    private void crowding(List<DoubleSolution> list) {

        double maxAffinity = Double.NEGATIVE_INFINITY;

        for (int obj = 0; obj < this.problem.getNumberOfObjectives(); obj++) {
            double min = Double.POSITIVE_INFINITY, max=Double.NEGATIVE_INFINITY;
            for (DoubleSolution s : list) {
                min = (s.getObjective(obj)<min) ? s.getObjective(obj) : min;
                max = (s.getObjective(obj)>max) ? s.getObjective(obj) : max;
            }

            list.sort(new ObjectiveComparator<>(obj));

            for (int index = 1; index < list.size()-1; index++) {
                DoubleSolution s = list.get(index);

                double left = (list.get(index-1).getObjective(obj) - min) / (max - min);
                double right = (list.get(index+1).getObjective(obj) - min) / (max - min);
                this.affinityAttribute.setAttribute(s,this.affinityAttribute.getAttribute(s)+
                        Math.pow(right-left,2.0));
                if (this.affinityAttribute.getAttribute(s) > maxAffinity)
                    maxAffinity = this.affinityAttribute.getAttribute(s);
            }

            this.affinityAttribute.setAttribute(list.get(0),this.affinityAttribute.getAttribute(list.get(0))+
                        maxAffinity);
            this.affinityAttribute.setAttribute(list.get(list.size()-1),this.affinityAttribute.getAttribute(list.get(list.size()-1))+
            maxAffinity);
        }

    }



    private double computeAffinityAntiBodies() {
        double maxAffAntiBodies = 0.0;
        if (antibodies.size() > 0 ) {
            DoubleSolution gene = antigen.get(JMetalRandom.getInstance().nextInt(0, antigen.size() - 1));
            for (DoubleSolution s : antibodies) {
                affinityAttribute.setAttribute(s, 1.0 / (distanceBetweenObjectives(s, gene) +1.0));//the +1 is from the code (never described)
            }


            maxAffAntiBodies = affinityAttribute.getAttribute(antibodies.stream().min(affinityComparator).get());//min since the affinity comparator is reverted


            for (int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {
                antibodies.sort(new ObjectiveComparator<>(obj));
                affinityAttribute.setAttribute(antibodies.get(0), maxAffAntiBodies);
            }
        }
        return maxAffAntiBodies;
    }

    private void computeAffinityAntiGenes(double minimumAffinity) {
        if (this.antigen.size() < 3) {
            for (DoubleSolution s : this.antigen)
                affinityAttribute.setAttribute(s,1.5 * minimumAffinity);
        } else {

            // Initialization to 1 (this is from the code, it is not in the paper)
            for (DoubleSolution s : antigen)
                affinityAttribute.setAttribute(s, 1.0);


            double maxAffinityAntigene = Double.NEGATIVE_INFINITY;

            for (int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {

                double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;

                for (DoubleSolution s : antigen) {
                    min = (s.getObjective(obj) < min) ? s.getObjective(obj) : min;
                    max = (s.getObjective(obj) > max) ? s.getObjective(obj) : max;
                }

                antigen.sort(new ObjectiveComparator<>(obj));

                for (int index = 1; index < antigen.size() - 1; index++) {
                    DoubleSolution s = antigen.get(index);

                    double left, right;
                    if ((max - min) == 0)
                        left = right = 0.0;
                    else {
                        left  = (antigen.get(index - 1).getObjective(obj) - min) / (max - min);
                        right = (antigen.get(index + 1).getObjective(obj) - min) / (max - min);
                    }

                    affinityAttribute.setAttribute(s, affinityAttribute.getAttribute(s) * Math.abs(left - right));
                    maxAffinityAntigene = (affinityAttribute.getAttribute(s) > maxAffinityAntigene) ? affinityAttribute.getAttribute(s) : maxAffinityAntigene;

                }
            }

            for (int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {
                antigen.sort(new ObjectiveComparator<>(obj));
                affinityAttribute.setAttribute(antigen.get(0),maxAffinityAntigene);
            }
        }
    }


    private void computeAffinityAntiGenesHV(double minimumAffinity) {
        if (this.antigen.size() < 2) {
            for (DoubleSolution s : this.antigen)
                affinityAttribute.setAttribute(s,1.5 * minimumAffinity);
        } else {
            // Initialization to 1 (this is from the code, it is not in the paper)
            for (DoubleSolution s : antigen)
                affinityAttribute.setAttribute(s, 1.0);


            HypervolumeContributionAttribute<DoubleSolution> hvContribution = new HypervolumeContributionAttribute<>() ;
            PISAHypervolume<DoubleSolution> hypervolume = new PISAHypervolume<>();
            hypervolume.computeHypervolumeContribution(antigen, antigen); // use antigen as reference front too

            double maxAffinityAntigene = Double.NEGATIVE_INFINITY;
            for (DoubleSolution s : antigen) {
                affinityAttribute.setAttribute(s,affinityAttribute.getAttribute(s) * hvContribution.getAttribute(s));
                maxAffinityAntigene = (affinityAttribute.getAttribute(s) > maxAffinityAntigene) ? affinityAttribute.getAttribute(s) : maxAffinityAntigene;
            }

            for (int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {
                antigen.sort(new ObjectiveComparator<>(obj));
                affinityAttribute.setAttribute(antigen.get(0),maxAffinityAntigene);
            }
        }
    }



    private void computeAffinity() {
        double maxAffAntiBodies = computeAffinityAntiBodies();
        computeAffinityAntiGenesHV(maxAffAntiBodies);
    }


    // Allows setting a different nb_cl_parameter
    public void setNb_cl_parameter(double nb_cl_parameter) {
        this.nb_cl_parameter = nb_cl_parameter;
    }


    /*
     * Termination criterion
     * Returns true when the termination criterion is met; false otherwise
     */
    private boolean terminationCriteriaMet() {
        return (this.generations > 200);
    }


    private class AffinityAttribute
            extends GenericSolutionAttribute<DoubleSolution, Double> {
    }



}
