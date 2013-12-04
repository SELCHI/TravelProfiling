'''
Created on Dec 5, 2013

@author: lasitha
'''

from EnchantSimilarWords import EnchantSimilarWords
from ThriGramSearcher import ThriGramSearcher
from BiGramSearcher import BiGramSearcher
import enchant 

class NGramResolver:
    
    def __init__(self):
        self.simwSearcher = EnchantSimilarWords()
        self.thriSearcher = ThriGramSearcher()
        self.biSearcher = BiGramSearcher()
        self.en_dic = enchant.Dict("en_US")
    
    def resolve(self,sentense):
        sentense = sentense.lower()
        sentense = self.convert(sentense)
        wordtokens= sentense.split()
        filtered = list()
        temp1 = ''
        temp2 = ''

        for token in wordtokens:
            
            if self.en_dic.check(token):
                filtered.append(token)
                temp1 = temp2
                temp2 = token
                #print token +' is in the dic'
                continue 
                 
            resolved = self.slangResolve(token)
            if resolved != token:
                filtered.append(resolved)
                temp1 = temp2
                temp2 = resolved
                #print 'resolution for '+token +' is ' + resolved
                continue 
            
            #Get best suggestion based on NGrams
            
            prwords = temp1+' '+temp2
            prwords = prwords.strip()  
            
            posblwords = self.simwSearcher.get(token)
            #print 'Possible words for :'+token +' '+str(posblwords)
            results = dict()
            
            if len(prwords.split(' '))==2:
                
                for psword in posblwords:   
                    results[psword] = self.thriSearcher.getCount(prwords+' '+psword)
                
                sol = ''
                maxsol = 0
                for key,value in results.iteritems():
                    
                    if value > maxsol:
                        sol = key
                        maxsol = value
                if maxsol > 0:
                    print maxsol
                    resolved = sol        
                
            elif len(prwords.split(' '))==1 and prwords!='':
                
                for psword in posblwords:   
                    results[psword] = self.biSearcher.getCount(prwords+' '+psword)
                
                sol = ''
                maxsol = 0
                for key,value in results.iteritems():
                    
                    if value > maxsol:
                        sol = key
                        maxsol = value
                if maxsol > 0:
                    print maxsol
                    resolved = sol 
            
            if resolved !=token:
                #New solution is found for a slang word, have to add this to sland dictionary
                slangFile = open("SlangData/SlangDictionary.txt", "a")
                slangFile.writelines(token+'-'+resolved+'\n')
                slangFile.close()
            else:
                #unresolved slangs have to put in for inspection
                inspect = open("SlangData/inspect.txt","r")
                data = inspect.readlines()
                inspect.close()
                
                for i in range(len(data)):
                    if token in data[i]:
                        splitted = data[i].strip().split(":")
                        splitted[1]= str(int(splitted[1])+1)
                        data[i] = splitted[0]+':'+splitted[1]+'\n'
                        break
                    
                    if i == len(data)-1:
                        data.append(token+':1\n')
                
                #write the file 
                inspectW = open("SlangData/inspect.txt","w")
                inspectW.writelines(data)
                inspectW.close()
         
            filtered.append(resolved)                               
              
            temp1 = temp2
            temp2 = resolved
            
            print ' '.join(filtered)
            
        return ' '.join(filtered)
        
    
    
    def slangResolve(self,word):
        
        urbanlist = open("SlangData/SlangDictionary.txt", "r")
        
        for eachline in urbanlist:
            eachline = eachline.strip()
            split = eachline.split("-")
            if split[0] == word:
                return split[1]
        urbanlist.close()
                
        return word 
    
    def convert(self,jsonData):
        
        if isinstance(jsonData, dict):
            return dict([(self.convert(key), self.convert(value)) for key, value in jsonData.iteritems()])
        elif isinstance(jsonData, list):
            return [self.convert(element) for element in jsonData]
        elif isinstance(jsonData, unicode):
            return jsonData.encode('utf-8')
        else:
            return jsonData   
    
    