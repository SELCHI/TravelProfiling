'''
Created on Nov 4, 2013

@author: samith
'''

import re, os
import cPickle as pickle
from classifier_helper import ClassifierHelper
from wsd_helper import WSDHelper
from textblob import TextBlob
from sentimentanalysis.config import HARD_TO_CLASSIFY, POSITIVE, NEGATIVE, SUBJECTIVE,\
    OBJECTIVE, HARD_THRESHOLD

class SentimentClassifier():
    '''
    classdocs
    '''


    def __init__(self ,classifier_names = "MaxentClassifier" , domains = "tweets"):
        '''
        Constructor
        '''
        self.cl_helper = ClassifierHelper()
        self.wsd_helper = WSDHelper()
        self.ab_path = os.path.dirname(os.path.abspath(__file__))
        self.loaded_classifiers = self.set_classifiers(classifier_names, domains)
        self.subjective_classifier = pickle.load(open(self.ab_path+'/Data/Pickles/subjective/classifier-MaxentClassifier.rotten.pickle', 'r'))
        
    def subjective_and_objective_classification(self ,sentence ):
        tokens = self.cl_helper.bag_of_words(self.cl_helper.extract_words(sentence ,is_stem = True))
        decision = self.subjective_classifier.classify(tokens)
        subj = self.subjective_classifier.prob_classify(tokens).prob('subjective')
        obj = self.subjective_classifier.prob_classify(tokens).prob('objective')
        print "Subjectivity = %s Objectivity = %s decision = %s" %(subj, obj ,decision)
        if subj > obj:
            return SUBJECTIVE
        else:
            return OBJECTIVE
        
    def textblob_results(self ,sentence):
        testimonial = TextBlob(sentence)
        subjectivtiy = SUBJECTIVE
        if testimonial.subjectivity > 0.3:
            subjectivtiy = SUBJECTIVE
        else:
            subjectivtiy =  OBJECTIVE
        
        print "Subjectivity from Textblob %s" %(testimonial.subjectivity )   
        textblob_decision = self.prepare_textblob_results(testimonial.polarity)
        return (subjectivtiy , textblob_decision)
        
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
                key = classifier_name+"_"+domain
                try:
                    classifier = self.loaded_classifiers[key]
                except KeyError:
                    # Key is not present
                    classifier = self.load_classifiers(classifier_name, domain)
                    pass            
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
                #print abs(pos - neg)
                #print "Results from WSD SentiWordNet on %s Corpus "%domain
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
        if abs(pos - neg) <= HARD_THRESHOLD:# and neg is not 0 and pos is not 0:
            print " Text is Neutral/Hard To Classify"
            print ' Positive = %s , Negative = %s' % (pos, neg)
            return HARD_TO_CLASSIFY    
        elif pos > neg:
            print " Text is POSITIVE"
            print ' Positive = %s Negative = %s' % (pos, neg)
            return POSITIVE
        elif pos < neg:
            print " Text is NEGATIVE"
            print ' Positive = %s Negative = %s' % (pos, neg)
            return NEGATIVE
        else:
            return HARD_TO_CLASSIFY 
    
    def prepare_textblob_results(self , textblob_polarity):
        print "Results from TextBlob pattern" 
        polarity = textblob_polarity
        print ' Polarity %s' % polarity
        if -0.25 <= polarity <= 0.25:
            print " Text is Neutral/Hard To Classify"
            return HARD_TO_CLASSIFY   
        elif -0.25 > polarity:
            print " Text is NEGATIVE"
            return NEGATIVE
        elif polarity > 0.25:
            print " Text is POSITIVE"
            return POSITIVE
        else:
            return HARD_TO_CLASSIFY 
            
        
    def load_classifiers(self ,classifier_name = "MaxentClassifier" , domain_name = "tweets"):
        pickled_classifier = 'classifier-%s.%s.pickle' % (classifier_name, domain_name)
        pickle_dir = self.ab_path +'/'+'Data/Pickles/%s/%s' % (domain_name ,pickled_classifier)
        if not os.path.exists(pickle_dir): 
            return None
        classifier = pickle.load(open(pickle_dir, 'r'))
        return classifier
                
    def set_classifiers(self ,classifier_names = "MaxentClassifier" , domains = "tweets"):
        print "Loading classfiers..."
        req_classifiers = classifier_names.split()
        req_domains = domains.split()
        loaded_classifiers = {}
        for classifier_name in req_classifiers:
            if classifier_name == "WSD-SentiWordNet":
                continue
            
            for domain_name in req_domains:
                classifeir = self.load_classifiers(classifier_name, domain_name)
                if classifeir is None:
                    continue
                classifier_key_name = classifier_name+"_"+domain_name
                loaded_classifiers[classifier_key_name] = classifeir
                print "Classifier %s loaded!..." %(classifier_key_name)
                
        return loaded_classifiers
                
                
        