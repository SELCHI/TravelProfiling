import nltk, re
from sys import stdout
from nltk import word_tokenize
from os import listdir
from nltk.tokenize import word_tokenize
import enchant 
from compiler.pycodegen import EXCEPT

class Cleaner:
	
	def __init__(self):
		self.en_dic = enchant.Dict("en_US");
		self.count = 0
		urbanlist = open("SlangDictionary.txt","r")
		self.urbandic = {}
		for entry in urbanlist:
			self.urbandic[entry.strip().split('-')[0]]=entry.strip().split('-')[1]
		
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
		toreturn = None
		try:
			toreturn = self.urbandic[word]
		except:
			print 'No entry in urban dic'
		
		if toreturn!=None:
			return toreturn		
		return word


    
