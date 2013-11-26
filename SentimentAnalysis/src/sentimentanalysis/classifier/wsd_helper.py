'''
Created on Nov 4, 2013

@author: samith
'''
import os, re, sys, codecs, nltk, operator
from nltk.corpus import wordnet as wn
from collections import defaultdict
import cPickle as pickle

class WSDHelper():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.ab_path = os.path.dirname(os.path.abspath(__file__))+'/'
        self.synsets_scores = pickle.load(open(self.ab_path+'Data/Pickles/libs/SentiWn.p','rb'))
        self.bag_of_words = pickle.load(open(self.ab_path+'Data/Pickles/libs/bag_of_words.p','rb'))
    
    def word_similarity(self, word1, word2):
        w1synsets = wn.synsets(word1)
        w2synsets = wn.synsets(word2)
        maxsim = 0
        for w1s in w1synsets:
            for w2s in w2synsets:
                current = wn.path_similarity(w1s, w2s)
                if (current > maxsim and current > 0):
                    maxsim = current
        return maxsim

    def disambiguateWordSenses(self, sentence, word):
        wordsynsets = wn.synsets(word)
        bestScore = 0.0
        result = None
        for synset in wordsynsets:
            for w in nltk.word_tokenize(sentence):
                score = 0.0
                for wsynset in wn.synsets(w):
                    sim = wn.path_similarity(wsynset, synset)
                    if(sim == None):
                        continue
                    else:
                        score += sim
                if (score > bestScore):
                    bestScore = score
                    result = synset
        return result
    
    def line_wn(self, line):
        line_wsd = ""
        for w in line.split():
            if self.disambiguateWordSenses(line, w):
                line_wsd += " " + self.disambiguateWordSenses(line, w).name
            else: line_wsd += " "+ w
        return line_wsd.strip()
    
    def SentiWordNet_to_pickle(self, swn):
        synsets_scores = defaultdict(list)
        for senti_synset in swn.all_senti_synsets():
            if not synsets_scores.has_key(senti_synset.synset.name):
                synsets_scores[senti_synset.synset.name] = defaultdict(float)
            synsets_scores[senti_synset.synset.name]['pos'] += senti_synset.pos_score
            synsets_scores[senti_synset.synset.name]['neg'] += senti_synset.neg_score
        return synsets_scores
    
    def classify(self, text, synsets_scores, bag_of_words):
        #synsets_scores = pickled object in data/SentiWN.p
        pos = neg = 0
        for line in text:
            if not line.strip() or line.startswith('#'):continue
            for sentence in line.split('.'):
                sentence = sentence.strip()
                sent_score_pos = sent_score_neg = 0
                for word in sentence.split():
                    if self.disambiguateWordSenses(sentence, word): 
                        disamb_syn = self.disambiguateWordSenses(sentence, word).name
                        if synsets_scores.has_key(disamb_syn):
                            #uncomment the disamb_syn.split... if also want to check synsets polarity
                            if bag_of_words['neg'].has_key(word.lower()):
                                sent_score_neg += synsets_scores[disamb_syn]['neg']
                            if bag_of_words['pos'].has_key(word.lower()):
                                sent_score_pos += synsets_scores[disamb_syn]['pos']
                pos += sent_score_pos
                neg += sent_score_neg
        return pos, neg
    
    def call_classifier(self, lines_list):
        results = []        
        bag_of_words = self.classify_polarity(self.bag_of_words)
        scorer = defaultdict(list)
        pos, neg = self.classify(lines_list, self.synsets_scores, bag_of_words)
        return pos, neg
    
    def classify_polarity(self ,bag_of_words):
        """
        Pops word from bag_of_words['neg'/'pos'] if the word appears
        more in 'pos/'neg' respectively
        """
        for word, count in bag_of_words['neg'].items():
            if count > bag_of_words['pos'][word]: bag_of_words['pos'].pop(word)
            else: bag_of_words['neg'].pop(word)
        return bag_of_words