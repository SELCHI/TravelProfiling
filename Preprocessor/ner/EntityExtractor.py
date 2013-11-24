'''
Created on Oct 30, 2013

@author: lasitha
'''
from ListSearcher import ListSearcher

class EntityExtractor:
    
    def __init__(self):
        self.searcher = ListSearcher()

    
    def tagEntity(self,sentense):
        
        entitySet = {'region':None, 'place':None, 'activity':None, 'text':None}
        
        sentense = sentense.lower().strip()
        
        entitySet['region'] = self.searcher.getRegion(sentense)
#         entitySet['place'] = self.searcher.getPlace(sentense)
        entitySet['placeType'] = self.searcher.getPlaceType(sentense)
        entitySet['activity'] = self.searcher.getQualifierType(sentense)
        entitySet['text'] = ( sentense )
        
        
        if entitySet['region']!=None and entitySet['placeType']!=None:
            return entitySet
        if entitySet['region']!=None and entitySet['activity']!=None:
            return entitySet
        
        
        return None
        
           
        
