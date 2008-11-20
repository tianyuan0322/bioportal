package lucene;

import java.util.ArrayList;
import java.util.Collection;

import lucene.bean.LuceneSearchBean;

import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.Paginator;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.paginator.impl.PaginatorImpl;

import com.sshtools.j2ssh.io.UnsignedInteger64;
import com.thoughtworks.xstream.XStream;

public class LuceneSearchClient {
	public static void main(String[] args) {
		try {
			// contents:"blood" && (ontologyId:1116 || ontologyId:1053)

			// File file = new
			// File("/apps/bmir.apps/bioportal_resources/lucene", "lock");
			// System.out.println(file.getAbsolutePath());

			LuceneSearch search = LuceneSearch.getInstance();
			// search.indexOntology(1121);

			// search.backupIndex();
			// search.indexOntology(1070);
			// search.indexOntology(1032);
			// search.indexOntology(1058);
			// search.indexOntology(1055);
			// search.indexOntology(1056);
			// search.indexOntology(1089);
			// search.indexOntology(1057);
			// search.indexOntology(1090);
			// search.indexOntology(1053);
			// search.indexOntology(1054);
			// search.indexOntology(1104);
			// search.indexOntology(1066);
			// search.indexOntology(1047);

			// search.indexAllOntologies();
			Collection<Integer> ontologyIds = new ArrayList<Integer>();

			ontologyIds.add(1070); // Biological Process (GO) - OBO
			ontologyIds.add(1102); // Cellular Component (GO) - OBO
			ontologyIds.add(1032); // NCIT - OWL
			ontologyIds.add(1058); // SNP-Ontology - OWL
			ontologyIds.add(1055); // Galen - OWL
			ontologyIds.add(1056); // Basic Vertebrate Anatomy - OWL
			ontologyIds.add(1089); // BIRNLex - OWL
			ontologyIds.add(1057); // RadLex - FRAMES
			ontologyIds.add(1090); // Amphibian gross anatomy - OBO
			ontologyIds.add(1053); // FMA - OWL
			ontologyIds.add(1054); // Amino Acid - OWL
			ontologyIds.add(1104); // Biomedical Resource Ontology (BRO) - OWL
			ontologyIds.add(1066); // Unit - OBO
			ontologyIds.add(1047); // Cereal plant development - OBO

			System.out.println("Searching...");
			SearchResultListBean results = search.executeQuery("Cell",
					ontologyIds, false, true);

			Paginator<LuceneSearchBean> p = new PaginatorImpl<LuceneSearchBean>(
					results);
			Page<LuceneSearchBean> page = p.getAll();

			String xmlHeader = ApplicationConstants.XML_DECLARATION + "\n";
			XStream xstream = new XStream();
			xstream.setMode(XStream.NO_REFERENCES);
			// xstream.alias("paginatableList", PaginatableList.class);
			xstream.alias("page", Page.class);
			xstream.alias("luceneSearchBean", LuceneSearchBean.class);
			// xstream.addDefaultImplementation(ArrayList.class, SearchResultListBean.class);
			// xstream.addImplicitCollection(Page.class, "contents", List.class);

			// ArrayList l = new ArrayList();
			// l.add("hello");
			// l.add("world");

//			System.out.println(xmlHeader + xstream.toXML(page));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
