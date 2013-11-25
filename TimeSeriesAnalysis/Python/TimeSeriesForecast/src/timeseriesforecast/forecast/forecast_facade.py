'''
Created on Nov 18, 2013

@author: samith
'''
from timeseriesforecast.preprocessor.preprocessor import Preprocessor
from timeseriesforecast.models.holt_winters_model import HoltWintersModel
from timeseriesforecast.models.arima_model import ArimaModel
import pandas as pd
class ForecastFacade():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.preprocessor = Preprocessor()
        self.hw_model = HoltWintersModel()
        self.arima_model = ArimaModel()
        
    def get_holt_winters_weekly(self , pd_df_object , data_col_name ,\
                                sampling_freq = 'W-MON' ,interpolate_method = 'time',\
                                data_freq = 52 , number_of_predctions = 5):
        
        resampled_df = self.preprocessor.preprocess_weekly_to_weekly(pd_df_object, 
                                                                        data_col_name ,
                                                                        sampling_freq ,
                                                                        interpolate_method)  
          
        (results_pd_ts ,holt_winter_forecast) = self.hw_model.forecast(resampled_df,data_freq ,number_of_predctions)
        return (results_pd_ts ,resampled_df)
    
    def get_arima_monthly(self , pd_df_object , data_col_name ,\
                                sampling_freq = 'MS' ,interpolate_method = 'time',\
                                data_freq = 12 , number_of_predctions = 5):
        
        resampled_df = self.preprocessor.preprocess_weekly_to_monthly(pd_df_object, 
                                                                        data_col_name,
                                                                        sampling_freq ,
                                                                        interpolate_method)  
          
        (results_pd_ts ,arima_forecast_results) = self.arima_model.forecast(resampled_df,data_freq ,number_of_predctions)
        return (results_pd_ts ,resampled_df)
    
    def get_next_wk_month_forecast(self , pd_df_object , data_col_name ):
        (wk_results ,wk_resampled_df) = self.get_holt_winters_weekly(pd_df_object, data_col_name, number_of_predctions = 2 )
        wk_final = (wk_results[0] + wk_results[1] * 1.0) /2
        
        (month_results,month_resampled_df) = self.get_arima_monthly(pd_df_object, data_col_name, number_of_predctions = 1 )
        
        return (wk_final , month_results[0] ,wk_resampled_df , month_resampled_df)
    
    def get_weekly_resampled_data(self,pd_df_object , data_col_name ,\
                                sampling_freq = 'W-MON' ,interpolate_method = 'time'):
        resampled_df = self.preprocessor.preprocess_weekly_to_monthly(pd_df_object, 
                                                                        data_col_name,
                                                                        sampling_freq ,
                                                                        interpolate_method)  
        return resampled_df
    
    def get_monthly_resampled_data(self,pd_df_object , data_col_name ,\
                                sampling_freq = 'MS' ,interpolate_method = 'time'):
        resampled_df = self.preprocessor.preprocess_weekly_to_monthly(pd_df_object, 
                                                                        data_col_name,
                                                                        sampling_freq ,
                                                                        interpolate_method)  
        return resampled_df
    
    def get_weekly_n_monthly_resampled(self ,pd_df_object , data_col_name):
        weekly_resampled = self.get_weekly_resampled_data(pd_df_object , data_col_name)
        monthly_resampled = self.get_monthly_resampled_data(pd_df_object , data_col_name)
        
        return (weekly_resampled ,monthly_resampled)
    
    def prepare_final_results(self ,weka_weekly , weka_monthly,\
                              python_week =None , python_month = None):
        final_weekly = weka_weekly
        final_montly = weka_monthly
        weka_factor = 1.3
        python_factor = 2-weka_factor
        if python_week is not None:
            final_weekly = ((weka_weekly*weka_factor) +  (python_week*python_factor))/2.0
        if python_month is not None:
            final_montly = ((weka_monthly*weka_factor) +  (python_month*python_factor))/2.0
        
        return (int(final_weekly) ,int(final_montly) )
        
    
if __name__ == "__main__":   
    forecast_fs = ForecastFacade()
    df = pd.read_csv('../test_data/data_with_mis.csv' ,parse_dates = ['Date'] ,index_col = 'Date', dayfirst = True )
    
    (wk_results_pd_ts,resampled_df) = forecast_fs.get_holt_winters_weekly(df , 'Departments')
    print wk_results_pd_ts.head()
    
    (mon_results_pd_ts,resampled_df) = forecast_fs.get_arima_monthly(df , 'Departments')
    print mon_results_pd_ts.head()
    
    (wk_final , month_results,wk_resampled_df , month_resampled_df) = forecast_fs.get_next_wk_month_forecast(df , 'Departments')
    print "weekly %s" %(wk_final)
    print "monthly %s "%(month_results)
    
        