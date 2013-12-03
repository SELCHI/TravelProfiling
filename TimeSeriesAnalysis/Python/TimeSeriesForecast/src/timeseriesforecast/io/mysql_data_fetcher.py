'''
Created on Nov 18, 2013

@author: samith
'''
import pandas.io.sql as pd_sql
import MySQLdb
from docutils.parsers import null
import pandas as pd
class MysqlDataFetcher():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.set_connection_paras()
        
    def set_connection_paras(self,
                             host='localhost',port=8080,
                             user='root',passwd='123',
                             db='test_weka'):
        self.host = host
        self.port = port
        self.user = user
        self.passwd =passwd
        self.db = db
        
    def get_dataframe_from_db(self , table_name = 'department' , index_col_name = 'Date' , sql_query = None):
        con = MySQLdb.connect(host= self.host,
                      port= self.port,
                      user=self.user,
                      passwd=self.passwd,
                      db=self.db)
        
        if sql_query is None:
            sql_query = "select * from %s order by %s "  %( table_name , index_col_name)
        
        df = pd_sql.frame_query(sql_query, con ,index_col = index_col_name)
        con.close()    
        df.index = pd.to_datetime(df.index)
        return df
    
    def get_connection(self,
                       host='localhost',port=8080,
                       user='root',passwd='123',
                       db='test_weka'):
        con = MySQLdb.connect(host= self.host,
                      port= self.port,
                      user=self.user,
                      passwd=self.passwd,
                      db=self.db)
        

if __name__ == "__main__":
    db = MysqlDataFetcher()
    df = db.get_dataframe_from_db()
    print df.head()
        