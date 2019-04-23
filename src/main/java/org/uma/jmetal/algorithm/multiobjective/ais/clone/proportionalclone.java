package org.uma.jmetal.algorithm.multiobjective.ais.clone;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;

import java.util.ArrayList;
import java.util.List;

public class proportionalclone extends Clone<List<DoubleSolution>> {

	/**
	 * @param No
	 */
	private int clonesize;
    private final CrowdingDistance<DoubleSolution> crowdingDistance = new CrowdingDistance<>() ;

	public proportionalclone(int clonesize) {
		this.clonesize=clonesize;
	} // proportional clone

	@Override
	public List<DoubleSolution> execute(List<DoubleSolution> parents) {

		List<DoubleSolution> offSpring=new ArrayList<DoubleSolution>(clonesize);
		double min_distance=0.0;
		double max_distance=1.0;
		double sum_distance=0.0;
		int k=0;
		for (k = 0; k < parents.size(); k++ ) {
            if(crowdingDistance.getAttribute(parents.get(k))!=Double.POSITIVE_INFINITY){
			    max_distance = crowdingDistance.getAttribute(parents.get(k));
				min_distance = crowdingDistance.getAttribute(parents.get(parents.size()-1));

				for(int l=0;l<k;l++){
				    crowdingDistance.setAttribute(parents.get(l),crowdingDistance.getAttribute(parents.get(k)));
				}
				break;
			}
		} // for
        if (crowdingDistance.getAttribute(parents.get(0))==Double.POSITIVE_INFINITY) {
			for(int l=0;l<parents.size();l++){
			    crowdingDistance.setAttribute(parents.get(l),0.0);
			}
		}//if all the points are in extreme region.
		for (k = 0; k < parents.size(); k++ ) {
			sum_distance+=crowdingDistance.getAttribute(parents.get(k));
			//TO-DO: parents.get(k).setmaxDistance(max_distance);
			//TO-DO: parents.get(k).setminDistance(min_distance);
		} // for
		double[] clones=new double[parents.size()];
		for(k=0;k<parents.size();k++){
            clones[k]= Math.ceil(clonesize*crowdingDistance.getAttribute(parents.get(k))/sum_distance);
			if(sum_distance==0){
				clones[k]=Math.ceil((double)clonesize/parents.size());
				System.out.print("zeros");
				System.out.print(clones[k]+" ");
			}
		  /* for (int l=0;l<clones;l++)
		      {
			     Solution Newsolution=new Solution(parents.get(k));
		       //if(remain>0){
		         offSpring.add(Newsolution);
		         //remain--;
		         //}
		    }*/
		}
		int remain=clonesize;
		int i=0;
		for(k=0;k<parents.size();k++){
			//int age=1;
			//parents.get(k).setclone_num(k);
			for(int l=0;l<clones[k];l++){
				if(remain>0){
					//Solution Newsolution=new Solution(parents.get(k));
					// Newsolution.age_=Newsolution.age_+age;
					offSpring.add(parents.get(k));
					remain--;
					//age++;
				}
				i++;
			}
			if(remain==0)
				break;
			//parents.get(k).age_+=clones[k];
			//parents.get(k).setmutationscale(PseudoRandom.randDouble());
			if(i>600)
			{
				System.out.print("zeros400");
			}
		}

		  /* for(int k=0;k<parents.size();k++){
		    	   Solution Newsolution=new Solution(parents.get(k));
			       offSpring.add(Newsolution);
		    	parents.get(k).age_+=1;
		  }*/

		return offSpring;//*/
	}
}
