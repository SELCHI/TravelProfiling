'''
Created on Nov 24, 2013

@author: lasitha
'''
from flask import Flask
from flask.ext import restful
from MySQLHandler import MySQLHandler
import unicodedata

app = Flask(__name__)
api = restful.Api(app)

class getItemsInsideRegion(restful.Resource):
    def get(self,region,itemtype):
        mysql = MySQLHandler('localhost','root','selchi123','FourSquareData')  
        data = mysql.getRows("SELECT * FROM  `fsqsearch` WHERE  region=%s and onto_tag=%s ORDER BY checkins_count DESC",region.replace('_',' '),itemtype) 
        toReturn = {}
        if(data == 'exception'):
            return toReturn
        
        totcount = 0
        itemsNeed = 5
        topFive = list()
        
        for row in data:
            
        
            if(itemsNeed>0):
                topFive.append({str(row[4]):str(row[10])})
            else:
                totcount+= row[10]
                       
            itemsNeed -=1
    
        toReturn['topfive'] = topFive
        toReturn['others'] = totcount
        return toReturn

class getTopSeven(restful.Resource):
    
    def get(self):    
  
        mysql = MySQLHandler('localhost','root','selchi123','FourSquareData') 
        data = mysql.getData("SELECT * FROM `fsqsearch` ORDER BY checkins_count DESC LIMIT 7") 
        count = 0 
        mydd =list()
        if(data == 'exception'):
            return mydd
         
         
        for row in data:
            name = unicodedata.normalize('NFKD', row[4]).encode('ascii','ignore')    
            mydd.append({'name':name,'onto_tag':row[5],'checkins':row[10]})

        return mydd
    
api.add_resource(getItemsInsideRegion, '/insideitems/<region>/<itemtype>')
api.add_resource(getTopSeven, '/gettop')


if __name__ == '__main__':
    app.run(debug=True)
    
    