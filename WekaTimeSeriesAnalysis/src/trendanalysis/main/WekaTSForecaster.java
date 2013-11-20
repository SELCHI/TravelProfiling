package trendanalysis.main;

import trendanalysis.forecast.TrendForecastFacade;
import trendanalysis.iohandle.ArffReader;
import trendanalysis.iohandle.Messages;
import weka.core.Instances;

public class WekaTSForecaster {
	private TrendForecastFacade forecaster;
	private ArffReader arff; 
	
	public TrendForecastFacade getForecaster() {
		return forecaster;
	}
	public ArffReader getArff() {
		return arff;
	}
	
	public WekaTSForecaster(){
		this.forecaster = new TrendForecastFacade();
		this.arff = new ArffReader();
	}
	
	public float getNextWeeklyForecast(){
		Instances dataInstances = this.arff.readArffFile(Messages.getString("TrendForecastFacade.weeklyArff"));
		float value = this.forecaster.getNextWeeklyForecast(dataInstances);
		System.out.println(value);
		return value;		
	}
	
	public float getNextMonthlyForecast(){
		Instances dataInstances = this.arff.readArffFile(Messages.getString("TrendForecastFacade.monthlyArff"));
		float value = this.forecaster.getNextMonthlyForecast(dataInstances);
		System.out.println(value);
		return value;		
	}
	

}
