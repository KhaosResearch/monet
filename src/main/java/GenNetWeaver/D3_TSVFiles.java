package GenNetWeaver;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.io.File;
import java.io.FileNotFoundException;

public abstract class D3_TSVFiles {
	//FIELDS
	protected File fich;
	protected String[] genes;
	
	//METHODS
	/**
	 * @return true if there is a File selected
	 */
	public boolean fileSelected(){
		return fich == null? false : true;
	}
	
	
	/**
	 * @return selected file
	 * @throws NullPointerException
	 */
	public File getFile() throws NullPointerException{
		if(fich == null) throw new NullPointerException("File isn't already selected");
		return fich;
	}
	
	
	/**
	 * @return selected file's name
	 * @throws NullPointerException
	 */
	public String getFileName() throws NullPointerException{
		if(fich == null) throw new NullPointerException("File isn't already selected");
		return fich.getName();
	}
	
	
	/**
	 * @return an array with all genes included on this file
	 * @throws InformationNotFoundException
	 */
	public String[] getGenes() throws InformationNotFoundException {
		if(genes == null || genes.length <= 0) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return genes;
	}


	/**
	 * Return the index of a gene name given. Is useful to know what columns saves this gene information.
	 * @param gene it's which index we want to know.
	 * @return the index if the gene is found or -1 if it ins't on this file.
	 * @throws InformationNotFoundException
	 */
	public int getIndexOfGene(String gene) throws InformationNotFoundException {
		if(genes == null || genes.length <= 0) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		if(gene.isEmpty()) return -1;
		int index = -1;
		for(int i=0;i<genes.length;++i)
			if(genes[i].equalsIgnoreCase(gene)){
				index = i;
				i = genes.length;
			}
		return index;
	}


	/**
	 * @return number of genes included on this file
	 * @throws InformationNotFoundException
	 */
	public int getNumGenes() throws InformationNotFoundException {
		if(genes.length <= 0) throw new InformationNotFoundException("This information isn't already loaded. Please use load() method");
		return genes.length;
	}
	
	
	public abstract boolean load() throws FileNotFoundException, NullPointerException;
	
	/**
	 * Change the file used to load the information
	 * @param f = new file
	 */
	public void setFile(File f){
		fich = f;
	}
}
