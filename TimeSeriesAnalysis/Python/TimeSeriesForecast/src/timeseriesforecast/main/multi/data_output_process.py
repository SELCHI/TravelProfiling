'''
Created on Nov 24, 2013

@author: samith
'''
from multiprocessing import Process, Queue ,Value
import urllib2
class DataOutputProcess(Process):
    '''
    classdocs
    '''


    def __init__(self , output_queue = None):
        '''
        Constructor
        '''
        self.output_queue =  output_queue
        self.update_url =  'http://10.8.108.163/TravelDataWebService/rest/updatetrends/%s/%s/%s/%s/%d/%d'
        self.finalize_url = 'http://10.8.108.163/TravelDataWebService/rest/updatetrends/finalize'
        
    def send_data_to_ontolog(self):
        
        
        # Add your headers
        headers = {'Accept': 'application/json; charset=utf8'}
        
        update = self.update_url  %(is_twitter,region ,is_activity , type, monthly_forecast, weekly_forecast)
        
        # Create the Request. 
        request = urllib2.Request(self.finalize_url, None, headers)
        
        # Getting the response
        response = urllib2.urlopen(request)
        
        # Print the headers
        print response.read()

if __name__ == '__main__':
    ff = DataOutputProcess()
    ff.send_data_to_ontolog()