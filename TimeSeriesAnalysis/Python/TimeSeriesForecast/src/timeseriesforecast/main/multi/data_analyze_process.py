'''
Created on Nov 24, 2013

@author: samith
'''
from multiprocessing import Process 
import os
from timeseriesforecast.main.config import BASE_WEEKLY_ARFF_PATH, BASE_MONTHLY_ARFF_PATH, INDEX_COL, DATA_COL
from timeseriesforecast.forecast.forecast_facade import ForecastFacade
from timeseriesforecast.io.weka_manager import WekaManager
from timeseriesforecast.io.arff_handler import ArffHandler
class DataAnalyzeProcess(Process):
    '''
    classdocs
    '''


    def __init__(self , input_queue,output_queue ,identifier =0 ,index_col_name = INDEX_COL , data_col_name = DATA_COL):
        '''
        Constructor
        '''
        super(DataAnalyzeProcess, self).__init__()
        self.input_queue = input_queue
        self.output_queue =output_queue 
        self.identifier = identifier        
        self.weekly_path = BASE_WEEKLY_ARFF_PATH+"weekly.arff"
        self.weekly_path = self.weekly_path  %(identifier)
        self.monthly_path = BASE_MONTHLY_ARFF_PATH+"monthly.arff" 
        self.monthly_path= self.monthly_path %(identifier)
        self.index_col_name = index_col_name
        self.data_col_name = data_col_name
        self.forecast_facade = ForecastFacade()
        self.arff_manager = ArffHandler()
        self.weka_manager = WekaManager()
        
    def analyze_data(self):
        input_data = self.input_queue.get()
        
        while(type(input_data) is not str): # if input queue is not reach the end of data, loop
            
            pd_df_object = input_data["pd_df_object"]
            # get weekly and monthly forecast from holt-winters arima model
            (python_week , python_month,wk_resampled_df , month_resampled_df) = \
            self.forecast_facade.get_next_wk_month_forecast(pd_df_object, data_col_name = self.data_col_name )
            
            # get resampled data only
            #(wk_resampled_df ,month_resampled_df) = self.get_weekly_and_monthly_resampled(pd_df_object) 
             
            # save preprocessed data in arff format for weka forecaster
            self.arff_manager.saveAsArff(wk_resampled_df, index_col = self.index_col_name, data_col_name = self.data_col_name,path=  self.weekly_path)        
            self.arff_manager.saveAsArff(month_resampled_df, index_col = self.index_col_name, data_col_name = self.data_col_name, path =self.monthly_path)
            
            # get weekly and monthly forecast from weka
            (weka_weekly , weka_monthly) = self.weka_manager.getWekaResults(weekly_arff_path =self.weekly_path , monthly_arff_path =self.monthly_path) 
            
            # combine results
            (final_weekly , final_montly) = self.forecast_facade.prepare_final_results(weka_weekly, weka_monthly, python_week, python_month)
            
            resutls = {"is_twitter": input_data["is_twitter"],
                       "region": input_data["region"],
                       "is_activity": input_data["is_activity"],
                       "type":input_data["type"],
                       "weekly_forecast":final_weekly, 
                       "monthly_forecast":final_montly}
            # Add forecasted values to output queue
            self.output_queue.put(resutls)
            
            # get next dataframe
            input_data = self.input_queue.get()
        
        # quiting from the process    
        print "Data Analysis Process %d with pid %d completed..." %((self.identifier+1) ,os.getpid() )
        
    def get_weekly_and_monthly_resampled(self , pd_df_object):
        wk_resampled_df = self.forecast_facade.get_weekly_resampled_data(pd_df_object, self.data_col_name)
        month_resampled_df = self.forecast_facade.get_monthly_resampled_data(pd_df_object, self.data_col_name)
        return (wk_resampled_df ,month_resampled_df)
    
    def run(self):
        print "Data Analysis Process %d with pid %d started..." %((self.identifier+1) , os.getpid() )
        self.analyze_data()