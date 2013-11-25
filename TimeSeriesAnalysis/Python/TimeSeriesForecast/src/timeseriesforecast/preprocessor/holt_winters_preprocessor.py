'''
Created on Nov 16, 2013

@author: samith
'''
import pandas as pd
import pandas.rpy.common as com
import rpy2.robjects as robjects
from rpy2.robjects.packages import importr
class HoltWintersPreprocessor():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.base = importr('base')
        
    def preprocess(self , dataframe_object , data_col_name , \
                   sampling_freq = 'W' ,interpolate_method = 'time' ,):
        # resample data into weekly data
        resampled_df = dataframe_object.resample(sampling_freq)
        
        # fill missing values using interpolation
        resampled_df[data_col_name] = resampled_df[data_col_name].interpolate(method=interpolate_method)       
    
        return resampled_df
    
if __name__ == "__main__":
    test_hw_pre = HoltWintersPreprocessor()
    df = pd.read_csv('../test_data/data_with_mis.csv' ,parse_dates = ['Date'] ,index_col = 'Date', dayfirst = True )
    resampled_df = test_hw_pre.preprocess(df, 'Departments')
    print resampled_df.head()