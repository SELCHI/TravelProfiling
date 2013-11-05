'''
Created on Nov 4, 2013

@author: samith
'''
from sentiment_classifier import SentimentClassifier
from pprint import pprint
from sentimentalanalysis import POSITIVE, NEGATIVE, SUBJECTIVE
class SentimentClassifierTest():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
    
    def test_classifier(self):
        classifier = SentimentClassifier()
        sentence = "Lanka is not good."
        
        # please refere __init__ file of the sentimentalanaliysis package
        # for mapping of the output values
        subjectivity  = classifier.subjective_and_objective_classification(sentence)
        results = classifier.classify(sentence)
        
        # final decision making                               
        sub_dsn = "OBJECTIVE"
        if subjectivity == SUBJECTIVE:
            sub_dsn = "SUBJECTIVE"     
            
        pos_count = 0
        neg_count = 0
        decision = "Hard to Classify"
        for each_cls_res in results:
            if each_cls_res["result"] == POSITIVE:
                pos_count = pos_count +1
            elif each_cls_res["result"] == NEGATIVE:
                neg_count = neg_count +1
        if (pos_count > neg_count):
            decision = "POSITIVE"
        elif (pos_count < neg_count):
            decision = "NEGATIVE"
            
        print "\n---------------------"
        print "Sentence :"+sentence   
        print "Subjective or Objective: "+ sub_dsn
        print "\nFinal decision is: " +decision
        print "--------------------\n"
        
            
    
        
if __name__ == "__main__":
    test = SentimentClassifierTest()
    test.test_classifier()