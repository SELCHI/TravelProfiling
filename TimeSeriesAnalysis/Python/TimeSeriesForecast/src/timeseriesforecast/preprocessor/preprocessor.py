'''
Created on Nov 18, 2013

@author: samith
'''
import pandas as pd
import pandas.rpy.common as com
import rpy2.robjects as robjects
from rpy2.robjects.packages import importr
class Preprocessor():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.base = importr('base')
        
    def preprocess_weekly_to_weekly(self , dataframe_object , data_col_name , \
                   sampling_freq = 'W-MON' ,interpolate_method = 'time' ):

        # resample data into weekly data
        resampled_df = dataframe_object.resample(sampling_freq , how ='first')
        # fill missing values using interpolation
        resampled_df[data_col_name] = resampled_df[data_col_name].interpolate(method=interpolate_method)        
    
        return resampled_df
    
    def preprocess_weekly_to_monthly(self , dataframe_object , data_col_name , \
                   sampling_freq = 'MS' ,interpolate_method = 'time' ,):
        # resample data into weekly data
        resampled_df = dataframe_object.resample('W')
        
        # fill missing values using interpolation
        resampled_df[data_col_name] = resampled_df[data_col_name].interpolate(method=interpolate_method)
        
        # resample again for monthly
        monthly_resmpled_df  = resampled_df.resample(sampling_freq ,how = 'sum' ,closed = 'right')
        
        monthly_resmpled_df = self.last4_wks_same_month_filter(resampled_df, monthly_resmpled_df)
        
        return monthly_resmpled_df
    
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
        