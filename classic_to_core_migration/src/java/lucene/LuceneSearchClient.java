package lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class LuceneSearchClient {
	public static void main(String[] args) {
		try {
			// contents:"blood" && (ontologyId:1116 || ontologyId:1053)

//			File file = new File("/apps/bmir.apps/bioportal_resources/lucene", "lock");
//			System.out.println(file.getAbsolutePath());
			
			LuceneSearch search = LuceneSearch.getInstance();

//			search.indexOntology(1070);
//			search.indexOntology(1090);
//			search.indexOntology(1056);
//			search.indexAllOntologies();
//
//			// search.executeQuery("cell type", false);
//
			Collection<Integer> ontologyIds = new ArrayList<Integer>();
			ontologyIds.add(1070); // Biological Process (GO)
//			// ontologyIds.add(1090); // Amphibian gross anatomy
			ontologyIds.add(1032); // NCIT
//			// ontologyIds.add(1056); // Basic Vertebrate Anatomy
//			// ontologyIds.add(1053); // FMA
//			// ontologyIds.add(1089); // RadLex
			search.executeQuery("Blood", ontologyIds, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
