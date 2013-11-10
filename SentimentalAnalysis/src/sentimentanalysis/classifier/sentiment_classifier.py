'''
Created on Nov 4, 2013

@author: samith
'''

import re, os
import cPickle as pickle
from classifier_helper import ClassifierHelper
from wsd_helper import WSDHelper
from sentimentanalysis.config import HARD_TO_CLASSIFY, POSITIVE, NEGATIVE, SUBJECTIVE,\
    OBJECTIVE

class SentimentClassifier():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.cl_helper = ClassifierHelper()
        self.wsd_helper = WSDHelper()
        self.ab_path = os.path.dirname(os.path.abspath(__file__))
        
    def subjective_and_objective_classification(self ,sentence ):
        classifier = pickle.load(open(self.ab_path+'/Data/Pickles/subjective/classifier-MaxentClassifier.rotten.pickle', 'r'))
        tokens = self.cl_helper.bag_of_words(self.cl_helper.extract_words(sentence ,is_stem = True))
        decision = classifier.classify(tokens)
        subj = classifier.prob_classify(tokens).prob('subjective')
        obj = classifier.prob_classify(tokens).prob('objective')
        print "Subjectivity = %s Objectivity = %s decision = %s" %(subj, obj ,decision)
        if subj > obj:
            return SUBJECTIVE
        else:
            return OBJECTIVE
        
    def classify(self,sentence , classifier_names = "MaxentClassifier" , domain = "tweets"): 
        """Classify the sentence
        Keyword arguments:
        classifier_names -- classifier names as space separated strings
                                   for example if single classifier "MaxentClassifier",
                                   if two classifiers MaxentClassifier NaiveBayes"
        
        domain -- domain of the train corpus
                                 
        :return NaiveBayesClassifier: Corresponding classifier
        """   
        pos = neg = 0
        #NLTK Classifiers Starts Here ----------------------------------------->
        results = []
        req_classifiers = classifier_names.split();
        for classifier_name in req_classifiers:
            if not classifier_name == "WSD-SentiWordNet" :
                pickled_classifier = 'classifier-%s.%s.pickle' % (classifier_name, domain)
                pickle_dir = self.ab_path +'/'+'Data/Pickles/%s/%s' % (domain ,pickled_classifier)
                if not os.path.exists(pickle_dir): 
                    continue
                classifier = pickle.load(open(pickle_dir, 'r'))
                tokens = self.cl_helper.bag_of_words(self.cl_helper.extract_words(sentence))
                #decision = classifier.classify(tokens)
                neg = classifier.prob_classify(tokens).prob('neg')
                pos = classifier.prob_classify(tokens).prob('pos')
                decision = self.prepare_results(pos, neg, classifier_name, domain)
                results.append({
                                "classifier"    : classifier_name,
                                "result"        : decision,
                                "pos_score"     : pos,
                                "neg_score"     : neg
                               })
                
            #WSD Hue Starts Here ----------------------------------------->
            if classifier_name == "WSD-SentiWordNet":
                r = re.compile("[,.?()\\d]+ *")
                lines_list = r.split(sentence)
                pos, neg = self.wsd_helper.call_classifier(lines_list)
                normalize_wsd = pos + neg + 1
                pos = pos/normalize_wsd
                neg = neg/normalize_wsd
            
                print "Results from WSD SentiWordNet on %s Corpus "%domain
                decision = self.prepare_results(pos, neg, classifier_name, domain)
                results.append({
                                "classifier"    : classifier_name,
                                "result"        : decision,
                                "pos_score"     : pos,
                                "neg_score"     : neg
                               })
            #WSD Hue ENDS Here ----------------------------------------->
        return results
            
    def prepare_results(self ,pos, neg, classifier_name ,domain):
        print "Results from %s on %s Corpus" % (classifier_name, domain)
        if abs(pos - neg) <= 0.15 and neg != 0 and pos != 0:
            print "Text is Neutral/Hard To Classify"
            print 'Positive = %s , Negative = %s' % (pos, neg)
            return HARD_TO_CLASSIFY    
        elif pos > neg:
            print " Text is POSITIVE"
            print 'Positive = %s Negative = %s' % (pos, neg)
            return POSITIVE
        else:
            print " Text is NEGATIVE"
            print 'Positive = %s Negative = %s' % (pos, neg)
            return NEGATIVE
    
        