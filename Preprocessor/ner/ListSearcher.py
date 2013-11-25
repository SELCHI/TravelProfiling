'''
Created on Oct 29, 2013

@author: lasitha
'''

import nltk
from difflib import SequenceMatcher
import Queue
import thread



class ListSearcher:
  
    
    def __init__(self):
        # Create lists place types, qualifier types, places, regions
        regionFile = open('Regions.txt', 'r')
        self.regionList = list()
        
        for regioin1 in regionFile:
            self.regionList.append(regioin1.strip().replace('_', ' ').lower())
            
        
        regionFile.close()
        
        placetypesFile = open('PlaceTypes.txt', 'r')
        self.placetypeList = list()
        
        for place1 in placetypesFile:
            self.placetypeList.append(place1.strip().replace('_', ' ').lower())
        
        placetypesFile.close()
        
        qualifiertypesFile = open('QualifierTypes.txt', 'r')
        self.qualifiertypeList = list()
        
        for qualifier in qualifiertypesFile:
            self.qualifiertypeList.append(qualifier.strip().replace('_', ' ').lower())
        
        qualifiertypesFile.close()
        
        placesFile = open('Places.txt', 'r')
        self.placesList = list()
        
        for place in placesFile:
            self.placesList.append(place.strip().replace('_', ' ').lower())
            
        placesFile.close()

        
        
    def getQualifierType(self, sentense):
        
        return self.searchMachineRecursive(self.qualifiertypeList, sentense)
    
       
    def getPlaceType(self, sentense):
        return self.searchMachineRecursive(self.placetypeList, sentense)
    
    
    def getRegion(self, sentense):
        
#         step = len(self.regionList)/6
#
#         set1 = self.regionList[0:step-1]
#         set2 = self.regionList[step:2*step-1]
#         set3 = self.regionList[2*step:3*step-1]
#         set4 = self.regionList[3*step:len(self.regionList)-1]
#         results = Queue.Queue()
#         thread.start_new_thread(self.searchMachineOneTime,(set1,sentense,results))
#         thread.start_new_thread(self.searchMachineOneTime,(set2,sentense,results))
#         thread.start_new_thread(self.searchMachineOneTime,(set3,sentense,results))
#         thread.start_new_thread(self.searchMachineOneTime,(set4,sentense,results))
#         
#         result1 = results.get()
#         print result1
#         result2 = results.get()
#         print result2
#         result3 = results.get()
#         print result3
#         result4 = results.get()
#         print result4
           
        return self.searchSimple(self.regionList, sentense)
    
    
    def getPlace(self, sentense):
        return self.searchSimple(self.placesList, sentense)

    
    def searchMachineRecursive(self, itemlist, sentense, index=0):
        
        dataList = list()
        
        for item in itemlist:
            if len(item.split(' ')) > index:
                dataList.append(item)

        sentense = sentense.lower()
        
        qualifer = None
        ratio = 0
        
        for data in dataList:
            
            if ratio == 1:
                break
            
            dataTokens = nltk.word_tokenize(data)
            
            if len(dataTokens) == 1:
                               
                for word in nltk.word_tokenize(sentense.lower()):
                    
                    value = SequenceMatcher(None, word, data).ratio()
                    
                    if value > ratio:
                        ratio = value
                        qualifer = data
                    
                    if value == 1:
                        break
                    
            elif len(dataTokens) == 2:
                
                sentTokens = nltk.bigrams(nltk.word_tokenize(sentense))
                
                for sentToken in sentTokens:
                    
                    value = SequenceMatcher(None, ' '.join(sentToken), data).ratio()
                    
                    if value > ratio:
                        ratio = value
                        qualifer = data
                    
                    if value == 1:
                        break
            
            elif len(dataTokens) == 3:
                
                sentTokens = nltk.trigrams(nltk.word_tokenize(sentense))
                
                for sentToken in sentTokens:
                    
                    value = SequenceMatcher(None, ' '.join(sentToken), data).ratio()
                    
                    if value > ratio:
                        ratio = value
                        qualifer = data
                    
                    if value == 1:
                        break
                
            else:
                if data in sentense:
                    ratio = 1
                    qualifer = data
                
        if ratio < 0.9:
            qualifer = None
        
        else:
            secQualifier = self.searchMachineRecursive(dataList, sentense, len(qualifer.split(' ')))
            if secQualifier != None:
                qualifer = secQualifier    
                      
        return qualifer
        
 
    def searchMachineOneTime(self, itemlist, sentense, queue):
         
         
            dataList = itemlist
    
            sentense = sentense.lower()
            
            qualifer = ''
            ratio = 0
            
            for data in dataList:
                
                if ratio == 1:
                    break
                
                dataTokens = nltk.word_tokenize(data)
                
                if len(dataTokens) == 1:
                                   
                    for word in nltk.word_tokenize(sentense.lower()):
                        
                        value = SequenceMatcher(None, word, data).ratio()
                        
                        if value > ratio:
                            ratio = value
                            qualifer = data
                        
                        if value == 1:
                            break
                        
                elif len(dataTokens) == 2:
                    
                    sentTokens = nltk.bigrams(nltk.word_tokenize(sentense))
                    
                    for sentToken in sentTokens:
                        
                        value = SequenceMatcher(None, ' '.join(sentToken), data).ratio()
                        
                        if value > ratio:
                            ratio = value
                            qualifer = data
                        
                        if value == 1:
                            break
                
                elif len(dataTokens) == 3:
                    
                    sentTokens = nltk.trigrams(nltk.word_tokenize(sentense))
                    
                    for sentToken in sentTokens:
                        
                        value = SequenceMatcher(None, ' '.join(sentToken), data).ratio()
                        
                        if value > ratio:
                            ratio = value
                            qualifer = data
                        
                        if value == 1:
                            break
                    
                else:
                    if data in sentense:
                        ratio = 1
                        qualifer = data
                    
            if ratio < 0.9 or qualifer.strip() == '':
                qualifer = None
              
                          
            queue.put(qualifer)
     
     
    def searchSimple(self, itemlist, sentense):
        
        dataList = itemlist
        toReturn = None
        sentense = sentense.lower()
        
        for data in dataList:
            
            if ' ' + data + ' ' in sentense and len(data) > 3:
                toReturn = data
                break
        
        return toReturn
                
    
    
    
    
                
        
    
        
        
