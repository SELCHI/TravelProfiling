'''
Created on Nov 16, 2013

@author: samith
'''
import pandas as pd
import pandas.rpy.common as com
import rpy2.robjects as robjects
from rpy2.robjects.packages import importr
from timeseriesforecast.models.abstract_model import AbstractModel
from timeseriesforecast.preprocessor.preprocessor import Preprocessor
class HoltWintersModel(AbstractModel):
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
    
    def forecast(self , resampled_df ,data_freq = 52 , number_of_predctions = 5):
        
        # start and end date of the series
        start_date = pd.to_datetime(resampled_df.ix[0].name).date()
        end_date = pd.to_datetime(resampled_df.ix[-1].name).date()
        
        r_series = self.convert_to_r_series(resampled_df, start_date, data_freq)
    
        # fit the model
        log_r_series = self.base.log(r_series)
        holt_winter_fit = self.stats.HoltWinters(r_series)
        
        # forecast
        holt_winter_forecast = self.forecast_lib.forecast_HoltWinters(holt_winter_fit , \
                                                                  h = number_of_predctions)
        # prepare and convert results to pandas dataframe
        reshaped_melted_results= self.reshape.melt(holt_winter_forecast) 
        if data_freq == 52:
            forecast_duration = self.base.as_Date(end_date.strftime('%Y-%m-%d')).ro +\
                                (self.base.seq(1,number_of_predctions).ro * 7)
            myxts = self.xts.xts(reshaped_melted_results, forecast_duration)
            results_field =  'value.value.Point.Forecast'
        elif  data_freq == 12:
            myxts =  holt_winter_forecast 
            results_field =  'value.Point.Forecast' 
            
        results_pd_df = com.convert_robj(self.r.melt(myxts)) 
        results_pd_ts  = results_pd_df[results_field ]
        
        return (results_pd_ts ,holt_winter_forecast)
    
    def plot_forecast(self , holt_winter_forecast):
        self.r.plot(holt_winter_forecast)
        
        
if __name__ == "__main__":
    
    test_pre = Preprocessor()
    test_hw_model = HoltWintersModel()
    df = pd.read_csv('../test_data/data_with_mis.csv' ,parse_dates = ['Date'] ,index_col = 'Date', dayfirst = True )
    '''
    resampled_df = test_pre.preprocess_weekly_to_weekly(df, 'Departments')
    print resampled_df.tail()
    (results_pd_ts ,holt_winter_forecast) = test_hw_model.forecast(resampled_df)
    test_hw_model.plot_forecast(holt_winter_forecast)
    raw_input("Enter key to exit")
    '''
    resampled_df = test_pre.preprocess_weekly_to_monthly(df, 'Departments')
    
    (results_pd_ts ,holt_winter_forecast) = test_hw_model.forecast(resampled_df , data_freq =12)
    
    print results_pd_ts.head()
    test_hw_model.plot_forecast(holt_winter_forecast)
    raw_input("Enter key to exit")
    
