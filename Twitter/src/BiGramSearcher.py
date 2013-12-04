'''
Created on Dec 3, 2013

@author: lasitha
'''


class BiGramSearcher:
    
   
    def getCount(self,sentense):
        thefile = open('NgramCorpuses/w2_.txt','r')
        frequency =0
        for line in thefile:
            temp = ' '.join(line.split())   
            
            if sentense in temp:
                
                frequency = line.split()[0]
                return frequency
        thefile.close()
           
        return frequency