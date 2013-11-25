'''
Created on Oct 28, 2013

@author: lasitha
'''
from FilteredTweetWriter import FilteredTweetWriter
from MongoReader import MongoReader
from EntityExtractor import EntityExtractor
from pymongo import Connection
from sentimentanalysis.classifier.classifier import Classifier
from sentimentanalysis.classifier.config import POSITIVE, NEGATIVE, HARD_TO_CLASSIFY
   
reader = MongoReader()

entityExtractor = EntityExtractor()



while True:
   
    data = reader.getData()
    if data != None:
        tweetText = data['text']
    else:
        break
    
    enti = entityExtractor.tagEntity(tweetText)
    filters = FilteredTweetWriter('a', 'b', 'c', 'd')
    if (enti != None and enti['activity'] != None):
        enti['senti'] = True
        enti['time'] = data['time']
       
        classifier = Classifier()

        result = classifier.classify( enti['text'] )
        enti['senti'] = result
      #  filters = FilteredTweetWriter('a', 'b', 'c', 'd')
        filters.insertToActivityTweetbase( enti['region'], enti['activity'], enti['time'], enti['senti'] )
    if(enti != None and enti['placeType'] != None):   
       

        enti['time'] = data['time']
        classifier = Classifier()

        result = classifier.classify( enti['text'] )
        enti['senti'] = result
       # filters = FilteredTweetWriter('a', 'b', 'c', 'd')
        filters.insertToPlacesTweetbase( enti['region'], enti['placeType'], enti['time'], enti['senti'] )
        # print enti
  
print 'Iteration is over !'

    
   
    
    
    
            
        
    
    
    



    










    

    

