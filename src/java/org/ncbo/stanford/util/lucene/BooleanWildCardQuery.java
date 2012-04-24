package org.ncbo.stanford.util.lucene;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;

import edu.emory.mathcs.backport.java.util.Arrays;

public class BooleanWildCardQuery extends BooleanQuery {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(BooleanWildCardQuery.class);
	private static final long serialVersionUID = 6175210725964749088L;

	private static final String IN_PAREN_DELIMITER = "#@#@#@#@#";
	private static final char WILDCARD_CHAR = '*';

	private static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
	private static final Pattern LUCENE_PATTERN = Pattern
			.compile(LUCENE_ESCAPE_CHARS);
	private static final String REPLACEMENT_STRING = "\\\\$0";
	private static final int EXACT_MATCH_BOOST = 10;

	private Version luceneVersion;
	private IndexReader reader;
	private Analyzer analyzer;

	public BooleanWildCardQuery(Version luceneVersion, IndexReader reader,
			Analyzer analyzer) {
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
		this.luceneVersion = luceneVersion;
		this.reader = reader;
		this.analyzer = analyzer;
	}

/*	public static void main(String[] args) {
		String expr;
		expr = "cas (blo+od del phi) -clo -(cat mo) 		         (artery       vibe)   (nil) ser_00 	gul";
		expr = "(heart lung) vess";
		expr = "(blood nose) -dis";
		expr = "hear attac -Anxiety";
		expr = "-(calcium(2+) irium)";
		expr = "calcium(";
		expr = "-(heart lung) -calcium(2) blood-clot";
		expr = "-(Lung-dish-soup heart) -(calcium(2)) blood_clot_heart bao_000";
		expr = "calcium(2+)";

		Version luceneVersion = Version.LUCENE_24;
		Analyzer analyzer = new StandardAnalyzer(luceneVersion, Collections
				.emptySet());
		String indexPath = "/apps/bmir.apps/bioportal_resources/searchindex";

		try {
			FSDirectory indexDir = NIOFSDirectory.open(new File(indexPath));
			IndexSearcher searcher = new IndexSearcher(indexDir, true);

			BooleanWildCardQuery q = new BooleanWildCardQuery(luceneVersion,
					searcher.getIndexReader(), analyzer);
			q.parseBooleanWildCardQuery("contents", expr);
			// q.parseExpression(expr, "contents");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	@SuppressWarnings("unchecked")
	public void parseBooleanWildCardQuery(String field, String expr)
			throws Exception {
		expr = parseExpression(expr, field);

		if (expr.length() > 0) {
			StringTokenizer st = new StringTokenizer(expr, " ");
			boolean inQuotes = false;

			while (st.hasMoreTokens()) {
				MultiPhraseQuery mpq = new MultiPhraseQuery();
				String termSetStr = st.nextToken();
				String[] termSet = termSetStr.split(IN_PAREN_DELIMITER);
				List<Term> clauseTerms = new ArrayList<Term>(0);

				if (termSet[0].matches("^[-+].+")) {
					termSet[0] = termSet[0].substring(1);
				}

				for (String term : termSet) {
					if (termSetStr.charAt(0) != '-') {
						TermQuery tq = new TermQuery(new Term(field, term));
						tq.setBoost(EXACT_MATCH_BOOST);
						add(tq, BooleanClause.Occur.SHOULD);
					}

					term = LUCENE_PATTERN.matcher(term).replaceAll(
							REPLACEMENT_STRING);

					// In most cases, inQuotes = true would mean that the
					// original term contained a dash (-) or underscore (_) and
					// it was replaced by Lucene parser with a space and double
					// quotes surrounding the affected terms. For example:
					// <pre>
					// blood-clot => "blood clot"
					// Anal_Melanoma => "anal melanoma"
					// </pre>
					// in these cases, we assume that only the last quoted word
					// would need to be wild-carded. The rest, will just be
					// added as individual terms.
					if (term.startsWith("\"")) {
						inQuotes = true;
						term = term.substring(1);
					} else if (term.endsWith("\"")) {
						inQuotes = false;
						term = term.substring(0, term.length() - 1);
					}

					if (inQuotes) {
						clauseTerms.add(new Term(field, term));
					} else {
						Term[] expandedTerms = expand(field, term);
						clauseTerms.addAll(Arrays.asList(expandedTerms));
					}
				}

				Term[] allTerms = clauseTerms.toArray(new Term[clauseTerms
						.size()]);
				mpq.add(allTerms);

				switch (termSetStr.charAt(0)) {
				case '-':
					add(mpq, BooleanClause.Occur.MUST_NOT);
					break;
				default:
					add(mpq, BooleanClause.Occur.MUST);
				}
			}
		}
	}

	private String parseExpression(String expr, String field)
			throws ParseException {
		QueryParser parser = new QueryParser(luceneVersion, field, analyzer);
		Query query = parser.parse(expr);
		expr = query.toString().replace(field + ":", "");

		// words in parens
		Pattern inParens = Pattern.compile("\\([^\\(\\)]+\\)");
		Matcher matcherParen = inParens.matcher(expr);

		while (matcherParen.find()) {
			String gr = matcherParen.group();
			String grRepl = gr.replaceAll(" ", IN_PAREN_DELIMITER).replaceAll(
					"\\(", "").replaceAll("\\)", "");
			expr = StringUtils.replace(expr, gr, grRepl);
		}

		return expr;
	}

	private Term[] expand(String field, String prefix) throws Exception {
		QueryParser parser = new QueryParser(luceneVersion, field, analyzer);
		Query queryExact = parser.parse(prefix + WILDCARD_CHAR);

		if (queryExact instanceof org.apache.lucene.search.PrefixQuery) {
			((org.apache.lucene.search.PrefixQuery) queryExact)
					.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
		}

		Query queryRewritten = queryExact.rewrite(reader);

		Set<Term> terms = new TreeSet<Term>();
		terms.add(new Term(field, prefix));
		queryRewritten.extractTerms(terms);

		return terms.toArray(new Term[terms.size()]);
	}
}
