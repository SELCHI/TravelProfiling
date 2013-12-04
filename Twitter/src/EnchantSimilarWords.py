'''
Created on Nov 29, 2013

@author: lasitha
'''
import enchant 
from difflib import SequenceMatcher

class EnchantSimilarWords:
    
    def get(self,word):
        endic = enchant.Dict("en_US")
        suggestions= endic.suggest(word)

        topSet = list()
        ratiovalues = list()
        calculations = {}

        for i in range(len(suggestions)):
    
            sug = suggestions[i]
            if ' ' in sug or '-' in sug:
                continue
            tmp = SequenceMatcher(None,sug,word).ratio()
            calculations[sug]=tmp
            ratiovalues.append(tmp)

        data = set(ratiovalues)

        sortedRatios= sorted(data,reverse=True)
        limit = 3
        if len(sortedRatios) < limit:
                   
            limit = len(sortedRatios)

        topRatios = sortedRatios[:limit]

        for key,value in calculations.iteritems():
                                    
                if value in topRatios:               
                    topSet.append(key)

        return topSet
    
    