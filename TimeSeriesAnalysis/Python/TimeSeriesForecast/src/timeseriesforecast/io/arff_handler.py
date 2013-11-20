'''
Created on Nov 20, 2013

@author: samith
'''
import pandas as pd
import arff
import collections
import numpy
class ArffHandler(object):
    '''
    classdocs
    '''


    def __init__(self):
        '''
        Constructor
        '''
        self.PYTHON_TYPES = {
            numpy.float64: 'real',
            int: 'integer',
            str: "date 'yyyy-MM-dd'",
        }
      
    def saveAsArff(self , pd_resampled_df ,index_col= "Date",data_col_name = 'Departments' , path = "/home/samith/final.arff" ):
        pd_ts = pd_resampled_df[data_col_name]
        pd_ts.index = pd_ts.index.format()
        pd_ts.index = pd_ts.index.astype("str")
        unordered_dict  = pd_ts.to_dict()
        ordered_dict = collections.OrderedDict(sorted(unordered_dict.items()))
        ordered_list = ordered_dict.items()
        
        writer = arff.Writer(path, "travel_data", names=[index_col, data_col_name])
        writer.pytypes = dict(self.PYTHON_TYPES)
        for row in ordered_list:
            writer.write(row)
        writer.close()
        