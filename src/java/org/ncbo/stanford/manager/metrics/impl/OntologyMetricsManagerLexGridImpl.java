package org.ncbo.stanford.manager.metrics.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.naming.SupportedProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.metrics.OntologyMetricsManager;
import org.ncbo.stanford.manager.retrieval.impl.OntologyRetrievalManagerLexGridImpl;
/**
 * A implementation of the OntologyMetricsManager for ontologies stored in
 * LexGrid.
 * 
 * 
 * @author Pradip Kanjamala
 * 
 */

public class OntologyMetricsManagerLexGridImpl extends
        AbstractOntologyManagerLexGrid implements OntologyMetricsManager {

    private int maxSubClasses_;
    
    @SuppressWarnings("unused")
    private static final Log log = LogFactory
            .getLog(OntologyMetricsManagerLexGridImpl.class);
    private LexBIGService lbs;
    LexBIGServiceConvenienceMethods lbscm;

    public OntologyMetricsManagerLexGridImpl() throws Exception {
        lbs = LexBIGServiceImpl.defaultInstance();
        lbscm = (LexBIGServiceConvenienceMethods) lbs
                .getGenericExtension("LexBIGServiceConvenienceMethods");
    }
    
    public OntologyMetricsBean extractOntologyMetrics(OntologyBean ob)
            throws Exception  {
        maxSubClasses_ = OntologyMetricsManager.GOOD_DESIGN_SUBCLASS_LIMIT;
        
        OntologyMetricsBean omb = new OntologyMetricsBean();

        String schemeName = getLexGridCodingSchemeName(ob);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ob);
        ResolvedConceptReferencesIterator iterator = lbs
                .getCodingSchemeConcepts(schemeName, csvt).resolve(null,
                        null, null, null, false);

        omb.setNumberOfProperties(findNumberOfProperties(ob));
        
        OntologyRetrievalManagerLexGridImpl retrieveMan = new OntologyRetrievalManagerLexGridImpl();
        ArrayList<String> noDescConcepts = new ArrayList<String>();
        ArrayList<String> oneSubConcepts = new ArrayList<String>();
        HashMap<String, Integer> manySubConcepts = new HashMap<String,Integer>();
        String description = "";
        int numberOfSiblings = 0;
        int maxNumberOfSiblings = 0;
        int totalNumberSiblings = 0;
        int totalNumberParents = 0;
        while (iterator.hasNext()) {
            ResolvedConceptReference ref = iterator.next();
            List<ClassBean> childrenBean = retrieveMan.findChildren(ob, ref.getCode());
            ClassBean childBean = childrenBean.get(0);
            
            numberOfSiblings = (Integer)childBean.getRelation("ChildCount");
            totalNumberSiblings = totalNumberSiblings +numberOfSiblings;
            if(numberOfSiblings > maxNumberOfSiblings)
            {
                maxNumberOfSiblings = numberOfSiblings;
            }
            if(numberOfSiblings == 1)
            {
                oneSubConcepts.add(ref.getCode());
            }
            if(numberOfSiblings > maxSubClasses_)
            {
                manySubConcepts.put(ref.getCode(), numberOfSiblings);
            }
            totalNumberParents++;
            
            description = ref.getEntityDescription().getContent();  
            if(description==null || description.equals(""))  {
                noDescConcepts.add(ref.getCode());
            }
        }
        
        omb.setId(ob.getId());
        omb.setNumberOfClasses(totalNumberParents);
        omb.setClassesWithNoDocumentation(noDescConcepts);
        omb.setMaximumNumberOfSiblings(maxNumberOfSiblings);
        omb.setAverageNumberOfSiblings(totalNumberSiblings /totalNumberParents);
        omb.setClassesWithOneSubclass(oneSubConcepts);
        omb.setClassesWithMoreThanXSubclasses(manySubConcepts);
        
        // not done
        omb.setNumberOfAxioms(0);
        omb.setNumberOfIndividuals(0);
        omb.setMaximumDepth(0);
        omb.setClassesWithNoAuthor(new ArrayList<String>());
        omb.setClassesWithMoreThanOnePropertyValue(new ArrayList<String>());
        
        return omb;
    }

    public int findNumberOfProperties(OntologyBean ob)
            throws Exception  {
        String urnAndVersion = ob.getCodingScheme();
        String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
        CodingScheme cs = getCodingScheme(lbs, urnVersionArray[0],
                urnVersionArray[1]);
        SupportedProperty[] sp = cs.getMappings().getSupportedProperty(); 

        return sp.length;
    }
}
