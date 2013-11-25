'''
Created on Oct 28, 2013

@author: lasitha
'''
import pymongo
from pymongo import MongoClient


class MongoReader:
    '''
    classdocs
    '''

    def __init__(self):
        self.client = MongoClient('localhost',27017)
        self.db = self.client.test_tweetbase
        self.tweetset = self.db.tweets
        self.mycursor = self.tweetset.find()
        self.length = self.mycursor.count()

        
    def getData(self):
        
        if self.length>0:
            toReturn = self.mycursor.next()
            self.length -=1
        else:
            return None
         
        return self.convert(toReturn)
    
    
    def convert(self,jsonData):
        
        if isinstance(jsonData, dict):
            return dict([(self.convert(key), self.convert(value)) for key, value in jsonData.iteritems()])
        elif isinstance(jsonData, list):
            return [self.convert(element) for element in jsonData]
        elif isinstance(jsonData, unicode):
            return jsonData.encode('utf-8')
        else:
            return jsonData
        
    
        