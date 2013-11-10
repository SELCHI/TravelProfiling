'''
Created on Nov 9, 2013

@author: samith
'''
import re, os
from sentimentanalysis.config import POSITIVE, NEGATIVE, HARD_TO_CLASSIFY,DECISION_LIST
class EmoticonClassifier():
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.compile()
        
    def compile(self):
        combine_and_compile = lambda pat:  re.compile(pat, re.UNICODE)
        
        NormalEyes = r'[:=X8]'
        #Wink = r'[;]'        
        NoseArea = r'(|o|O|-|\'|\'-)'  # # rather tight precision, \S might be reasonable...        
        HappyMouths = r'[Dd\)\]]'
        SadMouths = r'[\(\[]'
        
        happy_smilies = NormalEyes + NoseArea + HappyMouths 
        happy_smilies_rev = SadMouths + NoseArea  + NormalEyes  # reverse version
        self.Happy_RE = combine_and_compile('(\^_\^|' + happy_smilies + '|'+ happy_smilies_rev +')')
        
        sad_smilies = r'[:=X;8]' + NoseArea + SadMouths 
        sad_smilies_rev = r'[\)\]]' + NoseArea  + r'[:=X;8]'    # reverse version
        self.Sad_RE = combine_and_compile('(' + sad_smilies + '|'+ sad_smilies_rev + ')')
        # print self.Happy_RE.pattern
        # print self.Sad_RE.pattern
        
    def analyze_sentence(self, text):
        happy = []
        sad = []
        happy = self.Happy_RE.findall(text)
        sad = self.Sad_RE.findall(text)
        return (len(happy), len(sad))
    
    def classify(self , text):
        happy_count = 0
        sad_count = 0
        (happy_count , sad_count) = self.analyze_sentence(text)
        decision = HARD_TO_CLASSIFY
        if happy_count ==  sad_count:
            decision = HARD_TO_CLASSIFY
        elif happy_count >  sad_count:
            decision = POSITIVE
        else:
            decision = NEGATIVE
        self.print_results(decision, happy_count, sad_count)
        return (decision, happy_count , sad_count)
    
    def print_results(self ,decision, happy_count , sad_count):
        print "\nResults from EmoticonClassifier "
        print " Text is :" + DECISION_LIST[decision]
        print 'Happy = %s Sad = %s' % (happy_count, sad_count)
                
if __name__ == "__main__":
    emoticon_test = EmoticonClassifier()
    while True:
        text = raw_input(" Enter a sentence... \t type exit to exit\n")
        (result ,happy_count , sad_count) = emoticon_test.classify(text)
        if result ==  HARD_TO_CLASSIFY:
            print "HARD_TO_CLASSIFY"
        elif result ==  POSITIVE:
            print "POSITIVE"
        else:
            print "NEGATIVE"