import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.util.ShortFormProvider;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 8/14/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DLQueryInstances {

    private DLQueryEngine dlQueryEngine;

    public DLQueryInstances(DLQueryEngine engine)
    {
        this.dlQueryEngine = engine;
    }

    public Set<OWLNamedIndividual> getInstances(String query)
    {
        Set<OWLNamedIndividual> individuals = null;
        try {

            individuals = dlQueryEngine.getInstances(query, true);

        } catch (ParserException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return individuals;

    }



}
