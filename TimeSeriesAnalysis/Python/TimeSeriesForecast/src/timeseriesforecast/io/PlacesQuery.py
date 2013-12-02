'''
Created on Nov 21, 2013

@author: chiran
'''
import MySQLdb

import pandas.io.sql as pd_sql


class PlaceQuery:

    def __init__(self, ip_address = "10.0.0.2"): 

        self.TablesList = list()
        self.FinalList = list()
        self.PlacesList = list()
        print "FF"
        self.db = MySQLdb.connect( host="192.248.8.251", port = 3306, user ="root", passwd ="selchi123", db="PlacesDB" )
        print "GG"
        self.cursor = self.db.cursor()
        self.cursor.execute( "SELECT table_name FROM information_schema.tables     WHERE table_type = 'BASE TABLE' AND table_schema='PlacesDB'AND `TABLE_ROWS` >0 \
                         ORDER BY table_name ASC" )
        print "GGG"
        result = self.cursor.fetchall()
        for t in result:
            print t
            self.TablesList.append( ( t[0] ) )


    def getPlacesEntries( self ):
        print( len( self.TablesList ) )
        for index in range( len( self.TablesList ) ):

            self.cursor.execute( "select Place_Type from %s group by Place_Type" % self.TablesList[index] )

            result_places = self.cursor.fetchall()

            k = 0
            del self.PlacesList[0:len( self.PlacesList )]
            for t in result_places:
                self.PlacesList.insert( k, t )
                k = k + 1

            for a in self.PlacesList:


                sql = ( "select * from " + self.TablesList[index] + "  where Place_Type = '" + str( a[0] ) + "'" )  # % (TablesList[index], str(a[0])))

                df = pd_sql.frame_query( sql, self.db , index_col = "Date" )


                orderedList = {'region':None, 'place':None , 'Data':None}
                orderedList['region'] = self.TablesList[index]
                orderedList['place'] = a[0]
                orderedList['Data'] = df
                print orderedList['Data']
                self.FinalList.append( orderedList )


        return self.FinalList


