'''
Created on Nov 24, 2013

@author: samith
'''
from multiprocessing import Process, Queue ,Value
import os, time
import timeseriesforecast.main.config as config
from timeseriesforecast.main.multi.data_analyze_process import DataAnalyzeProcess
from timeseriesforecast.io.mysql_data_fetcher import MysqlDataFetcher
from timeseriesforecast.main.config import INPUT_QUEUE_END_IDENTIFIER ,INDEX_COL,\
    MYSQL_HOST_IP, MYSQL_PORT, MYSQL_USER, MYSQL_PW, MYSQL_DB_PLACES,\
    MYSQL_DB_ACTIVITY, DATA_COL
from timeseriesforecast.main.main_forecast import MainForecast
import MySQLdb
import pandas.io.sql as pd_sql

class DataFetchProcess(Process):
    '''
    classdocs
    '''


    def __init__(self , input_queue ,identifier =0,db_name = None,  host = MYSQL_HOST_IP , port = MYSQL_PORT , user = MYSQL_USER , passwd = MYSQL_PW ):
        '''
        Constructor
        '''
        super(DataFetchProcess, self).__init__()
        self.input_queue = input_queue
        self.identifier = identifier
        self.data_fetcher = MysqlDataFetcher()
        self.host = host
        self.port = port
        self.user = user
        self.passwd = passwd
        self.db_name = db_name
        
    def init_db_connection( self ):
        self.tables_list = list()
        self.final_list = list()
        self.results_list = list()
        
        if self.db_name == MYSQL_DB_PLACES:
            self.where_type = "Place_Type"
            self.is_activity = "false"
        if self.db_name == MYSQL_DB_ACTIVITY:
            self.where_type = "Activity_Type"
            self.is_activity = "true"
            
        self.db = MySQLdb.connect( host = self.host, port = self.port , user = self.user, passwd = self.passwd, db = self.db_name )
        self.cursor = self.db.cursor()
        print "Data Fetch Process %d with pid %d start fetching table info..." % ((self.identifier+1) ,os.getpid()) 
        self.cursor.execute( "SELECT table_name FROM information_schema.tables   WHERE table_type = 'BASE TABLE' AND table_schema = '%s' AND `TABLE_ROWS` >0  ORDER BY table_name ASC" % str( self.db_name ) )
        print "Data Fetch Process %d with pid %d done fetching table info..." % ((self.identifier+1) ,os.getpid()) 
        #=======================================================================
        # self.cursor.execute( "SELECT table_name FROM information_schema.tables     WHERE table_type = 'BASE TABLE' AND table_schema = 'PlacesDB' AND `TABLE_ROWS` >0 \
        #                  ORDER BY table_name ASC" )
        #=======================================================================
        result = self.cursor.fetchall()
        for t in result:
            self.tables_list.append( ( t[0] ) )
            
    def fetch_data_from_db(self):
        for i in range(6):
            pd_df_object = self.data_fetcher.get_dataframe_from_db(table_name = 'department'  , index_col_name = INDEX_COL)      
            input_data = {"pd_df_object": pd_df_object,
                          "is_twitter": "true",
                          "region": "region",
                          "is_activity": "true",
                          "type":"type"
                          }
            self.input_queue.put(input_data)
        print "Data Fetch Process %d with pid %d completed..." %((self.identifier+1) ,os.getpid() )
        
    def get_activity_or_place_entries( self ):
        print "Number of tables in database ' %s ': %d" %( self.db_name ,( len( self.tables_list ) ))
        for index in range( len( self.tables_list ) ):                   
            self.cursor.execute( "select %s  from %s group by  %s " \
                                 % (self.where_type ,self.tables_list[index],self.where_type ))
            results_group_by_type = self.cursor.fetchall()
            k = 0
            del self.results_list[0:len( self.results_list )]
            for t in results_group_by_type:
                self.results_list.insert( k, t )
                k = k + 1

            for results in self.results_list:
                sql = ( "select "+INDEX_COL+" , "+DATA_COL+" from " + self.tables_list[index] + \
                        "  where  "+self.where_type+"  = '" + str( results[0] ) + "'" )  # % (tables_list[index], str(results[0])))
                df = pd_sql.frame_query( sql, self.db , index_col = INDEX_COL )
                input_data  = { 'is_twitter' : "true",
                                'region' : self.tables_list[index],
                                'type': results[0],
                                'is_activity' :self.is_activity,
                                'pd_df_object' : df}
                self.input_queue.put(input_data)
        self.db.close()
        print "Data Fetch Process %d with pid %d completed..." %((self.identifier+1) ,os.getpid() )
            
    def run(self):        
        print "Data Fetch Process %d with pid %d started..." %((self.identifier+1) ,os.getpid() )
        self.init_db_connection()
        self.get_activity_or_place_entries()
        
        #self.fetch_data_from_db()
        

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
        
        