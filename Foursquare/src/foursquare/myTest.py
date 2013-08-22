'''
Created on Aug 14, 2013

@author: samith
'''
from extract_geo_names import GeoDataHandler
from dbfacade import DBFacade
from FoursquareAdapter import Foursquare_Adapter


db = DBFacade()
geoHandler = GeoDataHandler(dbFacade = db)


cl_id = "HTPND1WMLPA35G4MEBGULBG4TZJAMHUTSZ041K0J22FNP5FM"
cl_sec = "FENV3PMONDEZILINO1VEAHTWYXDRI2O1WQH1SS3BBA1AI531"
fourSquare = Foursquare_Adapter(dbFacade = db, client_id=cl_id, client_secret=cl_sec)
foursquareDb = "my_test_one"


db.dropDB(foursquareDb)
geoData = geoHandler.getGeoData("../../Resources/Geo_Names/rr.txt")
geoHandler.saveData(geoDataList = geoData, dbName =foursquareDb )
fourSquare.saveSeedWords(filename = "../../Resources/Words/temp_seed_words.txt" ,dbName = foursquareDb)
fourSquare.retrieveVenues(dbName = foursquareDb)

fourSquare.retrieveTips(dbName = foursquareDb)
"""
data = db.getData(dbName = foursquareDb ,collectionName = "foursquare_locations" , 
                              projection = { "locations.location_id": True ,
                                            "geo_properties.name": True ,
                                          "locations.location_name": True ,"_id":False })

for d in data:
    print d
    
    """