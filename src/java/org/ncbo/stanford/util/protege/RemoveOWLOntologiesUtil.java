package org.ncbo.stanford.util.protege;


import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

public class RemoveOWLOntologiesUtil {

    public static void removeOWLOntologies(OWLModel owlModel, Collection<Frame> frames) {
        //deletes all malformed imports that are RDFResources, rather than owl:Ontology
        frames.removeAll(getImportTree(owlModel));
        //deletes all instances of owl:Ontology class
        frames.removeAll(owlModel.getOWLOntologies());
    }

    public static Collection<RDFResource> getImportTree(OWLModel owlModel) {
        OWLOntology defaultOWLOntology = owlModel.getDefaultOWLOntology();
        RDFProperty owlImportsProperty = owlModel.getSystemFrames().getOwlImportsProperty();

        Collection<RDFResource> res = new ArrayList<RDFResource>();
        getAllImports(defaultOWLOntology, owlImportsProperty, res);

        return res;
    }

    private static void getAllImports(RDFResource ontology,
            RDFProperty owlImportsProperty, Collection<RDFResource> res) {

        if (res.contains(ontology)) {
            return;
        }

        res.add(ontology);
        Collection<RDFResource> directImports = getDirectImports(ontology, owlImportsProperty);
        for (RDFResource importedOntology : directImports) {
            getAllImports(importedOntology, owlImportsProperty, res);
        }
    }

    private static Collection<RDFResource> getDirectImports(RDFResource resource, RDFProperty owlImportsProperty) {
        @SuppressWarnings("unchecked")
        Collection<RDFResource> res = resource.getPropertyValuesAs(owlImportsProperty, RDFResource.class);
        if (res == null) {
            res = new ArrayList<RDFResource>();
        }
        return res;
    }
}