package trendanalysis.iohandle;

import weka.core.Instances;

public interface IFInputReader {
	
	public abstract Instances retrieveInstances(String userName, String password,String database,String table );
	

}
