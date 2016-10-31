package GenNetWeaver;

import java.io.File;

public class testing {

	public static void main(String[] args) {
		File input_file = new File("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM3\\InSilicoSize10\\InSilicoSize10-Yeast3-trajectories.tsv");
        File output_file = new File("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM3\\InSilicoSize10\\InSilicoSize10-Yeast3-trajectories.txt");

        //File input_file = new File("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM4\\DREAM4-in-silico challenge\\Size 10\\DREAM4 training data\\insilico_size100_1\\insilico_size100_1_timeseries.tsv");
        //File output_file = new File("C:\\Users\\Jose-Manuel\\Software\\jmetalmaven\\datasets-gnw\\DREAM4\\DREAM4-in-silico challenge\\Size 10\\DREAM4 training data\\insilico_size100_1\\insilico_size100_1_timeseries.txt");

		//GNW_TimeseriesTSV gn = new GNW_TimeseriesTSV(input_file);
        D3_TrajectoriesTSV gn = new D3_TrajectoriesTSV(input_file);


		//gn.load();
        //File f = gn.convertToDCUData();
        File f = gn.convertToKhaosData(output_file);
        System.out.println(f.getAbsolutePath());
	
	}

}
