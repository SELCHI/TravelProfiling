'''
Created on Nov 18, 2013

@author: samith
'''
import pandas as pd
import pandas.rpy.common as com
import rpy2.robjects as robjects
from rpy2.robjects.packages import importr
from timeseriesforecast.models.abstract_model import AbstractModel
from timeseriesforecast.preprocessor.preprocessor import Preprocessor
class ArimaModel(AbstractModel):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.r = robjects.r
        self.base = importr('base')
        self.forecast_lib = importr('forecast')
        self.stats = importr('stats')
        self.reshape = importr("reshape")
        self.xts = importr("xts", robject_translations = {".subset.xts": "_subset_xts2", \
                                                     "to.period": "to_period2"})
    def forecast(self , resampled_df ,data_freq = 12 , number_of_predctions = 5):
        
        # start and end date of the series
        start_date = pd.to_datetime(resampled_df.ix[0].name).date()
        end_date = pd.to_datetime(resampled_df.ix[-1].name).date()
        
        r_series = self.convert_to_r_series(resampled_df, start_date, data_freq)
        # fit the model
        arima_fit = self.forecast_lib.auto_arima(r_series )
        
        # forecast
        arima_forecast_results = self.forecast_lib.forecast_Arima(arima_fit , h=number_of_predctions)
        # prepare and convert results to pandas dataframe
        results_pd_df = com.convert_robj(self.r.melt(arima_forecast_results))
        results_pd_ts  = results_pd_df['value.Point.Forecast' ]
        results_pd_ts.index = results_pd_ts.index.to_datetime('datetime64[ns]')
        results_pd_ts = results_pd_ts.resample('MS')
        
        return (results_pd_ts ,arima_forecast_results)
    
    def get_start_for_r_series(self , start_date):
        return self.base.c(start_date.year,start_date.month)
    
    def plot_forecast(self , arima_forecast_results):
        self.forecast_lib.plot_forecast(arima_forecast_results)
    
if __name__ == "__main__":
    test_pre = Preprocessor()
    test_arima_model = ArimaModel()
    df = pd.read_csv('../test_data/data_with_mis.csv' ,parse_dates = ['Date'] ,index_col = 'Date', dayfirst = True )
    
    resampled_df = test_pre.preprocess_weekly_to_monthly(df, 'Departments')
    
    (results_pd_ts ,arima_forecast_results) = test_arima_model.forecast(resampled_df)
    print results_pd_ts.head(10)
    test_arima_model.plot_forecast(arima_forecast_results)
    raw_input("Enter key to exit")
    