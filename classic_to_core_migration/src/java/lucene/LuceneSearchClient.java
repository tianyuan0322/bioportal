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
//			search.indexOntology(1032);
//			search.indexOntology(1058);
			search.indexOntology(1055);
//			search.indexOntology(1056);
//			search.indexOntology(1089);
//			search.indexOntology(1057);
//			search.indexOntology(1090);
//			search.indexOntology(1053);
//			search.indexOntology(1054);
//			search.indexAllOntologies();

			System.out.println("Searching...");
			search.executeQuery("blood", true);

			Collection<Integer> ontologyIds = new ArrayList<Integer>();
			ontologyIds.add(1070); // Biological Process (GO) - OBO
//			ontologyIds.add(1032); // NCIT - OWL
			ontologyIds.add(1058); // SNP-Ontology - OWL
			ontologyIds.add(1055); // Galen - OWL
			ontologyIds.add(1056); // Basic Vertebrate Anatomy - OWL
			ontologyIds.add(1089); // BIRNLex - OWL
			ontologyIds.add(1057); // RadLex - FRAMES
			ontologyIds.add(1090); // Amphibian gross anatomy - OBO
			ontologyIds.add(1053); // FMA - OWL
			ontologyIds.add(1054); // Amino Acid - OWL
//			System.out.println("Searching...");
//			search.executeQuery("blood", ontologyIds, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
