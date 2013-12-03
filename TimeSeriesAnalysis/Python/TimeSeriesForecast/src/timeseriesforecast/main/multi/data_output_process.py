'''
Created on Nov 24, 2013

@author: samith
'''
from multiprocessing import Process, Queue ,Value
import urllib2,os
from timeseriesforecast.main.config import FINALIZE_COUNT, UPDATE_URL,\
    FINALIZE_URL
import traceback
class DataOutputProcess(Process):
    '''
    classdocs
    '''


    def __init__(self , output_queue,identifier =0):
        '''
        Constructor
        '''
        super(DataOutputProcess, self).__init__()
        self.output_queue =  output_queue
        self.update_url =  UPDATE_URL
        self.finalize_url = FINALIZE_URL
        self.finalize_count = FINALIZE_COUNT
        self.headers = {'Accept': 'application/json; charset=utf8'}
        self.identifier = identifier 
        
    def send_data_to_ontolog(self):
        
        count = 0
        processed_data = self.output_queue.get()
        # Add your headers
        
        while(type(processed_data) is not str):
            try:
                self.print_final_results(processed_data)
                self.send_update_request(processed_data)
                count = count +1
                if self.finalize_count == count:
                    self.send_save_request()
                    count = 0
                
                processed_data = self.output_queue.get()
            except urllib2.HTTPError:
                var = traceback.format_exc()
                print var
                processed_data = self.output_queue.get()
                continue
            
        print "Data Output Process %d with pid %d completed..." %((self.identifier+1) ,os.getpid() )
                
    def send_update_request(self,processed_data):
        print "Send update signal"
        (is_twitter,region ,is_activity , type, monthly_forecast, weekly_forecast) = self.get_params(processed_data)
        update = self.update_url  %(is_twitter,region ,is_activity , type, monthly_forecast, weekly_forecast)
        self.send_request(update)
     
    def send_save_request(self):
        print "Send finalize signal..."
        self.send_request(self.finalize_url)
        
    def send_request(self, url):
        request = urllib2.Request(url, None, self.headers)
        # Getting the response
        response = urllib2.urlopen(request)
        # Print the headers
        print response.read()
        print "Successfully sent..."
        
    def print_final_results(self ,resutls):
        print "Results for Region: ' %s '  Activity/Place: ' %s '" %(resutls["region"] ,resutls["type"])
        print "Weekly Forecast %d" %(resutls["weekly_forecast"])
        print "Monthly Forecast %d" %(resutls["monthly_forecast"])
       
    def get_params(self , processed_data):
        is_twitter = processed_data["is_twitter"]
        region = str(processed_data["region"]).title().replace(" ", "_")
        is_activity = processed_data["is_activity"]
        type = str(processed_data["type"]).title().replace(" ", "_")
        monthly_forecast = processed_data["monthly_forecast"]
        weekly_forecast = processed_data["weekly_forecast"]
        
        return (is_twitter,region ,is_activity , type, monthly_forecast, weekly_forecast)
    
    def run(self):
        print "Data Output Process %d with pid %d started..." %((self.identifier+1) , os.getpid() )
        self.send_data_to_ontolog()
        

if __name__ == '__main__':
    ff = DataOutputProcess()
    ff.send_data_to_ontolog()