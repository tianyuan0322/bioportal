package org.ncbo.stanford.util.sparql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.apache.commons.lang.StringUtils;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.ncbo.stanford.sparql.bean.Mapping;

/** This is a helper class that generates SPARQL "UNION" queries for Bioportal mappings. 
 *
 * @author Manuel Salvadores
 */
public class SPARQLUnionGenerator {

    
    /** qStruct contains all the bind structure for the query.
    *   This structure is never exposed outside of this class */
    private Map<URI,List<Value>> qStruct = null;

    /** bidirBundles is a list of URI arrays (of size 2) that contain
    *   the predicates connected via bidirectionality. 
    *   This structure is never exposed outside of this class */
    private List<URI[]> bidirBundles = null;
    private Integer limit = null;
    private Integer offset = null;
    private boolean count = false;
    private SPARQLFilterGenerator parameters = null; 

    public SPARQLUnionGenerator() {
        qStruct = new Hashtable<URI,List<Value>>();
        bidirBundles  = new ArrayList<URI[]>();
    }

    /** Method to add bind patterns to the SPARQL query.
     * Example:
     *       generator.addBindValue(ApplicationConstants.SOURCE_ONTOLOGY_ID,
     *               new LiteralImpl(Integer.toString(ontologyId), ApplicationConstants.XSD_INTEGER));
     *       generator.addBindValue(ApplicationConstants.SOURCE_TERM,
     *               new URIImpl(conceptId));
     * @param predicate predicate of the triple pattern.
     * @param value value or object of the triple pattern.
     *  
     */    
    public void addBindValue(URI predicate, Value value) {
        List<Value> values = null;
        if ((values=qStruct.get(predicate))==null) {
            values = new ArrayList<Value>();
            qStruct.put(predicate,values);
        }
        if (values.size() == 0 || !values.contains(value)) {
            values.add(value);
        }
    }
  



    /** If two predicates are to be considered as part of a bidirectional
     *  relationshinp then they should registered with this method.
     * 
     * Example:
     *       generator.addBidirectional(ApplicationConstants.SOURCE_TERM,
     *                      ApplicationConstants.TARGET_TERM);
     * @param predicateA predicate source of bidirectional relation.
     * @param predicateB predicate target of bidirectional relation.
     *
     */ 
    public void addBidirectional(URI predicateA, URI predicateB) {
        URI[] bundle = new URI[2];
        bundle[0] = predicateA;
        bundle[1] = predicateB;
        bidirBundles.add(bundle);
    }


    /** setParameters is used to pass extra user parameters only for count based SPARQL queries.
     * 
     * @param parameters a SPARQLFilterGenerator instance holds filters to be added to a count SPARQL query.
     *
     */ 
    public void setParameters(SPARQLFilterGenerator parameters) {
        this.parameters = parameters;
    }

    /** flags the object to generate a count SPARQL query. */
    public void setCount(Boolean count) {
        this.count = count;
    }
    
    /** sets the offset for the SPARQL query, ignored if using setCount. */ 
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /** sets the limit for the SPARQL query. */ 
    public void setLimit(int limit) {
        this.limit = limit;
    }
  
    /** internal utility to gather all the bidirectional predicates for a given one */  
    private List<URI> getBidirectionalsForURI(URI k) {
        List<URI> bundleRes = new ArrayList<URI>();
        for (URI[] bundle : bidirBundles) {
            if (bundle[0].equals(k) && !bundleRes.contains(bundle[1]))
                bundleRes.add(bundle[1]);
            if (bundle[1].equals(k) && !bundleRes.contains(bundle[0]))
                bundleRes.add(bundle[0]);
        }
        return bundleRes;
    }


    /** Inner class that represents the tuple (predicate,value).
      * Used to generate the query.
      */
    static class UnionPropertyValue {
        URI p = null;
        Value v = null;
        UnionPropertyValue(URI p,Value v) {
            this.p = p;
            this.v = v;
        }
        public boolean equals(Object o) {
            if (o instanceof UnionPropertyValue) {
                UnionPropertyValue other = (UnionPropertyValue)o;
                return other.p.equals(this.p) && other.v.equals(this.v);
            }
            return false;
        }
        public int hashCode() {
            return super.hashCode();
        }

        public String toString() {
            if (v instanceof URI)
                return " ?mappingId "+p+" <"+v+"> .";
            return  " ?mappingId "+p+" "+v + " .";
        }
    }


