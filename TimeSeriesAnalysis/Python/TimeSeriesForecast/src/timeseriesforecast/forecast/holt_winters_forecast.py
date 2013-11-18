'''
Created on Nov 17, 2013

@author: samith
'''
from timeseriesforecast.preprocessor.holt_winters_preprocessor import HoltWintersPreprocessor
from timeseriesforecast.models.holt_winters_model import HoltWintersModel
class HoltWintersForecast():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.hw_preprocessor = HoltWintersPreprocessor()
        self.hw_model = HoltWintersModel()
        
    def forecast_holt_winters(self , pd_df_object , data_col_name):
        resampled_df = self.hw_preprocessor.preprocess(pd_df_object, 'Departments')    
        (results_pd_df ,holt_winter_forecast) = self.hw_model.forecast(resampled_df)