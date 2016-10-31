package GenNetWeaver;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;


/**
 * This class open a *-null-mutants.tsv files given for DREAM3 
 * Challenge 4 (http://wiki.c2b2.columbia.edu/dream/index.php?title=D3c4)
 * and save all this info in this class' fields.
 *  
 *  To use it you've to create the instance specifying a file or using setFile() and, after, 
 *  use load() method to load all the info.
 * 
 *  *-null-mutants.tsv files saves information of a knock-out simulation. It means that
 *  for simulate the experiment of the file N they decrease the transcript ratio of the gene
 *  N to zero. It 
 *  REMEMBER: this class saves this matrix including a special experiment where all genes
 */
public class D3_NullMutantsTSV extends D3_HeterozygousTSV {
	//CONSTRUCTORS
	public D3_NullMutantsTSV() {
		this(null);
	}
	public D3_NullMutantsTSV(File tsv){
		super(tsv);
	}
}
