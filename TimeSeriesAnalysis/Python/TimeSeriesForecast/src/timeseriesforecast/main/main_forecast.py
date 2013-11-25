'''
Created on Nov 19, 2013

@author: samith
'''
from timeseriesforecast.forecast.forecast_facade import ForecastFacade
from timeseriesforecast.io.mysql_data_fetcher import MysqlDataFetcher
from timeseriesforecast.io.arff_handler import ArffHandler
from timeseriesforecast.io.weka_manager import WekaManager
from timeseriesforecast.main.config import INDEX_COL ,DATA_COL ,WEEKLY_ARFF_PATH ,MONTHLY_ARFF_PATH
class MainForecast():
    '''
    classdocs
    '''


    def __init__(self ):
        '''
        Constructor
        '''
        self.forecast_facade = ForecastFacade()
        self.data_fetcher = MysqlDataFetcher()
        self.arff_manager = ArffHandler()
        self.weka_manager = WekaManager()
     
    def get_next_forecast(self):
        print "Forecast Started..."
        pd_df_object = self.data_fetcher.get_dataframe_from_db(table_name = 'department'  , index_col_name = INDEX_COL)
        (python_week , python_month,wk_resampled_df , month_resampled_df) = self.forecast_facade.get_next_wk_month_forecast(pd_df_object, data_col_name = DATA_COL )
        print "Forecasting using python completed..."
        print "Send data to weka..."
        self.arff_manager.saveAsArff(wk_resampled_df, index_col = INDEX_COL, data_col_name = DATA_COL,path=  WEEKLY_ARFF_PATH)
        self.arff_manager.saveAsArff(month_resampled_df, index_col = INDEX_COL, data_col_name = DATA_COL, path =MONTHLY_ARFF_PATH)
        print "Forecasting using weka..."
        (weka_weekly , weka_monthly) = self.weka_manager.getWekaResults()
        self.print_results(python_week , python_month , weka_weekly , weka_monthly)
        print "Forecast Successfully completed..."       
        return (python_week , python_month , weka_weekly , weka_monthly)
    
    def print_results(self ,python_week , python_month , weka_weekly , weka_monthly):
        print "Python weekly %s" %(python_week)
        print "Weka weekly %s" %(weka_weekly)
        print "Python monthly %s "%(python_month)
        print "Weka monthly %s" %(weka_monthly)
        
    def print_final_results(self ,resutls):
        print "Weekly Forecast %d" %(resutls["weekly"])
        print "Monthly Forecast %d" %(resutls["monthly"])
        

if __name__ == "__main__":  
    main = MainForecast()
    (python_week , python_month , weka_weekly , weka_monthly) = main.get_next_forecast()
            
    

    