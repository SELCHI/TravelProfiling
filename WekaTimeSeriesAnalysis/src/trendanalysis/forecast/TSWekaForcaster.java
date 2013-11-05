package trendanalysis.forecast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import trendanalysis.iohandle.Messages;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagMaker;
import weka.classifiers.timeseries.core.TSLagMaker.Periodicity;
import weka.core.Instances;

public class TSWekaForcaster {

	public Multimap<Integer, String> forecast(Instances airline , HashMap<String, Object> params) {
		
		List<List<NumericPrediction>> forecast = null;
		Multimap<Integer, String> outputMultimap = null;
		try {
			//print Params
			printParams(params);
			// new forecaster
			WekaForecaster forecaster = new WekaForecaster();

			// set the targets we want to forecast. This method calls
			// setFieldsToLag() on the lag maker object for us
			forecaster.setFieldsToForecast(params.get("FieldsToForecast").toString()); //$NON-NLS-1$

			// default underlying classifier is SMOreg (SVM) - we'll use
			// gaussian processes for regression instead
			forecaster.setBaseForecaster(new SMOreg());

			forecaster.getTSLagMaker().setTimeStampField(params.get("TimeStampField").toString()); // date time stamp //$NON-NLS-1$
			forecaster.getTSLagMaker().setMinLag((int)params.get("MinLag")); //$NON-NLS-1$
			forecaster.getTSLagMaker().setMaxLag((int)params.get("MaxLag")); //$NON-NLS-1$

			// add a month of the year indicator field
			//forecaster.getTSLagMaker().setAddDayOfWeek(true);

			// set Periodicity
			forecaster.getTSLagMaker().setPeriodicity(
					(Periodicity)params.get("Periodicity")); //$NON-NLS-1$

			// build the model
			forecaster.buildForecaster(airline, System.out);

			// prime the forecaster with enough recent historical data
			// to cover up to the maximum lag. In our case, we could just supply
			// the 12 most recent historical instances, as this covers our maximum
			// lag period
			forecaster.primeForecaster(airline);
			
			// Set timestamps of the output since forecast() metho
			// does not return the results as timestamps , predicted value pair
			outputMultimap = setOutputTimeStamps(params, forecaster);
			System.out.println("Transforming input data completed...");
			System.out.println("Prediction Started...");
			int forecastLength = (int)params.get("ForecastLength"); //$NON-NLS-1$
			forecast = forecaster.forecast(forecastLength,System.out);
			System.out.println("Prediction completed successfully...");
			
			// combine output with the time stamps
			outputMultimap = combineOutputWithTimestapms(forecast , forecastLength ,outputMultimap);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outputMultimap;
	}

	private Multimap<Integer, String> setOutputTimeStamps(HashMap<String, Object> params,
			WekaForecaster forecaster) throws Exception {
		Multimap<Integer, String> outputMultimap = ArrayListMultimap.create();
		TSLagMaker lagMaker = forecaster.getTSLagMaker();
		double lastTimeFromPrime = lagMaker.getCurrentTimeStampValue();
		double currentTime = lastTimeFromPrime;			
		SimpleDateFormat formatter = new SimpleDateFormat(Messages.getString("TSWekaForcaster.dateFormat")); //$NON-NLS-1$
		for (int i = 0; i < (int)params.get("ForecastLength"); i++) { //$NON-NLS-1$
			currentTime = lagMaker.advanceSuppliedTimeValue(currentTime);
			Date d = new Date((long)currentTime);
			outputMultimap.put(i ,formatter.format(d));
		}
		
		return outputMultimap;
	}
	
	private Multimap<Integer, String> combineOutputWithTimestapms(
			List<List<NumericPrediction>> forecastedInstances,
			int forecastLength,
			Multimap<Integer, String> outputMultimap) {
		
		for (int i = 0; i < forecastLength; i++) {
	        List<NumericPrediction> predsAtStep = forecastedInstances.get(i);
	        NumericPrediction predForTarget = predsAtStep.get(0);
	        outputMultimap.put(i ,predForTarget.predicted()+""); //$NON-NLS-1$
	      }
		
		return outputMultimap;
	}
	
	private void printParams(HashMap<String, Object> params){
		for (String name: params.keySet()){
            String key =name.toString();
            String value = params.get(name).toString();  
            System.out.println(key + ": " + value);   //$NON-NLS-1$
    	} 
	}
}
