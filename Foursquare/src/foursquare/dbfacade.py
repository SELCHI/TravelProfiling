'''
Created on Aug 14, 2013

@author: samith
'''
from pymongo import MongoClient
from pymongo.errors import ConnectionFailure
import sys
import datetime
import pprint
class DBFacade:
    '''
    Db Handler
    '''

    def __init__(self,host="samith", port=27017):
        self.createConnection(host ,port)
    
    def createConnection(self ,host="samith", port=27017 ):
        """ Connect to MongoDB """
        try:
            self.connection = MongoClient(host ,port)
            print "Connected successfully"
        except ConnectionFailure, e:
            sys.stderr.write("Could not connect to MongoDB: %s" % e)
    
    def setDB(self,dbName):
        return self.connection[dbName]
    
    def setCollection(self,dbHandler ,collectionName):
        return dbHandler[collectionName]
    
    def insertData(self,collectionData,collectionName ,dbName = "foursquare"):
        dbHandler = self.setDB(dbName)
        collectionHandler = self.setCollection(dbHandler, collectionName)
        collectionHandler.insert(collectionData , safe=True)
    
    def updateData(self,collectionName ,query ,update ,dbName = "foursquare"):
        dbHandler = self.setDB(dbName)
        collectionHandler = self.setCollection(dbHandler, collectionName)
        collectionHandler.update(query ,update , upsert=True)
            
    def countCollection(self,collectionName ,dbName = "foursquare"):
        dbHandler = self.setDB(dbName)
        collectionHandler = self.setCollection(dbHandler, collectionName)
        return collectionHandler.count()
        
    def getData(self,collectionName ,dbName = "foursquare" , criteria ={} , projection ={}):
        dbHandler = self.setDB(dbName)
        collectionHandler = self.setCollection(dbHandler, collectionName)
        if projection:
            data =  collectionHandler.find( criteria,projection )
        else:
            data =  collectionHandler.find( criteria )
        return data
    
    def dropDB(self, dbName):
        self.connection.drop_database(dbName)
 
 
        