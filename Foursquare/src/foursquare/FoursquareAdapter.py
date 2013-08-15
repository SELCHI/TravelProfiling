'''
Created on Aug 14, 2013

@author: samith
'''
import foursquare
from dbfacade import DBFacade
from pprint import pprint
class Foursquare_Adapter:
    '''
    Adapter for foursquare
    '''


    def __init__(self,client_id, client_secret,dbFacade):
        '''
        Constructor
        '''
        self.dbFacade = dbFacade
        self.client_id = client_id
        self.client_secret = client_secret
        self.client = foursquare.Foursquare(client_id=self.client_id, client_secret=self.client_secret)
        
    def saveSeedWords(self,filename ,dbName="test_foursquare"):
        seedWords = []
        try:
            with open(filename ,"r") as file:
                [seedWords.append({ "word" :each_line.split( "\n" )[0]}) for each_line in file]
        except IOError as err:
            print("Error in File"+str(err))
        
        self.dbFacade.insertData(dbName =dbName , collectionData = seedWords , collectionName = "seed_words" )
        
    def getTips(self ,dbName="test_foursquare"):        
        
        cityLocArray = self.dbFacade.getData(dbName = dbName ,collectionName = "foursquare_locations" , 
                              projection = { "locations.location_id": True ,
                                            "geo_properties.name": True ,
                                          "locations.location_name": True ,"_id":False })
        
        for locArray in cityLocArray:
            for location in locArray['locations']:                
                result = self.client.venues.tips(VENUE_ID =location["location_id"])
                tipsArray = result['tips']['items']
                tipsDetails = []
                print "**************"
                print location["location_name"]
                
                for eachTip in tipsArray:
                    add = {
                           'createdAt':eachTip['createdAt'],
                           'id':eachTip['id'],
                           'likes':eachTip['likes']['count'],
                           'text':eachTip['text']
                           }
                    if add not in tipsDetails:
                        tipsDetails.append(add)
                pprint(tipsDetails)
                    
                if tipsDetails:
                    self.dbFacade.updateData(dbName =dbName,
                                             collectionName ="location_tips" ,
                                             query = {"location_id":location["location_id"]} ,
                                             update = {"location_id":location["location_id"] , 
                                                       "location_name":location["location_name"] , 
                                                       "tips":tipsDetails})
        
        
    def getVenues(self ,dbName="test_foursquare"):
        seedArray = self.dbFacade.getData(dbName = dbName ,collectionName = "seed_words" , 
                              projection = { "word": True ,"_id":False })
        
        cityArray = self.dbFacade.getData(dbName = dbName ,collectionName = "geo_names" , 
                              projection = { "geonameid": True ,"name": True ,"country code":True,"_id":False })

        for city in cityArray:
            location_info = []
            
            # find travel related locations inside the city
            for seedWord in seedArray:
                print seedWord["word"]
                print city["name"]+", "+city["country code"]
                result = self.client.venues.search(params={'query':seedWord["word"] ,
                                                           'near':city["name"]+", "+city["country code"]})            
                venues = result["venues"]
                for each_venue in venues:
                    add = {
                           "location_id"          :each_venue["id"],
                           "location_details"    :each_venue["location"],
                           "location_name"        :each_venue["name"]
                           }
                    if add not in location_info:
                        location_info.append(add)
                    

            if location_info:
                self.dbFacade.updateData(dbName =dbName,
                                         collectionName ="foursquare_locations" ,
                                         query = {"geo_properties":city} ,
                                         update = {"geo_properties":city , "locations":location_info})
        