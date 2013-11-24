'''
Created on Nov 4, 2013

@author: samith
'''
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer
from nltk.tokenize import WordPunctTokenizer
from nltk.collocations import BigramCollocationFinder
from nltk.collocations import TrigramCollocationFinder
from nltk.metrics import BigramAssocMeasures
from nltk.metrics import TrigramAssocMeasures

class ClassifierHelper():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
    def bag_of_words(self ,words):
        return dict([(word, True) for word in words])

    def extract_words(self ,text ,is_bigram = True, is_trigram = False , is_stem = False):
        stemmer = PorterStemmer()
        tokenizer = WordPunctTokenizer()
        tokens = tokenizer.tokenize(text)
        if is_bigram:
            bigrams = self.get_best_bigrams(tokens)
            
        if is_trigram:
            trigrams = self.get_best_trigrams(tokens)
            
        if is_bigram:
            for bigram_tuple in bigrams:
                x = "%s %s" % bigram_tuple
                tokens.append(x)
                
        if is_trigram:
            for trigram_tuple in trigrams:
                x = "%s %s %s" % trigram_tuple
                tokens.append(x)
       
        if is_stem:
            result =  [stemmer.stem(x.lower()) for x in tokens if x not in stopwords.words('english') and len(x) > 1]
        else:
            result =  [x.lower() for x in tokens if x not in stopwords.words('english') and len(x) > 1]
        return result
    
    def get_best_bigrams(self ,tokens, n=200):
        bigram_finder = BigramCollocationFinder.from_words(tokens)
        bigrams = bigram_finder.nbest(BigramAssocMeasures.chi_sq, n)
        return bigrams
    
    def get_best_trigrams(self ,tokens, n=200):
        trigram_finder = TrigramCollocationFinder.from_words(tokens)
        trigrams  = trigram_finder.nbest(TrigramAssocMeasures.chi_sq, n)
        return trigrams
    
    def get_feature(self ,word):
        return dict([(word, True)])   