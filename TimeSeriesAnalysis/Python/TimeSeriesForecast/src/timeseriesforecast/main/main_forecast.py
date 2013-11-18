'''
Created on Nov 19, 2013

@author: samith
'''
from timeseriesforecast.forecast.forecast_facade import ForecastFacade
from timeseriesforecast.io.mysql_data_fetcher import MysqlDataFetcher
class MainForecast():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.forecast_facade = ForecastFacade()
        self.data_fetcher = MysqlDataFetcher()
     
    def get_next_forecast(self):
        pd_df_object = self.data_fetcher.get_dataframe_from_db(table_name = 'department'  , index_col_name = 'Date')
        (wk_final , month_results) = self.forecast_facade.get_next_wk_month_forecast(pd_df_object, data_col_name = 'Departments' )       
        return (wk_final , month_results)

if __name__ == "__main__":   
    main = MainForecast()
    (wk_final , month_results) = main.get_next_forecast()
    print "weekly %s" %(wk_final)
    print "monthly %s "%(month_results)
    
            
    

    