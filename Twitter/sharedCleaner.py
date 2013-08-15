import nltk, re
from sys import stdout
from nltk import word_tokenize
from os import listdir
from nltk.tokenize import word_tokenize
import enchant 

class Cleaner:
	
	def __init__(self):
		self.en_dic = enchant.Dict("en_US");
		self.count = 0
		
	def clean(self,line):
		
		line = re.sub(r'(https?:\/\/)([^\.]*)\.([^\s]*)', '', line)
		line = re.sub(r'@([a-zA-z0-9-]+)', '', line)
		line = re.sub(r'#([^\s]*)', '', line)
		line = re.sub(r'RT.*:', '', line)
		line = re.sub(r'RT\s|\sRT\s', ' ', line)
		line = re.sub(r'[^\w\s]+', '', line)
		tokens = word_tokenize(line)
	   
		line = ""
		#check whether each token/word contains in the urban list  
		for eachword in tokens:
			if not self.en_dic.check(eachword):
				eachword = self.resolve(eachword)
	   
			line +=" "+eachword
		return line.strip()


	#search in the urban word list and if the word exists replace the word with it's fomal meaning
	def resolve(self,word):
		
		urbanlist = open("SlangDictionary.txt","r")
		for eachline in urbanlist:
			eachline= eachline.strip()
			split = eachline.split("-")
			if split[0] == word:
				return split[1]
		return word


    
