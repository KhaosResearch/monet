package org.uma.jmetal.algorithm.multiobjective.ais.clone;


import org.uma.jmetal.solution.DoubleSolution;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class implementing a factory for crossover operators.
 */
public class CloneFactory {

	/**
	 * Gets a crossover operator through its name.
	 * 
	 * @param name
	 *            Name of the operator
	 * @return The operator
	 */
	public static Clone<List<DoubleSolution>> getClone(String name, int cloneSize) {
		if (name.equalsIgnoreCase("entireclone"))
			return new entireclone(cloneSize);
		if (name.equalsIgnoreCase("proportionalclone"))
			return new proportionalclone(cloneSize);
		if (name.equalsIgnoreCase("proportional2"))
			return new proportional2(cloneSize);
		if (name.equalsIgnoreCase("proportional5"))
			return new proportional5(cloneSize);
		else {
			Logger.getGlobal().severe("CloneFactory.getCloneOperator. "
					+ "Operator '" + name + "' not found ");
			try {
				throw new ClassNotFoundException("Exception in " + name	+ ".getCloneOperator()");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} // else
		return null;
	} // getClone
} // CloneFactory

