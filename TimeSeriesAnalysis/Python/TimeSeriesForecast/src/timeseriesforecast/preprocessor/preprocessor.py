'''
Created on Nov 18, 2013

@author: samith
'''
import pandas as pd
import numpy as np
import pandas.rpy.common as com
import rpy2.robjects as robjects
from rpy2.robjects.packages import importr
import time
from timeseriesforecast.main.config import TS_START_DATE
class Preprocessor():
    '''
    Preprocessor class
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.base = importr('base')
        
    def preprocess_weekly_to_weekly(self , dataframe_object , data_col_name , \
                   sampling_freq = 'W-MON' ,interpolate_method = 'time' ):
        '''
        Preprocess and resample weekly data to weekly data
        '''
        resampled_df = self.set_date_range(dataframe_object, data_col_name, interpolate_method) 
        
        return resampled_df
    
    def preprocess_weekly_to_monthly(self , dataframe_object , data_col_name , \
                   sampling_freq = 'MS' ,interpolate_method = 'time' ,):
        '''
        Preprocess and resample weekly data to monthly data
        '''
        resampled_df = self.set_date_range(dataframe_object, data_col_name, interpolate_method)
        
        # resample again for monthly
        monthly_resmpled_df  = resampled_df.resample(sampling_freq ,how = 'sum' ,closed = 'right')
        
        monthly_resmpled_df = self.last4_wks_same_month_filter(resampled_df, monthly_resmpled_df)
        
        return monthly_resmpled_df
    
    def set_date_range(self,dataframe_object ,data_col_name ,interpolate_method = 'time'):
        '''
        Adjest and reindex data to time period, which use to forecast future values
        '''
        sampling_freq = "W-MON"
        # convert index to Pandas DateTimeIndex
        dataframe_object.index = pd.to_datetime(dataframe_object.index)

        today = time.strftime("%Y-%m-%d")
        # date range to consider for forecast
        date_range = pd.date_range(start = TS_START_DATE, end = today,freq= sampling_freq)
        
        # resample data into weekly data
        resampled_df = dataframe_object.resample(sampling_freq , how ='first')
        
        # reindex with the required daterange
        resampled_df = resampled_df.reindex(date_range)
        
        # fill first and last values if they are Nan
        if np.isnan(resampled_df.ix[0]):
            resampled_df.ix[0] = resampled_df.mean()
        if np.isnan(resampled_df.ix[-1]):
            resampled_df.ix[-1] = resampled_df.mean()
            
        # fill missing values using interpolation
        resampled_df[data_col_name] = resampled_df[data_col_name].interpolate(method=interpolate_method)
   
        return resampled_df
    
    def last4_wks_same_month_filter(self , weekly_resampled_df ,monthly_resmpled_df):
        '''
        Check whether last four weekly data belongs to the same month or not,
        If not, remove the entry of the corresponding month from the monthly_resmpled_df
        '''
        last_wk_month = pd.to_datetime(weekly_resampled_df.ix[-1].name).date().month
        last_4th_month = pd.to_datetime(weekly_resampled_df.ix[-4].name).date().month
        remove_last_month = bool(last_wk_month != last_4th_month)
        if remove_last_month:
            monthly_resmpled_df = monthly_resmpled_df.drop(monthly_resmpled_df.index[-1])
            
        return monthly_resmpled_df
        