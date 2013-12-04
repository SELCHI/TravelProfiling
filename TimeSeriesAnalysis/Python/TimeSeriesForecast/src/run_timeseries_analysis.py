'''
Created on Dec 2, 2013

@author: samith
'''
from timeseriesforecast.main.multi.main_process import MainProcess
if __name__ == '__main__':
    print "Start Time Series Analysis"
    process =    MainProcess()
    process.start()
    process.join()
    print "Done Time Series Analysis"