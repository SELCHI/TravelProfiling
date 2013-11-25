package trendanalysis.forecast;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ListMultimap;
import trendanalysis.forecast.ForecastParamBuilder.EnumClassifier;
import trendanalysis.iohandle.ArffReader;
import trendanalysis.iohandle.IFInputReader;
import trendanalysis.iohandle.Messages;
import trendanalysis.iohandle.MySqlInputReader;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagMaker.Periodicity;
import weka.core.Instance;
import weka.core.Instances;

public class TrendForecastFacade {
	
	private TSWekaForcaster forecaster ;
	private WekaForecaster weeklyWekaForecaster;
	private WekaForecaster monthlyWekaForecaster;
	private HashMap<String, Object> nextWeeklyParams;
	HashMap<String, Object> nextMonthlyParams;
	public TrendForecastFacade(){
		this.forecaster = new TSWekaForcaster();
		this.nextWeeklyParams = this.getParams(Messages.getString("TrendForecastFacade.timeStampField"), 
				Messages.getString("TrendForecastFacade.fieldToForecast"), 
				1, Periodicity.WEEKLY,1, 52, ForecastParamBuilder.EnumClassifier.MultilayerPerceptron);
		this.nextMonthlyParams = this.getParams(Messages.getString("TrendForecastFacade.timeStampField"), 
				Messages.getString("TrendForecastFacade.fieldToForecast"), 
				1, Periodicity.MONTHLY, ForecastParamBuilder.EnumClassifier.MultilayerPerceptron);
		this.weeklyWekaForecaster = forecaster.initForecaster(nextWeeklyParams);
		this.monthlyWekaForecaster = forecaster.initForecaster(nextMonthlyParams);
		
	}

	public ListMultimap<Integer, String> forecastWeekly(
			Instances dataInstances,
			String timeStampField , 
			String fieldsToForecast ,
			int forecastLength){
		HashMap<String, Object> params = this.getParams(timeStampField, fieldsToForecast, 
				forecastLength, Periodicity.WEEKLY, 1, 52,ForecastParamBuilder.EnumClassifier.MultilayerPerceptron);
		ListMultimap<Integer, String> resultsMultimap  = this.forecaster.forecast(dataInstances ,params);
		
		return resultsMultimap;
	}
	
	public ListMultimap<Integer, String> forecastMonthly(
			Instances dataInstances,
			String timeStampField , 
			String fieldsToForecast ,
			int forecastLength){
		HashMap<String, Object> params = this.getParams(timeStampField, fieldsToForecast, 
				forecastLength, Periodicity.MONTHLY, ForecastParamBuilder.EnumClassifier.MultilayerPerceptron);
		ListMultimap<Integer, String> resultsMultimap  = this.forecaster.forecast(dataInstances ,params);
		
		return resultsMultimap;
	}
	
	public float getNextWeeklyForecast(
			Instances dataInstances,
			String timeStampField , 
			String fieldsToForecast){
		ListMultimap<Integer, String> resultsMultimap = this.forecastWeekly(dataInstances, timeStampField, fieldsToForecast, 1);
		List<String> result = resultsMultimap.get(0);
		float nextWeeklyForecast  = Float.parseFloat((String)result.get(1));
		return nextWeeklyForecast;
		
	}
	
	private ListMultimap<Integer, String> forecastWithStoredConfig(
			WekaForecaster wekaforcaster,
			Instances dataInstances,
			HashMap<String, Object> params){
		
		ListMultimap<Integer, String> resultsMultimap   = this.forecaster.forecast(wekaforcaster, dataInstances, params);
		return resultsMultimap;
		
	}
	
	public float getNextWeeklyForecast(
			Instances dataInstances){
		ListMultimap<Integer, String> resultsMultimap = this.forecastWithStoredConfig(this.weeklyWekaForecaster,
				dataInstances, this.nextWeeklyParams);
		List<String> result = resultsMultimap.get(0);
		float nextWeeklyForecast  = Float.parseFloat((String)result.get(1));
		return nextWeeklyForecast;
		
	}
	
	public float getNextMonthlyForecast(
			Instances dataInstances){
		ListMultimap<Integer, String> resultsMultimap = this.forecastWithStoredConfig(this.monthlyWekaForecaster,
				dataInstances, this.nextMonthlyParams);
		List<String> result = resultsMultimap.get(0);
		float nextMonthlyForecast  = Float.parseFloat((String)result.get(1));
		return nextMonthlyForecast;
		
	}
	
	private void printResults(ListMultimap<Integer, String> resultsMultimap) {
		
		for (int i = 0; i < resultsMultimap.keySet().size(); i++) {
			List<String> result = resultsMultimap.get(i);
			System.out.println(result);
		}
		
	}
	
	private HashMap<String, Object> getParams(String timeStampField , 
			String fieldsToForecast ,
			int forecastLength,
			Periodicity periodicity,
			int minLag,
			int maxLag,
			EnumClassifier classifier){
		
		HashMap<String, Object> params = new ForecastParamBuilder()
											.setFieldsToForecast(fieldsToForecast)	// Attribute name of the field to forecast
											.setTimeStampField(timeStampField)			// Attribute name of the time stamp field
											.setForecastLength(forecastLength)
											.setPeriodicity(periodicity)
											.setClassifier(classifier)
											.setMinLag(minLag)
											.setMaxLag(maxLag)
											.buildParams();
		
		return params;
	}
	
	private HashMap<String, Object> getParams(String timeStampField , 
			String fieldsToForecast ,
			int forecastLength,
			Periodicity periodicity,
			EnumClassifier classifier){		
		return this.getParams(timeStampField, fieldsToForecast, forecastLength, periodicity, 1, 12, classifier);
	}
	
	public static void main(String[] args) {
		IFInputReader inputReader = new MySqlInputReader();
		TrendForecastFacade forecaster = new TrendForecastFacade();
		ArffReader arff = new ArffReader();
		Instances dataInstances = arff.readArffFile(Messages.getString("TrendForecastFacade.weeklyArff"));

		//ListMultimap<Integer, String> resultsMultimap  = forecaster.forecastWeekly(dataInstances, "Date", "passengers", 15);
    	//forecaster.printResults(resultsMultimap);
    	
    	float next  = forecaster.getNextWeeklyForecast(dataInstances); //$NON-NLS-1$ //$NON-NLS-2$
    	System.out.println(next);
    	

	}
	
}
