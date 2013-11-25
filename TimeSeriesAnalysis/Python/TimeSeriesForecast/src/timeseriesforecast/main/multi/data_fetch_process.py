'''
Created on Nov 24, 2013

@author: samith
'''
from multiprocessing import Process, Queue ,Value
import os, time
import timeseriesforecast.main.config as config
from timeseriesforecast.main.multi.data_analyze_process import DataAnalyzeProcess
from timeseriesforecast.io.mysql_data_fetcher import MysqlDataFetcher
from timeseriesforecast.main.config import INPUT_QUEUE_END_IDENTIFIER ,INDEX_COL
from timeseriesforecast.main.main_forecast import MainForecast
class DataFetchProcess(Process):
    '''
    classdocs
    '''


    def __init__(self , input_queue ,identifier =0):
        '''
        Constructor
        '''
        super(DataFetchProcess, self).__init__()
        self.input_queue = input_queue
        self.identifier = identifier
        self.data_fetcher = MysqlDataFetcher()
            
    def fetch_data_from_db(self):
        for i in range(3):
            pd_df_object = self.data_fetcher.get_dataframe_from_db(table_name = 'department'  , index_col_name = INDEX_COL)
            self.input_queue.put(pd_df_object)
        print "Data Fetch Process %d with pid %d completed..." %((self.identifier+1) ,os.getpid() )
            
    def run(self):        
        print "Data Fetch Process %d with pid %d started..." %((self.identifier+1) ,os.getpid() )
        self.fetch_data_from_db()
        

if __name__ == '__main__':
    number_of_process = 2
    config.init_arff_locs(number_of_process)
    input_queue = Queue(5)
    output_queue = Queue()
    jobs = []
    for i in range(number_of_process):
        p = DataAnalyzeProcess(input_queue, output_queue,i)
        jobs.append(p)
        p.start()
    
    
               
        
    
    fetch = DataFetchProcess(input_queue)
    fetch.start()
    fetch.join()
    for i in range(number_of_process):
        input_queue.put(str(INPUT_QUEUE_END_IDENTIFIER))
        
    for j in jobs:
        j.join()
    
    main = MainForecast()
    while(not output_queue.empty()):
        resutls = output_queue.get()
        main.print_final_results(resutls)
    '''
    directory = "/home/samith/Desktop/%s/%s/gg.txt" % ("blaa","ff")
    print directory
    ForecastFacade()
    #print os.getcwd()
    if not os.path.exists(directory):
            os.makedirs(directory)
            '''
'''
    q = Queue(5)
    jobs = []
    
    for i in range(5):
        p = DataAnalyzeProcess(q)
        jobs.append(p)
        p.start()
    fetch = DataFetchProcess(q)
    fetch.start()
    fetch.join()
    for j in jobs:
        j.join()
    
    while(not q.empty()):
        val = q.get()
        print val
    '''
        
        