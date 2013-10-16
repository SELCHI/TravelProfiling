/**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 10/11/13
 * Time: 12:54 AM
 * To change this template use File | Settings | File Templates.
 */

import Onto.OntoReader;
import xml.WikiTravelParser;

import java.util.List;

public class Main {

    public static void main(String[] args)  {

        WikiTravelParser parser =  new WikiTravelParser();
        parser.readWikiWriteOnto("res/wikitravelorg_wiki_en-20110605-current.xml");

    }


}
