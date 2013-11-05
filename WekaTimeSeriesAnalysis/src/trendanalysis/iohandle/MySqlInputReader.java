package trendanalysis.iohandle;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

public class MySqlInputReader implements IFInputReader{

	private String url = Messages.getString("MySqlInputReader.mysqlDbUrl"); //$NON-NLS-1$
	@Override
	public Instances retrieveInstances(String userName, String password,
			String database, String table) {
		Instances airline =  null;
		try {
			InstanceQuery query = new InstanceQuery();
		    query.setDatabaseURL(url+database);
		    query.setUsername(userName);
		    query.setPassword(password);
		    query.setQuery(Messages.getString("MySqlInputReader.basicQuery")+table); //$NON-NLS-1$
			airline = query.retrieveInstances();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return airline;
	}

}
