package trendanalysis.forecast;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ListMultimap;

import trendanalysis.iohandle.IFInputReader;
import trendanalysis.iohandle.Messages;
import trendanalysis.iohandle.MySqlInputReader;
import weka.classifiers.timeseries.core.TSLagMaker.Periodicity;
import weka.core.Instances;

public class TrendAnalyzer {
	
	public static void main(String[] args) {
		IFInputReader inputReader = new MySqlInputReader();
		TSWekaForcaster forecaster = new TSWekaForcaster();
		
		Instances retrieveInstances = inputReader
				.retrieveInstances(
						Messages.getString("TrendAnalyzer.dbUN"), 
						Messages.getString("TrendAnalyzer.dbPW"), 
						Messages.getString("TrendAnalyzer.dbName"), 
						Messages.getString("TrendAnalyzer.dbTblName")); 
		
    	HashMap<String, Object> params = new ForecastParamBuilder()
    										.setFieldsToForecast("passengers")	// Attribute name of the field to forecast
    										.setTimeStampField("Date")			// Attribute name of the time stamp field
    										.setForecastLength(12)
    										.setPeriodicity(Periodicity.WEEKLY)
    										.setClassifier(ForecastParamBuilder.EnumClassifier.MultilayerPerceptron)
    										.buildParams();
		
    	ListMultimap<Integer, String> resultsMultimap  = forecaster.forecast(retrieveInstances ,params);
    	printResults(resultsMultimap);

	}
	
	public static void printResults(ListMultimap<Integer, String> resultsMultimap) {
		
		for (int i = 0; i < resultsMultimap.keySet().size(); i++) {
			List<String> result = resultsMultimap.get(i);
			System.out.println(result);
		}
		
	}

}
