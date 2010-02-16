package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.manager.diff.impl.OntologyDiffManagerLexGridImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Pradip Kanjamala
 */
public class OntologyDiffManagerLexGridImplTest extends
        AbstractBioPortalTest {

    @Autowired
    OntologyDiffManagerLexGridImpl diffManager;
    Date startDate_;
    Date endDate_;
    

    
    @Test
    public void testCellDiff() throws Exception {
        System.out.println("testCellDiff");
        startDate_ = new Date();
        OntologyBean ontologyOld = diffManager.getLatestNcboOntology(OntologyRetrievalManagerLexGridImplTest.TEST_OBO_CELL_OLD_DISPLAY_LABEL);
        OntologyBean ontologyNew = diffManager.getLatestNcboOntology(OntologyRetrievalManagerLexGridImplTest.TEST_OBO_CELL_DISPLAY_LABEL);        
//        diffManager.printOntologyDiffs(ontologyOld, ontologyNew);
        diffManager.createDiff(ontologyOld, ontologyNew);

        endDate_ = new Date();
        System.out.println(endDate_.getTime() - startDate_.getTime() +" ms");
        
    }
    

}
