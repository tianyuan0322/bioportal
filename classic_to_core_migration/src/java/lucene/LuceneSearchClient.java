package lucene;

import java.util.ArrayList;
import java.util.Collection;

public class LuceneSearchClient {
	public static void main(String[] args) {
		try {
			// contents:"blood" && (ontologyId:1116 || ontologyId:1053)

			LuceneSearch search = LuceneSearch.getInstance();

			search.indexOntology(1056);
//			search.index();

//			search.executeQuery("cell type", false);

			Collection<Integer> ontologyIds = new ArrayList<Integer>();
			ontologyIds.add(1056);
//			ontologyIds.add(1089);
			search.executeQuery("Cell", ontologyIds, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
