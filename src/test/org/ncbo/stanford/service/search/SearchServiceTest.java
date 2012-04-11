package org.ncbo.stanford.service.search;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.search.Query;
import org.junit.Ignore;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
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
	@Ignore
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
	public void testSearchBranch() throws Exception {
		System.out
				.println("SearchServiceTest: testSearchBranch().......................BEGIN");

		try {
			String expr;
			expr = "DOID:1909";
			expr = "family";
			expr = "hydro";
			// expr = "Blue_Nevus-Like_Melanoma";
			// expr = "Interferon-Alfa_Lu-177-Monoclonal-Antibody-CC49_Pa";
			// expr = "Swiss_Albinos_City_of_Hope_Med_Ctr";
			// expr = "language";
			// expr = "lun";
			// expr = "algorith";
			// expr = "monadic Quality of an object*";
			// expr = "CHEBI:16069";
			// expr = "blood-vein";
			// expr = "Interferon-Alfa_Lu-177-Monoclonal-Antibody-CC49";
			// expr = "*Clarke's nu*";
			// expr = "multiply";

			String subtreeRootConceptId;
			subtreeRootConceptId = "Gene";
			subtreeRootConceptId = "GO:0012501";
			subtreeRootConceptId = null;

			Collection<Integer> ontologyIds = new ArrayList<Integer>(0);
			// ontologyIds.add(1032);
			// ontologyIds.add(1104);
			ontologyIds.add(1070);
			// ontologyIds.add(1107);
			// ontologyIds.add(1321); // Nemo

			Collection<String> objectTypes = new ArrayList<String>(0);
			objectTypes.add(ConceptTypeEnum.CONCEPT_TYPE_CLASS.getLabel());

			Collection<String> recordTypes = new ArrayList<String>(0);
			recordTypes.add(SearchRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME
					.getLabel());

			boolean includeProperties = false;
			boolean isExactMatch = false;
			Integer maxNumHits = 250;

			Query q = queryService.generateLuceneSearchQuery(ontologyIds,
					objectTypes, recordTypes, expr, includeProperties,
					isExactMatch);
			System.out.println("q: " + q);

			long start = System.currentTimeMillis();
			SearchResultListBean results = queryService.runQuery(q, maxNumHits,
					ontologyIds, subtreeRootConceptId, false);
			long stop = System.currentTimeMillis();

			System.out.println("Excecution Time: " + (double) (stop - start)
					/ 1000 + " seconds.");
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}

		System.out
				.println("SearchServiceTest: testSearchBranch().........................DONE");
	}

	@Test
	@Repeat(2)
	@Ignore
	public void testSearchAllOntologies() throws Exception {
		System.out
				.println("SearchServiceTest: searchAllOntologies().......................BEGIN");

		try {
			Integer maxNumHits = 50;
			Query query = queryService.generateLuceneSearchQuery(null, null,
					null, "cell", true, false);
			Page<SearchBean> results = queryService.executeQuery(query,
					maxNumHits, null, null, false);

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
			Query query = queryService.generateLuceneSearchQuery(null, null,
					null, "lead", true, false);
			Page<SearchBean> results = queryService.executeQuery(query,
					maxNumHits, null, null, false);

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
		Query query = queryService.generateLuceneSearchQuery(null, null, null,
				"blood", true, false);
		Page<SearchBean> results = queryService.executeQuery(query, 3, 6,
				maxNumHits, null, null, false);

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
