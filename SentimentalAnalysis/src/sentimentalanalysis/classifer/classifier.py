'''
Created on Nov 10, 2013

@author: samith
'''
from sentimentalanalysis.classifer.sentiment_classifier import SentimentClassifier
from sentimentalanalysis.classifer.emoticon_classifier import EmoticonClassifier
from sentimentalanalysis.config import POSITIVE, NEGATIVE, SUBJECTIVE,\
    HARD_TO_CLASSIFY, DECISION_LIST

class Classifier(object):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.sentiment_classifier = SentimentClassifier()
        self.emo_classifier = EmoticonClassifier()
        self.emo_factor = 0.8
        
    def classify(self , sentence):
        senti_results = self.sentiment_classifier.classify(sentence)
        (emo_result ,happy_count , sad_count) = self.emo_classifier.classify(sentence)
        (hard_count , pos_count , neg_count) = self.combine_results(senti_results, happy_count, sad_count)
        decision = self.decision(hard_count, pos_count, neg_count)
        self.print_results(sentence, decision)
        return decision
        
    def combine_results(self , senti_results , happy_count , sad_count ):
        (norm_happy_value , norm_sad_value) = self.normalized_emo_counts(happy_count, sad_count)
        (hard_count , pos_count , neg_count) = self.combine_senti_classyfies(senti_results)
        pos_count = pos_count + norm_happy_value
        neg_count = neg_count + norm_sad_value
        return (hard_count , pos_count , neg_count)
        
    def normalized_emo_counts(self ,happy_count , sad_count ):
        norm_happy_value = 0
        norm_sad_value = 0
        sum = happy_count + sad_count
        if sum != 0:
            norm_happy_value = (happy_count * self.emo_factor) / sum
            norm_sad_value = (sad_count * self.emo_factor) / sum
        return (norm_happy_value , norm_sad_value)
    
    def combine_senti_classifications(self, results):
        pos_count = 0
        neg_count = 0
        hard_count = 0
        for each_cls_res in results:
            if each_cls_res["result"] == POSITIVE:
                pos_count = pos_count +1
            elif each_cls_res["result"] == NEGATIVE:
                neg_count = neg_count +1
            elif each_cls_res["result"] == HARD_TO_CLASSIFY:
                hard_count = hard_count + 1
        return (hard_count , pos_count , neg_count)
    
    def decision(self ,hard_count , pos_count , neg_count):
        decision = HARD_TO_CLASSIFY
        if hard_count >= pos_count and hard_count >= neg_count:
            decision = HARD_TO_CLASSIFY
        elif (pos_count > neg_count):
            decision = POSITIVE
        elif (pos_count < neg_count):
            decision = NEGATIVE        
        return decision
    
    def print_results(self ,sentence ,decision):
        print "\n************ DECISION ************" 
        print "Sentence :"+sentence   
        print "\nFinal decision is: " +DECISION_LIST[decision]
        print "--------------------\n"
                
if __name__ == "__main__":
    test = Classifier()
    sentence = "Had a awesome fun at Hikkaduwa :) :)"
    test.classify(sentence)       
        
        