package test;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.net.UnknownHostException;
import java.util.Set;


public class MongoTest {

	public static void main(String[] args) {
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient();
			DB db = mongoClient.getDB( "mydb" );
			Set<String> colls = db.getCollectionNames();

			for (String s : colls) {
			    System.out.println(s);
			}
			DBCollection coll = db.getCollection("test");
			DBObject myDoc = coll.findOne();
			System.out.println(myDoc);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
