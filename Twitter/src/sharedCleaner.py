'''
Created on Oct 26, 2013

@author: chiran
'''
import nltk, re
from sys import stdout
from nltk import word_tokenize
from os import listdir
from nltk.tokenize import word_tokenize
import enchant 
from NGramResolver import NGramResolver

class Cleaner:
    
    def __init__(self):
        self.en_dic = enchant.Dict("en_US");
        self.count = 0
        self.ngresolver = NGramResolver()
        
    def clean(self, line):
        
        line = re.sub(r'(https?:\/\/)([^\.]*)\.([^\s]*)', '', line)
        line = re.sub(r'@([a-zA-z0-9-]+)', '', line)
        line = re.sub(r'#([^\s]*)', '', line)
        line = re.sub(r'RT.*:', '', line)
        line = re.sub(r'RT\s|\sRT\s', ' ', line)
        line = re.sub(r'[^\w\s]+', '', line)
        tokens = word_tokenize(line)
            
        return self.ngresolver.resolve(line).strip()


    
