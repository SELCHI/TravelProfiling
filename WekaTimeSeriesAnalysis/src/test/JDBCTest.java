package test;
import weka.core.Instances;
import weka.experiment.InstanceQuery;;

public class JDBCTest {

	    private static final String url = "jdbc:mysql://localhost/test_weka";
	 
	    private static final String user = "root";
	 
	    private static final String password = "123";
	 
	    public static void main(String args[]) {
	        try {
	        	
	            InstanceQuery query = new InstanceQuery();
	            query.setDatabaseURL(url);
	            query.setUsername(user);
	            query.setPassword(password);
	            query.setQuery("select * from mytable");
	            Instances data = query.retrieveInstances();
	            System.out.println("DSFDF");
	            System.out.println(data.attribute(2).ARFF_ATTRIBUTE_DATE);
	            
	            

	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
}
