'''
Created on Nov 26, 2013

@author: lasitha
'''
import MySQLdb

class MySQLHandler:
    
    
    def __init__(self,server,username,password,database):
        
        self.db = MySQLdb.connect(server, username, password, database,charset='utf8')
        self.cursor = self.db.cursor()    
    
    def getRows(self,query,region,itemType):
        
        try:           
            self.cursor.execute(query,(region,itemType)) 
            rows = self.cursor.fetchall()
            
        except:
            rows = 'exception'
       
        return rows
    
    def getData(self,query):
        
        try:                    
            self.cursor.execute(query) 
            rows = self.cursor.fetchall()
            
        except:
            rows = 'exception'

        return rows
    
if __name__ == '__main__':    
    
    
    mysql = MySQLHandler('localhost','root','LetMeIn','FourSquareData')  
    #data = mysql.getRows("SELECT * FROM  `fsqsearch` WHERE onto_tag =%s and region=%s ORDER BY checkins_count DESC",'Beach','Jakarta')
    data = mysql.getData("SELECT * FROM `fsqsearch` ORDER BY checkins_count DESC LIMIT 7") 
    for row in data:
        print row
        