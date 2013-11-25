'''
Created on Nov 21, 2013

@author: chiran
'''
import MySQLdb

import pandas.io.sql as pd_sql



class ActivityQuery:

    def __init__( self ):

        self.TablesList = list()
        self.FinalList = list()
        self.ActivityList = list()
        self.db = MySQLdb.connect( host = "localhost", port = 3306, user = "selchiuser", passwd = "selchi123", db = "ActivityDB" )
        self.cursor = self.db.cursor()
        self.cursor.execute( "SELECT table_name FROM information_schema.tables     WHERE table_type = 'BASE TABLE' AND table_schema='ActivityDB'AND `TABLE_ROWS` >0 \
                         ORDER BY table_name ASC" )
        result = self.cursor.fetchall()
        for t in result:
            self.TablesList.append( ( t[0] ) )


    def getActivityEntries( self ):
        print( len( self.TablesList ) )
        for index in range( len( self.TablesList ) ):

            self.cursor.execute( "select Activity_Type from %s group by Activity_Type" % self.TablesList[index] )

            result_activity = self.cursor.fetchall()

            k = 0
            del self.ActivityList[0:len( self.ActivityList )]
            for t in result_activity:
                self.ActivityList.insert( k, t )
                k = k + 1

            for a in self.ActivityList:


                sql = ( "select * from " + self.TablesList[index] + "  where Activity_Type = '" + str( a[0] ) + "'" )  # % (TablesList[index], str(a[0])))

                df = pd_sql.frame_query( sql, self.db , index_col = "Date" )


                orderedList = {'region':None, 'activity':None , 'Data':None}
                orderedList['region'] = self.TablesList[index]
                orderedList['activity'] = a[0]
                orderedList['Data'] = df
                self.FinalList.append( orderedList )


        return self.FinalList



if __name__ == '__main__':

    query = ActivityQuery()
    print query.getActivityEntries()

