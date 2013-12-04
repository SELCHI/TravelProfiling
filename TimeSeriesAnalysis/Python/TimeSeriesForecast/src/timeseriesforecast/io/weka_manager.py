'''
Created on Nov 20, 2013

@author: samith
'''
from py4j.java_gateway import JavaGateway, GatewayClient
from timeseriesforecast.main.config import GATEWAY_SERVER_PORT
class WekaManager(object):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.gateway = JavaGateway(GatewayClient(port=GATEWAY_SERVER_PORT))
        self.weka_forecaster =  self.gateway.entry_point.getWekaTSForecaster()
        
    def getWekaResults(self ,weekly_arff_path =None , monthly_arff_path =None):
        if weekly_arff_path is None:
            weka_weekly = self.weka_forecaster.getNextWeeklyForecast()
        else:
            weka_weekly = self.weka_forecaster.getNextWeeklyForecast(weekly_arff_path)
        if monthly_arff_path is None:
            weka_monthly = self.weka_forecaster.getNextMonthlyForecast()
        else:
            weka_monthly = self.weka_forecaster.getNextMonthlyForecast(monthly_arff_path)
        return (weka_weekly , weka_monthly)
        
if __name__ == "__main__":   
    main = WekaManager()
    main.getWekaResults()
    