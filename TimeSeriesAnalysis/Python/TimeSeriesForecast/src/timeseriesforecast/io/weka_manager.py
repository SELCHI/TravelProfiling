'''
Created on Nov 20, 2013

@author: samith
'''
from py4j.java_gateway import JavaGateway
class WekaManager(object):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.gateway = JavaGateway()
        self.weka_forecaster =  self.gateway.entry_point.getWekaTSForecaster()
        
    def getWekaResults(self):
        weka_weekly = self.weka_forecaster.getNextWeeklyForecast()
        weka_monthly = self.weka_forecaster.getNextMonthlyForecast()
        return (weka_weekly , weka_monthly)
        
if __name__ == "__main__":   
    main = WekaManager()
    main.getWekaResults()
    