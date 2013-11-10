'''
Created on Nov 11, 2013

@author: samith
'''
from sentimentanalysis.classifier.classifier import Classifier
classifier = Classifier()
sentence = "This is a Positive sentence. :)"
result = classifier.classify(sentence)
