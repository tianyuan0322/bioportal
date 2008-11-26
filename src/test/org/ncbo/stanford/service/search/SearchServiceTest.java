package org.ncbo.stanford.service.search;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchServiceTest extends AbstractBioPortalTest {

	@Autowired
	SearchService searchService;

	@Test
	public void testSearchAllOntologies() throws IOException {
		System.out
				.println("SearchServiceTest: searchAllOntologies().......................BEGIN");

		Query query = searchService.generateLuceneSearchQuery(null, "cell",
				true, false);
		SearchResultListBean results = searchService.executeQuery(query);

		assertNotNull(results);
		
		for (SearchBean rec : results) {
			System.out.println(rec);
		}

		System.out
				.println("SearcjServiceTest: searchAllOntologies().........................DONE");
	}
}
