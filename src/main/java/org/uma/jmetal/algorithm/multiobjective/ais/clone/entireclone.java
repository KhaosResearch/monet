package org.uma.jmetal.algorithm.multiobjective.ais.clone;

import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

public class entireclone extends Clone<List<DoubleSolution>> {

	/**
	 * @param No
	 */
	private int clonescale;



	public entireclone(int clonescale) {
		this.clonescale = clonescale;


	} // proportional clone

	/**
	 * /** Executes the operation
	 * 
	 * @param the parent population
	 * @return An object containing the offSprings
	 */

	@Override
	public List<DoubleSolution> execute(List<DoubleSolution> parents) {

		List<DoubleSolution> offspring = new ArrayList<>();

		for (int i = 0; i < parents.size(); i++) {
			for (int j = 0; j < clonescale; j++) {
				offspring.add(parents.get(i));
			}
		}
		return offspring;// */
	}
}
