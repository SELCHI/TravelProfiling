'''
Created on Dec 2, 2013

@author: samith
'''
from timeseriesforecast.main.multi.main_process import MainProcess
if __name__ == '__main__':
    process =    MainProcess()
    process.start()
    process.join()
    print "Done main process"