    /** generates a SPARQL based on internal property values (binds, bidirectionality, count, offset, ...) */  
    public String getSPARQLQuery() {
        
        // Matrix to hold different combination of patterns of UnionPropertyValue.
        List<List<UnionPropertyValue>> blocks = new ArrayList<List<UnionPropertyValue>>();
       
        // Populating query blocks with internal qStruct and bidirBundles (via getBidirectionalsForURI). 
        for (Map.Entry<URI,List<Value>> entry : qStruct.entrySet()) {
            URI predicate = entry.getKey();
            List<Value> values = entry.getValue();
            List<URI> bundle = getBidirectionalsForURI(predicate);
            bundle.add(predicate);
            for (Value v : values) {
                List<UnionPropertyValue> block = new ArrayList<UnionPropertyValue>();
                for (URI u : bundle) {
                    block.add(new UnionPropertyValue(u,v));
                }
                blocks.add(block);
            }
        }

        //Call to permute blocks in order to implement bidirectionality.
        List<List<UnionPropertyValue>>  queryBlocks = SPARQLUnionGenerator.permuteBlocks(blocks);

        //Query string generation
        List<String> sUnionBlock = new ArrayList<String>();
        for (List<UnionPropertyValue> qb : queryBlocks) {
            String qbs = "{" + StringUtils.join(qb,"\n") + "}";
            sUnionBlock.add(qbs);
        }
        String query = StringUtils.join(sUnionBlock,"\nUNION\n");
        if (parameters != null && !parameters.isEmpty()) {
            Mapping mapping = new Mapping();
            List<String> triples = parameters.generateTriplePatterns(
            "mappingId", new Mapping());
            query += "\n"+StringUtils.join(triples, " . "); 
            query += "\n FILTER ( " + parameters.toFilter() + " )"; 
        }
        if (!count) {
            query = "SELECT DISTINCT ?mappingId {\n" + query + "}\n";
            if (offset!=null)
                query += " OFFSET " + offset;
            if (limit!=null)
                query += " LIMIT " + limit;
        }
        else
            query = "SELECT DISTINCT (count(?mappingId) as ?c) {\n" + query + "}\n";

        query = "PREFIX "+ApplicationConstants.NS_MAPPING_PREFIX+": <" + ApplicationConstants.NS_MAPPING_PREFIX_URI + ">\n" + query;
        return query;
    }

   /** It does set permutation only one element (value,property) from each UnionPropertyValue. */
   private static void permuteBlocksRec(List<List<UnionPropertyValue>> partial,
                                        List<UnionPropertyValue> sol,
                                        List<List<UnionPropertyValue>> blocks,
                                        List<List<UnionPropertyValue>> solutions) {
        if (sol.size() == blocks.size()) {
            /* Base case - exit branch */
            for (int u=0; u<sol.size(); u++) {
                if (!blocks.get(u).contains(sol.get(u)))
                    return;
            }
            solutions.add(sol);
            return;
        }
        for (int i = 0;i < partial.size(); i++) {
            List<UnionPropertyValue> block = partial.get(i);
            List<List<UnionPropertyValue>> pclone = new ArrayList<List<UnionPropertyValue>>();
            for (int t = 0;t < partial.size(); t++) {
                if (t!=i)
                    pclone.add(new ArrayList<UnionPropertyValue>(partial.get(t)));
            }
            for (int j = 0;j < block.size(); j++) {
               UnionPropertyValue s = block.get(j);
               List<UnionPropertyValue> sclone = new ArrayList<UnionPropertyValue>(sol);
               if (!containsProperty(sclone,s.p)) {
                   sclone.add(s);
                   /* Recursive call happens here */
                   SPARQLUnionGenerator.permuteBlocksRec(pclone,sclone,blocks,solutions);
               }
            }
       }
       
   }
    /** internal utility to gather all the bidirectional predicates for a given one */  
    private static boolean containsProperty(List<UnionPropertyValue> l,URI x) {
        for (UnionPropertyValue u: l) {
            if (x.equals(u.p))
                return true;
        }
        return false;
    }

    /** Recursive function that generates all combinations of query blocks. 
     * This is just the initializer of the recursion the real thing is happening in 
     * permuteBlocksRec */ 
    private static List<List<UnionPropertyValue>> permuteBlocks(List<List<UnionPropertyValue>> blocks) {
         List<List<UnionPropertyValue>> solutions = new ArrayList<List<UnionPropertyValue>>();
         SPARQLUnionGenerator.permuteBlocksRec(blocks,new ArrayList<UnionPropertyValue>(),blocks,solutions);
         return solutions;
    }
}
