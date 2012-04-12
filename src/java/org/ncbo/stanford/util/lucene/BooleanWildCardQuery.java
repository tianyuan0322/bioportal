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

	private static final String SPACES_PATTERN = "\\s+";
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
		expr = "cas (blo+od del phi) -clo -(cat mo) 		         (artery       vibe)   (nil) ser 	gul";
		expr = "(heart lung) vess";
		expr = "(blood nose) -dis";
		expr = "hear attac -Anxiety";
		expr = "calcium(2+)";
		expr = "calcium(";
		expr = "-(heart lung) -calcium(2) blood-clot";
		expr = "-(heart lung-dish) -calcium(2) blood-clot";

		Version luceneVersion = Version.LUCENE_24;
		Analyzer analyzer = new StandardAnalyzer(luceneVersion, Collections
				.emptySet());
		String indexPath = "/apps/bmir.apps/bioportal_resources/searchindex";

		try {
			FSDirectory indexDir = NIOFSDirectory.open(new File(indexPath));
			IndexSearcher searcher = new IndexSearcher(indexDir, true);

			BooleanWildCardQuery q = new BooleanWildCardQuery(luceneVersion,
					searcher.getIndexReader(), analyzer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	@SuppressWarnings("unchecked")
	public void parseBooleanWildCardQuery(String field, String expr)
			throws Exception {
		expr = parseExpression(expr);

		if (expr.length() > 0) {
			StringTokenizer st = new StringTokenizer(expr);

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

					Term[] expandedTerms = expand(field, term);
					clauseTerms.addAll(Arrays.asList(expandedTerms));
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

	private String parseExpression(String expr) {
		expr = expr.replaceAll(SPACES_PATTERN, " ").toLowerCase();
		expr = expr.replace("\"", "");
		expr = expr.replace("_", " ");
		expr = expr.replace(":", " ");
		// replace all non-boolean-logic dashes with spaces
		expr = replaceInDashWithSpace(expr);

		// words in parens
		Pattern inParens = Pattern.compile("\\([^\\(\\)]+\\)");
		Matcher matcherParen = inParens.matcher(expr);

		while (matcherParen.find()) {
			String gr = matcherParen.group();
			int start = expr.indexOf(gr);

			try {
				char prev = expr.charAt(start - 1);

				// this means the parenthesis is part of the expression itself
				// and not the boolean logic. Do not process it!
				if (prev != '-' && prev != ' ') {
					continue;
				}
			} catch (StringIndexOutOfBoundsException e) {
				// this means the parenthesis is at the beginning
				// process normally
			}

			String grRepl = gr.replaceAll(" ", IN_PAREN_DELIMITER).replaceAll(
					"\\(", "").replaceAll("\\)", "");
			expr = StringUtils.replace(expr, gr, grRepl);
		}

		// replace all non-boolean-logic parentheses with spaces
		expr = expr.replace("(", " ").replace(")", " ");

		return expr;
	}

	private String replaceInDashWithSpace(String expr) {
		Pattern inDash = Pattern.compile("[^\\s](-)");
		Matcher matcherDash = inDash.matcher(expr);

		while (matcherDash.find()) {
			int dashInd = matcherDash.start(1);
			expr = expr.substring(0, dashInd) + " "
					+ expr.substring(dashInd + 1);
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
