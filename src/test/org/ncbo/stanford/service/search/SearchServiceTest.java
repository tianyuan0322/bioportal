package org.ncbo.stanford.service.search;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.lucene.search.Query;
import org.junit.Ignore;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Repeat;

import com.thoughtworks.xstream.XStream;

public class SearchServiceTest extends AbstractBioPortalTest {

	@Autowired
	IndexSearchService indexService;

	@Autowired
	QuerySearchService queryService;

	@Test
	public void testIndexOntology() throws Exception {
		System.out
				.println("SearchServiceTest: indexOntology().......................BEGIN");

		try {
			indexService.indexOntology(1032, false, true);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}

		System.out
				.println("SearchServiceTest: indexOntology().........................DONE");
	}

	@Test
	@Repeat(2)
	@Ignore
	public void testSearchAllOntologies() throws Exception {
		System.out
				.println("SearchServiceTest: searchAllOntologies().......................BEGIN");

		try {
			Integer maxNumHits = 50;
			Query query = queryService.generateLuceneSearchQuery(null, "cell",
					true, false);
			Page<SearchBean> results = queryService.executeQuery(query,
					maxNumHits);

			assertNotNull(results);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}

		System.out
				.println("SearchServiceTest: searchAllOntologies().........................DONE");
	}

	@Test
	@Ignore
	public void testSearchECG() throws Exception {
		System.out
				.println("SearchServiceTest: testSearchECG().......................BEGIN");

		try {
			Integer maxNumHits = 98;
			Query query = queryService.generateLuceneSearchQuery(null, "lead",
					true, false);
			Page<SearchBean> results = queryService.executeQuery(query,
					maxNumHits);

			assertNotNull(results);

			if (results.getNumResultsTotal() < 98)
				fail("Search results should be 98");

			System.out.println(getXML(results));
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}

		System.out
				.println("SearchServiceTest: testSearchECG().........................DONE");
	}

	@Test
	@Ignore
	public void testSearchAllOntologiesPaginated() throws Exception {
		System.out
				.println("SearchServiceTest: searchAllOntologies().......................BEGIN");

		Integer maxNumHits = 50;
		Query query = queryService.generateLuceneSearchQuery(null, "blood",
				true, false);
		Page<SearchBean> results = queryService.executeQuery(query, 3, 6,
				maxNumHits);

		assertNotNull(results);

		System.out.println(getXML(results));

		System.out
				.println("SearchServiceTest: searchAllOntologies().........................DONE");
	}

	public String getXML(Page<SearchBean> page) {
		String xmlHeader = ApplicationConstants.XML_DECLARATION + "\n";
		XStream xstream = new XStream();
		xstream.setMode(XStream.NO_REFERENCES);
		// xstream.alias("paginatableList", PaginatableList.class);
		xstream.alias("page", Page.class);
		xstream.alias("luceneSearchBean", SearchBean.class);
		// xstream.addDefaultImplementation(ArrayList.class,
		// SearchResultListBean.class);
		// xstream.addImplicitCollection(Page.class, "contents", List.class);

		return xmlHeader + xstream.toXML(page);
	}
}
