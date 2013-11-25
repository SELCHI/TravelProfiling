'''
Created on Nov 25, 2013

@author: samith
'''
import os
import timeseriesforecast.main.config as config
from multiprocessing import Process, Queue ,Value
from timeseriesforecast.main.config import NUMBER_OF_FETCH_PROCESSES ,NUMBER_OF_ANALYZE_PROCESSES ,NUMBER_OF_OUTPUT_PROCESSES,\
                 INPUT_QUEUE_LIMIT , OUTPUT_QUEUE_LIMIT,\
    INPUT_QUEUE_END_IDENTIFIER
from timeseriesforecast.main.multi.data_analyze_process import DataAnalyzeProcess
from timeseriesforecast.main.multi.data_fetch_process import DataFetchProcess
from timeseriesforecast.main.main_forecast import MainForecast

class MainProcess(Process):
    '''
    classdocs
    '''


    def __init__(self ,number_of_fetch_pro = NUMBER_OF_FETCH_PROCESSES , \
                 number_of_analyze_pro = NUMBER_OF_ANALYZE_PROCESSES ,\
                 number_of_output_pro = NUMBER_OF_OUTPUT_PROCESSES,\
                 input_queue_limit = INPUT_QUEUE_LIMIT, \
                 output_queue_limit = OUTPUT_QUEUE_LIMIT):
        '''
        Constructor
        '''
        super(MainProcess, self).__init__()
        self.number_of_fetch_pro = number_of_fetch_pro
        self.number_of_analyze_pro = number_of_analyze_pro
        self.number_of_output_pro = number_of_output_pro
        self.input_queue_limit = input_queue_limit
        self.output_queue_limit = output_queue_limit
        
        config.init_arff_locs(self.number_of_analyze_pro)
        self.input_queue = Queue(input_queue_limit) if input_queue_limit is not None else Queue()
        self.output_queue = Queue(output_queue_limit) if output_queue_limit is not None else Queue()
    
    def print_final_results(self ,resutls):
        print "Weekly Forecast %d" %(resutls["weekly"])
        print "Monthly Forecast %d" %(resutls["monthly"])
        
    def run(self):        
        print "Main Process with pid %d started..." %( int(os.getpid()) )
        
        fetch_processes = []
        for i in range(self.number_of_fetch_pro):
            f_process = DataFetchProcess(self.input_queue)
            fetch_processes.append(f_process)
            f_process.start()
            
        analyze_processes = []
        for i in range(self.number_of_analyze_pro):
            a_process = DataAnalyzeProcess(self.input_queue ,self.output_queue , i)
            analyze_processes.append(a_process)
            a_process.start()
        
        '''    
        output_processes = []
        for i in range(self.number_of_output_pro):
            o_process = DataOutputProcess(self.output_queue , i)
            DataOutputProcess.append(o_process)
            o_process.start()
        '''
            
        for f_process in fetch_processes:
            f_process.join()
        
        for i in range(self.number_of_analyze_pro):
            self.input_queue.put(str(INPUT_QUEUE_END_IDENTIFIER))
        
        for a_process in analyze_processes:
            a_process.join()  
        
        '''
        for o_process in output_processes:
            a_process.join() 
        '''   
            
        while(not self.output_queue.empty()):
            resutls = self.output_queue.get()
            self.print_final_results(resutls)
        
        print "Main process with pid %d completed..." %( int(os.getpid()) )

if __name__ == '__main__':
    process =    MainProcess()
    process.start()
    process.join()
    print "Done main process"
        