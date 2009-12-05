package org.ncbo.stanford.util.protege;

import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.resource.Files;
import edu.stanford.smi.protege.storage.clips.ClipsKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.SystemUtilities;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;

public class ProtegeUtil {
	
	private static final String SLOT_SOURCES = "sources";
	private static final String CLASS_PROJECT = "Project";
	private static final String OWL_FILE_URI_PROPERTY = "owl_file_name";

	public static boolean isFramesProject(URI pprjUri) {
		return getKnowledgeBaseFactory(pprjUri) instanceof ClipsKnowledgeBaseFactory;
	}
	
	public static boolean isOWLProject(URI pprjUri) {
		return getKnowledgeBaseFactory(pprjUri) instanceof OWLKnowledgeBaseFactory;
	}
	
    public static KnowledgeBaseFactory getKnowledgeBaseFactory(URI pprjUri) {        
        KnowledgeBase pprjKb = getPprjKb(pprjUri);
        if (pprjKb == null) { 
        	return null;
        }
        
        KnowledgeBaseFactory factory;        
        String name = getSources(pprjKb).getString(KnowledgeBaseFactory.FACTORY_CLASS_NAME);
        if (name == null) {
            factory = new ClipsKnowledgeBaseFactory();
        } else {
            factory = (KnowledgeBaseFactory) SystemUtilities.newInstance(name);
        }
        return factory;
    }
	
    private static KnowledgeBase getPprjKb(URI uri) {
    	return loadPprjFile(uri, new ClipsKnowledgeBaseFactory(), new ArrayList());
    }
    
    private static KnowledgeBase loadPprjFile(URI uri, KnowledgeBaseFactory factory, Collection errors) {
        KnowledgeBase kb = null;
        Reader clsesReader = null;
        Reader instancesReader = null;
        try {
            clsesReader = getProjectClsesReader();
            instancesReader = getProjectInstancesReader(uri, factory, errors);
            if (instancesReader == null) {   
                Log.getLogger().severe("Unable to open project file (pprj): " + uri);
            } else {              
            	ClipsKnowledgeBaseFactory clipsFactory = new ClipsKnowledgeBaseFactory();
            	
            	kb = clipsFactory.createKnowledgeBase(errors);
            	kb.setGenerateEventsEnabled(false);
            	
            	clipsFactory.loadKnowledgeBase(kb, clsesReader, instancesReader, false, errors);
            	                          
                kb.setGenerateEventsEnabled(false);
                kb.setDispatchEventsEnabled(false);
            }
        } catch (Exception e) {        	
            Log.getLogger().log(Level.SEVERE, "Error loading project kb", e);            
        } finally {
            FileUtilities.close(clsesReader);
            FileUtilities.close(instancesReader);
        }
        return kb;
    }
	
    private static Reader getProjectClsesReader() {
        Reader reader = Files.getSystemClsesReader();
        if (reader == null) {
            Log.getLogger().severe("Unable to read system ontology");
        }
        return reader;
    }
    
    private static Reader getProjectInstancesReader(URI uri, KnowledgeBaseFactory factory, Collection errors) {
        Reader reader = null;
        if (uri != null) {
            reader = URIUtilities.createBufferedReader(uri);
            if (reader == null) {
            	String message = "Unable to load project from: " + uri;               
                Log.getLogger().severe(message);
            }
        }
        if (reader == null && factory != null) {
            String path = factory.getProjectFilePath();
            if (path != null) {
                reader = FileUtilities.getResourceReader(factory.getClass(), path);
                if (reader == null) {                	
                    Log.getLogger().severe("Unable to read factory project: " + path);
                }
            }
        }
        if (reader == null) {
            reader = Files.getSystemInstancesReader();
            if (reader == null) {
                Log.getLogger().severe("Unable to read system instances");
            }
        }
        return reader;
    }
    
    private static Instance getProjectInstance(KnowledgeBase kb) {
        Instance result = null;
        Cls cls = kb.getCls(CLASS_PROJECT);
        if (cls == null) {
            Log.getLogger().severe("no project class");
        } else {
            Collection<Instance> instances = cls.getDirectInstances();    
            result = CollectionUtilities.getFirstItem(instances);
        }
        if (result == null) {
            Log.getLogger().severe("no project instance");
        }
        return result;
    }
    
    private static PropertyList getSources(KnowledgeBase kb) {
        return new PropertyList((Instance) ModelUtilities.getDirectOwnSlotValue(getProjectInstance(kb), SLOT_SOURCES));
    }
    
    public static URI getOWLFileUri(URI pprjUri) {
    	KnowledgeBase pprjKb = getPprjKb(pprjUri);
    	PropertyList sources = getSources(pprjKb);
    		
        try {
            String owlURI = getOWLFilePath(sources);
            if (owlURI.startsWith("http://")) {
                return new URI(owlURI);
            }
            else {
                URI projectURI = pprjUri;
                if (projectURI == null) {
                    return new URI(owlURI);
                }
                else {
                    URI projectDirURI = URIUtilities.getParentURI(pprjUri);
                    URI rel = URIUtilities.relativize(projectDirURI, new URI(owlURI));
                    URI result = projectURI.resolve(rel);
                    return result;
                }
            }
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
        return null;
    }
    
    private static String getOWLFilePath(PropertyList sources) {
        return sources.getString(OWL_FILE_URI_PROPERTY);
    }
}
