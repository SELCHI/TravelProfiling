from extract_geo_names import GeoDataHandler
from dbfacade import DBFacade
from FoursquareAdapter import Foursquare_Adapter
import argparse

parser = argparse.ArgumentParser(description='This script save geo locations and seed words database.')
parser.add_argument('-host','--host', metavar = 'host_name' ,help='host name',required=True)
parser.add_argument('-port','--port',metavar = 'port_id' , help='port id', required=False)
parser.add_argument('-db','--db',metavar = 'foursquare_db' ,help='db name', required=False)
parser.add_argument('--test',nargs='?' ,const=True ,default=False,help='for testing add this arg', required=False)
args = parser.parse_args()

host = args.host
port=27017
foursquareDb = "foursquare"

if args.port:
    port = args.port
if args.test:
    foursquareDb = "test_foursquare"
if args.db:
    foursquareDb = args.db

db = DBFacade(host= host, port= port)
geoHandler = GeoDataHandler(dbFacade = db)

cl_id = "HTPND1WMLPA35G4MEBGULBG4TZJAMHUTSZ041K0J22FNP5FM"
cl_sec = "FENV3PMONDEZILINO1VEAHTWYXDRI2O1WQH1SS3BBA1AI531"
fourSquare = Foursquare_Adapter(dbFacade = db, client_id=cl_id, client_secret=cl_sec)
fourSquare.retrieveVenues(dbName = foursquareDb)