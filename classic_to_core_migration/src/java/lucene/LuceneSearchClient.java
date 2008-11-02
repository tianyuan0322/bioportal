package lucene;

import java.util.ArrayList;
import java.util.Collection;

public class LuceneSearchClient {
	public static void main(String[] args) {
		try {
			// contents:"blood" && (ontologyId:1116 || ontologyId:1053)

			LuceneSearch search = LuceneSearch.getInstance();

//			search.index();

			search.executeQuery("cell type", false);

			Collection<Integer> ontologyIds = new ArrayList<Integer>();
			ontologyIds.add(1032);
//			ontologyIds.add(1089);
//			search.executeQuery("Cell Type", ontologyIds, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
