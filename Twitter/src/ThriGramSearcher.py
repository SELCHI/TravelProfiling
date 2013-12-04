'''
Created on Dec 3, 2013

@author: lasitha
'''
class ThriGramSearcher:
       
        
    def getCount(self,sentense):
        thefile =  open('NgramCorpuses/w3_.txt','r')
        frequency =0
        for line in thefile:
            temp = ' '.join(line.split())   
            
            if sentense in temp:
                
                frequency = line.split()[0]
                return frequency
        
        thefile.close()
                
        return frequency
    

if __name__ == '__main__': 
    
    ts = ThriGramSearcher()
    print ts.getCount('in any activity')
    
    
    
    