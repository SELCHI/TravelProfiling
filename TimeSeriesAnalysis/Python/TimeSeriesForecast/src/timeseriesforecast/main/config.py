'''
Created on Nov 20, 2013

@author: samith
'''
import os
WEEKLY_ARFF_PATH = "/home/samith/weekly.arff"
MONTHLY_ARFF_PATH = "/home/samith/monthly.arff"
INDEX_COL = "Date"
DATA_COL = "frequency"
INPUT_QUEUE_END_IDENTIFIER = "STOP"
OUTPUT_QUEUE_END_IDENTIFIER = "STOP"
BASE_WEEKLY_ARFF_PATH = str(os.getcwd())+"/arff/weekly/process_%d/"
BASE_MONTHLY_ARFF_PATH = str(os.getcwd())+"/arff/monthly/process_%d/"
NUMBER_OF_FETCH_PROCESSES = 1
NUMBER_OF_ANALYZE_PROCESSES = 2
NUMBER_OF_OUTPUT_PROCESSES = 1
INPUT_QUEUE_LIMIT = 10
OUTPUT_QUEUE_LIMIT = 30

def init_arff_locs(number_of_process):
    for i in range(number_of_process):
        weekly = BASE_WEEKLY_ARFF_PATH %(i)
        monthly= BASE_MONTHLY_ARFF_PATH % (i)
        for directory in [weekly ,monthly]:
            if not os.path.exists(directory):
                        os.makedirs(directory)