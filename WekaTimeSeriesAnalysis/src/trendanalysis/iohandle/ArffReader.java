package trendanalysis.iohandle;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ArffReader {
	public Instances readArffFile(String path){
		Instances data = null;
		
		try {
			DataSource source = new DataSource(path);
			data = source.getDataSet();
			data.setClassIndex(data.numAttributes() - 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;

	}
}
