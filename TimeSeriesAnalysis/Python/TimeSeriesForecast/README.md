Python Time Series Forecast
================================

## How to use
First set the MYSQL_HOST_IP and ONTOLOGY_URL values of [config file](https://github.com/SELCHI/TravelProfiling/blob/master/TimeSeriesAnalysis/Python/TimeSeriesForecast/src/timeseriesforecast/main/config.py) accordingly.
Then run WekaTimeSeriesAnalysis/wekaserver/wekaserver.jar

    java -jar wekaserver.jar 
    
finally run run_timeseries_analysis.py

	python run_timeseries_analysis.py
    
look at timeseriesforecast.main.MainForecast class

## Dependencies
### python
- [pandas](http://pandas.pydata.org): 0.12.0 or higher
- [rpy2](http://rpy.sourceforge.net/rpy2/doc-2.2/html/index.html): 2.2 or higher
- [py4j](http://py4j.sourceforge.net/install.html): 0.8 or higher

### R
- [R](http://www.r-project.org/): 3.0.2 or higher
- [R eXtensible Time Series package](http://cran.r-project.org/web/packages/xts/index.html)
- [R forecast package](http://cran.r-project.org/web/packages/forecast/index.html)
    - [how to install a R package](http://www.r-bloggers.com/installing-r-packages/)
