package GenNetWeaver;

import java.io.*;

public class ConvertToDREAM {

	public static void main(String[] args) throws FileNotFoundException {
        int set = 0; //  to consider both strenghts gs and hs
        int sol = 0; // solution position in VAR file of pareto front
        double threshold = 0.01; // 0.01 threshold to do not consider a given arc

		File input_file = new File(args[1]);
        File output_file = new File(args[0]);

        int lines = 0;
        BufferedReader reader = new BufferedReader(new FileReader(args[1])); //VAR
        try {

            while (reader.readLine() != null) lines++;
            reader.close();
        } catch (FileNotFoundException fnf) {
            // Oh dear, error occurred opening file
            System.out.println(fnf.getMessage());
        } catch (IOException ioex) {
            System.out.println(ioex.getMessage());
        } catch (NumberFormatException nfe) {
            System.out.println("The file contained invalid data");
        }

        sol = lines-1;

        //File output_file = new File("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM3\\scripts\\challenge4_script\\predictions\\InSilicoSize10_Ecoli2.txt");
        //File output_file = new File("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM4\\DREAM4_Challenge2_Script\\INPUT\\my_predictions\\10\\DREAM4_Example_InSilico_Size10_5.txt");

        //File input_file = new File("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM4\\DREAM4-in-silico challenge\\Size 10\\DREAM4 training data\\insilico_size100_1\\insilico_size100_1_timeseries.tsv");
        //File output_file = new File("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM4\\DREAM4-in-silico challenge\\Size 10\\DREAM4 training data\\insilico_size100_1\\insilico_size100_1_timeseries.txt");

        D3_ResultJMetal var_file = new D3_ResultJMetal(input_file);
        input_file.length();
        //D3_TrajectoriesTSV gn = new D3_TrajectoriesTSV(input_file);


		var_file.load(-1); // load gs strenghts
        var_file.load(1);  // load hs strenghts

        File f = var_file.VARFile_DREAMFormat(output_file,set,sol,threshold);
        System.out.println("SET " +set+ " SOL " + sol);
        System.out.println(f.getAbsolutePath());
	
	}

}
