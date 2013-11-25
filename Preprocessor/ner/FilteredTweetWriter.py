'''
Created on Nov 3, 2013

@author: lasitha
'''

import MySQLdb


class FilteredTweetWriter:
    
    def __init__(self, severname, dbname, user, password):
        self.servername = severname
        self.dbname = dbname
        self.user = user
        self.password = password
       # self.db = MySQLdb.connect(self.servername, self.user, self.password, self.dbname)
        
        
    def insertToActivityTweetbase( self, region, activity, time, senti ):

        
        
        
        db = MySQLdb.connect( "localhost", "selchiuser", "selchi123", "ActivityDB" )
        cursor = db.cursor()
        Temp = str(region).title().replace(" ", "_")
        
        try:
            cursor.execute("CREATE TABLE IF NOT EXISTS %s (\
                  `Activity_Type` varchar(255) NOT NULL DEFAULT '',\
                  `Date` date NOT NULL DEFAULT '0000-00-00',\
                  `frequency_twitter` float DEFAULT NULL,\
                  `frequency_foursquare` float DEFAULT NULL,\
                   PRIMARY KEY (`Activity_Type`,`Date`)\
                   )" % \
                   (Temp))
            cursor.execute( "INSERT INTO %s (Activity_Type,  Date,frequency_twitter) VALUES ('%s', '%s',%s) ON DUPLICATE KEY UPDATE  frequency_twitter =  frequency_twitter +%s" % \
                   ( Temp, activity, time, senti, senti ) )
    
            db.commit()
            print ('commit-Activity in region ' + Temp)
        except:
    
            print ('roll back -Activity' + str(region))   
            db.rollback()

        db.close()
        
     
        
        
        
    def insertToPlacesTweetbase( self, region, place, time , senti ):
       
        
        
        db = MySQLdb.connect( "localhost", "selchiuser1", "selchi456", "PlacesDB" )
        cursor = db.cursor()
        Temp = str(region).title().replace(" ", "_")
        try:
            cursor.execute( "CREATE TABLE IF NOT EXISTS %s (\
                  `Place_Type` varchar(255) NOT NULL DEFAULT '',\
                  `Date` date NOT NULL DEFAULT '0000-00-00',\
                  `frequency_twitter` float DEFAULT NULL,\
                  `frequency_foursquare` float DEFAULT NULL,\
                   PRIMARY KEY (`Place_Type`,`Date`)\
                   )" % \
                   ( Temp ) )
            cursor.execute( "INSERT INTO %s (Place_Type,  Date,frequency_twitter) VALUES ('%s', '%s',%s) ON DUPLICATE KEY UPDATE  frequency_twitter =  frequency_twitter +%s" % \
                   ( Temp, place, time, senti, senti ) )

            db.commit()
            print ( 'commit-Place in region ' + Temp )
        except:

            print ( 'roll back -Place' + str( region ) )
            db.rollback()

        db.close()
        
        
    
