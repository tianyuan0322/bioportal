package org.ncbo.stanford.service.search;

import static org.junit.Assert.assertNotNull;

import org.apache.lucene.search.Query;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchServiceTest extends AbstractBioPortalTest {

	@Autowired
	IndexService indexService;

	@Autowired
	QueryService queryService;

	@Test
	public void testIndexOntology() throws Exception {
		System.out
				.println("SearchServiceTest: indexOntology().......................BEGIN");

		indexService.indexOntology(1056, false);

		System.out
				.println("SearchServiceTest: indexOntology().........................DONE");
	}

	@Test
	public void testSearchAllOntologies() throws Exception {
		System.out
				.println("SearchServiceTest: searchAllOntologies().......................BEGIN");

		Query query = queryService.generateLuceneSearchQuery(null, "cell",
				true, false);
		Page<SearchBean> results = queryService.executeQuery(query);

		assertNotNull(results);

		System.out
				.println("SearchServiceTest: searchAllOntologies().........................DONE");
	}
}
