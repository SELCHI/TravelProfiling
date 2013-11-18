'''
Created on Nov 18, 2013

@author: samith
'''
from abc import ABCMeta, abstractmethod
import pandas as pd
import pandas.rpy.common as com
import rpy2.robjects as robjects
from rpy2.robjects.packages import importr
class AbstractModel(object):
    '''
    classdocs
    '''
    __metaclass__ = ABCMeta

    def __init__(self):
        '''
        Constructor
        '''
        self.base = importr('base')
        

    def convert_to_r_series(self ,resampled_df, start_date ,data_freq):
        
        # convert to R dataframe
        r_dataframe = com.convert_to_r_matrix(resampled_df)
        
        if data_freq == 12:
            start_val = self.get_start_for_r_monthly(start_date)
        elif data_freq == 52:
            start_val = self.get_start_for_r_series_weekly(start_date)
        else:
            raise NotImplementedError( "Implemented only for other frequencies 12 and 52" )
        
        #convert to R time Series
        ts = robjects.r['ts']
        r_series = ts(r_dataframe, 
                      start=start_val,
                      frequency=data_freq
                      )
        return r_series
    
    def get_start_for_r_series_weekly(self , start_date):
        start_wk_number = start_date.isocalendar()[1]
        return self.base.c(start_date.year,start_wk_number)
    
    def get_start_for_r_monthly(self , start_date):
        return self.base.c(start_date.year,start_date.month)