'''
Created on Jun 28, 2013

@author: samith
'''
import foursquare
from pprint import pprint
cl_id = "HTPND1WMLPA35G4MEBGULBG4TZJAMHUTSZ041K0J22FNP5FM"
cl_sec = "FENV3PMONDEZILINO1VEAHTWYXDRI2O1WQH1SS3BBA1AI531"
client = foursquare.Foursquare(client_id=cl_id, client_secret=cl_sec)

result = client.venues.search(params={'query':'mountain surf' ,'near':'Hikkaduwa'})
venues = result["venues"]
location_info = []
for each_venue in venues:
    location_info.append({
                          "id"          :each_venue["id"],
                          "location"    :each_venue["location"],
                          "name"        :each_venue["name"]
                          })
pprint((location_info))