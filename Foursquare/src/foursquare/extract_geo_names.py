'''
Created on Jul 3, 2013

@author: samith
'''
import os
from dbfacade import DBFacade
class GeoDataHandler:

    def __init__(self,dbFacade):
        self.dbFacade = dbFacade
        
    def getGeoData(self ,filename):
        geo_data_list = []    
        try:
            with open(filename ,"r") as file:
                for each_line in file:
                    fields= each_line.split( "\t" )
                    geo_data_list.append({
                                          "geonameid"   :   fields[0],
                                          "name"        :   fields[1],
                                          "latitude"    :   fields[4],
                                          "longitude"   :   fields[5],
                                          "country code":   fields[8]
                                          })
                return geo_data_list
        except IOError as err:
            print("Error in File"+str(err))
            return(None)
    
    def saveData(self ,geoDataList ,collectionName = "geo_names" ,dbName ="test_foursquare"):
        self.dbFacade.insertData(geoDataList , collectionName = "geo_names" ,dbName =dbName)
 
"""       
db = DBFacade()
gd = GeoDataHandler(dbFacade = db)
#gd.saveData()
data = db.getData(collectionName = "geo_names" ,dbName ="test_foursquare")#,projection = { "name": True})
for d in data:
    print d
    
"""