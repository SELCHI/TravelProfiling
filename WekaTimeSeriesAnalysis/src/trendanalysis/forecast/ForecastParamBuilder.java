package trendanalysis.forecast;

import java.util.HashMap;

import weka.classifiers.Classifier;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.timeseries.core.TSLagMaker;
import weka.classifiers.timeseries.core.TSLagMaker.Periodicity;

/**
 * @author samith
 *
 */
public class ForecastParamBuilder {
    private String fieldsToForecast;
    private String timeStampField;
    private Periodicity periodicity = TSLagMaker.Periodicity.UNKNOWN;
    private int minLag = 1;
    private int maxLag = 12;
    private int forecastLength = 1;
    private Classifier classifier = new SMOreg();
    public static enum EnumClassifier {
    	SMOreg, MultilayerPerceptron, LinearRegression, GaussianProcesses 
    };
    
    /**
     * Builds the parameter key and value pairs
     * @return HashMap of parameter keys and values
     */
    public HashMap<String, Object> buildParams()
    {
    	HashMap<String, Object> params = new HashMap<String, Object>();
    	params.put("FieldsToForecast", fieldsToForecast);
    	params.put("TimeStampField", timeStampField);
    	params.put("Periodicity", periodicity);
    	params.put("MinLag", minLag);
    	params.put("MaxLag", maxLag);
    	params.put("ForecastLength", forecastLength);
    	params.put("Classifier", classifier);
        return params;
    }

    /**
     * @param fieldsToForecast Attributes(fields) names of fields to forecast as space separated as Strings
     * @return 
     */
    public ForecastParamBuilder setFieldsToForecast(String fieldsToForecast)
    {
        this.fieldsToForecast = fieldsToForecast;
        return this;
    }

    
    /**
     * @param timeStampField the name of the time stamp field
     * @return
     */
    public ForecastParamBuilder setTimeStampField(String timeStampField)
    {
        this.timeStampField = timeStampField;
        return this;
    }

    /**
     * @param periodicity the periodicity to use
     * @return
     */
    public ForecastParamBuilder setPeriodicity(Periodicity periodicity)
    {
        this.periodicity = periodicity;
        return this;
    }
    
    /**
     * @param minLag min the minimum lag to create
     * @return
     */
    public ForecastParamBuilder setMinLag(int minLag) {
		this.minLag = minLag;
		return this;
	}

	/**
	 * @param maxLag the maximum lag to create.
	 * @return
	 */
	public ForecastParamBuilder setMaxLag(int maxLag) {
		this.maxLag = maxLag;
		return this;
	}
	
	
	/**
	 * @param forecastLength number of forecasted values to produce for each target.
     *          E.g. a value of 5 would produce a prediction for t+1, t+2, ...,
     *          t+5. if no overlay data has been used during training)
	 * @return
	 */
	public ForecastParamBuilder setForecastLength(int forecastLength) {
		this.forecastLength = forecastLength;
		return this;
	}
	
	public ForecastParamBuilder setClassifier(EnumClassifier classifier) {

		switch (classifier) {
			case SMOreg: {
				this.classifier = new SMOreg();
				break;
			}
			case MultilayerPerceptron: {
				this.classifier = new MultilayerPerceptron();
				break;
			}
			case LinearRegression: {
				this.classifier = new LinearRegression();
				break;
			}
			case GaussianProcesses: {
				this.classifier = new GaussianProcesses();
				break;
			}

		}
		return this;
	}
	
	

}
