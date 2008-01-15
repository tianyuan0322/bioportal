/**
 * 
 */
package org.ncbo.stanford.manager;

/**
 * An abstract class that implements standard methods used for creating NCBO
 * beans (e.g. OntologyBean and ConceptBean) from external repositories (e.g.
 * Protege and LexGrid). If there are no common bean methods for sharing between
 * LexGrid and Protege, this class could be removed.
 * 
 * TODO: Consider if this pattern is the best way to decouple beans instances
 * (e.g. ontology bean) from the source repository beans (i.e. Protege and
 * LexGrid).
 * 
 * 
 * @author bdai1
 * 
 */
public abstract class BioPortalBeanFactory {

}
