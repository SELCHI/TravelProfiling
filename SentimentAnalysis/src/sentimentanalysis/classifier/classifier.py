'''
Created on Nov 10, 2013

@author: samith
'''
from sentimentanalysis.classifier.sentiment_classifier import SentimentClassifier
from sentimentanalysis.classifier.emoticon_classifier import EmoticonClassifier
from sentimentanalysis.config import POSITIVE, NEGATIVE, SUBJECTIVE,\
    HARD_TO_CLASSIFY, DECISION_LIST, OBJECTIVE

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
        is_neutral = False
        print "---------------------------------"
        (is_neutral ,textblob_decision) = self.is_neutral(sentence)
        print "- - - - - - - - - - - -"
        if( not is_neutral):
            senti_results = self.sentiment_classifier.classify(sentence ,classifier_names = "MaxentClassifier WSD-SentiWordNet")
            senti_results.append({
                                "classifier"    : "TextBlob_Pattern",
                                "result"        : textblob_decision,
                               })
            (emo_result ,happy_count , sad_count) = self.emo_classifier.classify(sentence)
            (hard_count , pos_count , neg_count) = self.combine_results(senti_results, happy_count, sad_count)
            (decision ,final_value) = self.decision(hard_count, pos_count, neg_count)
        else:
            decision = HARD_TO_CLASSIFY
            final_value = 0.0
        print "- - - - - - - - - - - -"
        self.print_results(sentence, decision ,final_value)
        return (decision,final_value)
    
    def is_neutral(self ,sentence ):
        naive_subjective = self.sentiment_classifier.subjective_and_objective_classification(sentence)
        (textblob_subjective , polarity) = self.sentiment_classifier.textblob_results(sentence)
        if naive_subjective == SUBJECTIVE or textblob_subjective == SUBJECTIVE:
            return (False,polarity)
        else:
            return (True,polarity)
        
        
    def combine_results(self , senti_results , happy_count , sad_count ):
        (norm_happy_value , norm_sad_value) = self.normalized_emo_counts(happy_count, sad_count)
        (hard_count , pos_count , neg_count) = self.combine_senti_classifications(senti_results)
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
        final_value = 0.0
        decision = HARD_TO_CLASSIFY
        norm_value = self.normalized_final_value(hard_count, pos_count, neg_count)
        if hard_count >= pos_count and hard_count >= neg_count:
            decision = HARD_TO_CLASSIFY
        elif (pos_count > neg_count):
            decision = POSITIVE
            final_value = norm_value 
        elif (pos_count < neg_count):
            decision = NEGATIVE 
            final_value = (-1.0)*norm_value 
        else:
            decision = HARD_TO_CLASSIFY
        return (decision ,final_value)
    
    def normalized_final_value(self ,hard_count , pos_count , neg_count ):
        normalized_hard_count  = 0.0
        normalized_pos_count = 0.0
        normalized_neg_count = 0.0
        sum = pos_count + neg_count+hard_count
        final_value = 1.0
        if sum != 0:
            #normalized_hard_count = (hard_count) / sum
            normalized_pos_count = (pos_count) / sum
            normalized_neg_count = (neg_count) / sum
        pos_neg_diff = abs(normalized_pos_count - normalized_neg_count)
        if( pos_neg_diff > 0.85):
            final_value = 1.0
        elif (pos_neg_diff >= 0.7):
            final_value = 0.9
        elif (pos_neg_diff >= 0.5):
            final_value = 0.8
        else:
            final_value = 0.7
        
            
        return final_value
    
    def print_results(self ,sentence ,decision,final_value):
        print "\n************ DECISION ************" 
        print "Sentence :"+sentence   
        print "\nFinal decision is: " +DECISION_LIST[decision]
        print "\nFinal value is: %s" %(final_value)
        print "************************************\n"
                
if __name__ == "__main__":
    test = Classifier()
    sentence = "This is a Positive sentence. :)"
    test.classify(sentence)       
        
        