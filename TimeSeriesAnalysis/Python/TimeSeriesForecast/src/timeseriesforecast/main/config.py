'''
Created on Nov 20, 2013

@author: samith
'''
import os
WEEKLY_ARFF_PATH = "/home/samith/weekly.arff"
MONTHLY_ARFF_PATH = "/home/samith/monthly.arff"
INDEX_COL = "Date"
DATA_COL = "frequency_twitter"
INPUT_QUEUE_END_IDENTIFIER = "STOP"
OUTPUT_QUEUE_END_IDENTIFIER = "STOP"
BASE_WEEKLY_ARFF_PATH = str(os.getcwd())+"/arff/weekly/process_%d/"
BASE_MONTHLY_ARFF_PATH = str(os.getcwd())+"/arff/monthly/process_%d/"
NUMBER_OF_FETCH_PROCESSES = 1
NUMBER_OF_ANALYZE_PROCESSES = 2
NUMBER_OF_OUTPUT_PROCESSES = 1
INPUT_QUEUE_LIMIT = 10
OUTPUT_QUEUE_LIMIT = 30
FINALIZE_COUNT = 25
RUN_MAIN_PROCESS = True
MAIN_PROCESS_SLEEP_TIME = 3600
TS_START_DATE = "2011-09-01"
GATEWAY_SERVER_PORT = 25335

# DB connection
MYSQL_HOST_IP = "192.248.8.247"
MYSQL_PORT = 3306
MYSQL_USER = "root"
MYSQL_PW = "selchi123"
MYSQL_DB_PLACES = "PlacesDB"
MYSQL_DB_ACTIVITY = "ActivityDB"



ONTOLOGY_URL = "http://10.8.108.163/"
UPDATE_URL =  ONTOLOGY_URL+ 'TravelDataWebService/rest/updatetrends/%s/%s/%s/%s/%d/%d'
FINALIZE_URL = ONTOLOGY_URL+ 'TravelDataWebService/rest/updatetrends/finalize'

def init_arff_locs(number_of_process):
    for i in range(number_of_process):
        weekly = BASE_WEEKLY_ARFF_PATH %(i)
        monthly= BASE_MONTHLY_ARFF_PATH % (i)
        for directory in [weekly ,monthly]:
            if not os.path.exists(directory):
                        os.makedirs(directory)