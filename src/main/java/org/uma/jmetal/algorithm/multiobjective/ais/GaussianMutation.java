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

package org.uma.jmetal.algorithm.multiobjective.ais;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;


/**
 * This class translate into a java class the code of the gaussian mutation. This is not a production ready class.
 * This class should only be used for testing the MOAIS-HV
 *
 * @author Juan J. Durillo
 */
public class GaussianMutation implements MutationOperator<DoubleSolution> {
    private static final double DEFAULT_PROBABILITY = 0.01 ;
    private double mutationProbability ;
    private int maxGenerations;
    private int currentGeneration;
    private JMetalRandom random = JMetalRandom.getInstance();



    /* to do remove this variables from here. They are mentioned in the code from carlos but it is not clear from
     * where they come */
    final static double mut_stl = 0.42, mut_stg = 1.7, st_gs = 0.8;


    /** Constructor */
    public GaussianMutation(int maxGenerations) {
        this(DEFAULT_PROBABILITY,  maxGenerations) ;
    }

    /** Constructor */
    public GaussianMutation(DoubleProblem problem, int maxGenerations) {
        this(1.0/problem.getNumberOfVariables(), maxGenerations) ;
    }

    /** Constructor */
    public GaussianMutation(double mutationProbability, int maxGenerations) {
        this.mutationProbability        = mutationProbability;
        this.maxGenerations             = maxGenerations;
        this.currentGeneration          = 0;
    }

    /** Getters */
    public double getMutationProbability() {
        return mutationProbability;
    }

    /** Setters */
    public void setMutationProbability(double probability) {
        this.mutationProbability = probability ;
    }

    public void setCurrentGeneration(int currentGeneration) {
        this.currentGeneration = currentGeneration;
    }


    /** Execute() method */
    @Override
    public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
        if (null == solution) {
            throw new JMetalException("Null parameter") ;
        }

        doMutation(mutationProbability, solution);
        return solution;
    }

    /** Perform the mutation operation */
    private void doMutation(double probability, DoubleSolution solution) {
        double param, x, p_mut_type;

        x = -6.0 + ((double)currentGeneration/(double)maxGenerations) * 12.0;
        param = -4.0 + st_gs * 8.0;
        p_mut_type = 1.0/(1.0+Math.exp(-2.0*(x+param)));



        if (this.random.nextDouble() > p_mut_type)
            mutat_ind_gauss_global(solution);
		else
            mutat_ind_gauss_local(solution);

    }

    /* Mutate an individual : Gaussian mutation */
    public void mutat_ind_gauss_local(DoubleSolution solution) {
        int i, atleastone;
        double range, bxm;

        atleastone = this.random.nextInt(0, solution.getNumberOfVariables() - 1);

        for (i = 0; i < solution.getNumberOfVariables(); i++) {
            if (atleastone == i || (random.nextDouble() <= this.mutationProbability)) {

                range = solution.getUpperBound(i) - solution.getLowerBound(i);
                atleastone = -1;
                bxm = 0.1*box_muller(0.0, mut_stl);

                double aux = solution.getVariableValue(i) + bxm * range;

                if (aux < solution.getLowerBound(i))
                    aux = solution.getLowerBound(i);

                if (aux > solution.getUpperBound(i))
                    aux = solution.getUpperBound(i);


                solution.setVariableValue(i,aux);

            }
        }
    }

    /* Mutate an individual : Gaussian mutation */
    public void mutat_ind_gauss_global(DoubleSolution solution) {
        int i, atleastone;
        double range, bxm;

        atleastone = this.random.nextInt(0, solution.getNumberOfVariables() - 1);

        for (i = 0; i < solution.getNumberOfVariables(); i++) {
            if (atleastone == i || (random.nextDouble() <= this.mutationProbability)) {
                range = solution.getUpperBound(i) - solution.getLowerBound(i);
                atleastone = -1;
                bxm = 0.1*box_muller(0.0, mut_stg);

                double aux = solution.getVariableValue(i) + bxm * range;

                if (aux < solution.getLowerBound(i))
                    aux = solution.getLowerBound(i);

                if (aux > solution.getUpperBound(i))
                    aux = solution.getUpperBound(i);

                solution.setVariableValue(i,aux);
            }
        }
    }


    public double box_muller(double m, double s)	/* normal random variate generator */
    {
        double x1, x2, w, y1;

        do {
            x1 = 2.0 * random.nextDouble() - 1.0;
            x2 = 2.0 * random.nextDouble() - 1.0;
            w = x1 * x1 + x2 * x2;
        } while ( w >= 1.0 );
        w = Math.sqrt( (-2.0 * Math.log( w ) ) / w );
        y1 = x1 * w;


        return( m + y1 * s );
    }



}