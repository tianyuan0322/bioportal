package org.ncbo.stanford.util.lucene;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

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
import org.apache.lucene.search.regex.SpanRegexQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.util.Version;

/**
 * Class that handles the construction of Lucene "prefix" type queries (i.e.
 * "Microsoft app*" or "melanom*")
 * 
 * @author Michael Dorf
 * 
 */
public class PrefixQuery extends BooleanQuery {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(PrefixQuery.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -6160362866197315524L;

	private static final String SPACES_PATTERN = "\\s+";
	@SuppressWarnings("unused")
	private static final String SINGLE_LETTER_WORD_PATTERN = "^\\w$|\\s+\\w$";
	private static final char WILDCARD_CHAR = '*';
	private static final String WILDCARD_LEADING_TRAILING_PATTERN = "^\\"
			+ WILDCARD_CHAR + "|\\" + WILDCARD_CHAR + "$";
	private static final int EXACT_MATCH_BOOST = 10;	
	private static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
	private static final Pattern LUCENE_PATTERN = Pattern.compile(LUCENE_ESCAPE_CHARS);
	private static final String REPLACEMENT_STRING = "\\\\$0";

	private Version luceneVersion;
	private IndexReader reader;
	private Analyzer analyzer;

	public PrefixQuery(Version luceneVersion, IndexReader reader,
			Analyzer analyzer) {
		this.luceneVersion = luceneVersion;
		this.reader = reader;
		this.analyzer = analyzer;
	}

	/**
	 * Constructs a Lucene query that finds all possible matches for words or
	 * phrases in their order with wildcard character at the end of each(i.e.
	 * "bloo*" or "cutaneo* mela*" or "epidermol* bullos* acquisi*")
	 * 
	 * @param field
	 *            - field to search on
	 * @param expr
	 * @throws Exception
	 */
	public void parsePrefixQuery(String field, String expr) throws Exception {
		expr = prepareExpression(expr, field);

		if (expr.length() > 0) {
			TermQuery tq = new TermQuery(new Term(field, expr));
			tq.setBoost(EXACT_MATCH_BOOST);
			add(tq, BooleanClause.Occur.SHOULD);

			MultiPhraseQuery mpq = new MultiPhraseQuery();
			StringTokenizer st = new StringTokenizer(expr);

			while (st.hasMoreTokens()) {
				Term[] terms = expand(field, st.nextToken());

				mpq.add(terms);
			}

			add(mpq, BooleanClause.Occur.SHOULD);
		}
	}

	/**
	 * Constructs a Lucene query that finds all possible matches for words or
	 * phrases that start with a word or expression and contain a wildcard
	 * character at the end (i.e. "bloo*" or "cutaneous mela*")
	 * 
	 * @param field
	 *            - field to search on
	 * @param expr
	 */
	public void parseStartsWithPrefixQuery(String field, String expr)
			throws Exception {
		expr = prepareExpression(expr, field);

		if (expr.length() > 0) {
			SpanRegexQuery srq = new SpanRegexQuery(new Term(field, expr));
			SpanFirstQuery sfq = new SpanFirstQuery(srq, 1);
			add(sfq, BooleanClause.Occur.SHOULD);
		}
	}

	public static boolean isMultiWord(String expr) {
		return expr.trim().split(SPACES_PATTERN).length > 1;
	}

	private String prepareExpression(String expr, String field)
			throws ParseException {
		expr = expr.replaceAll(WILDCARD_LEADING_TRAILING_PATTERN, "");
		expr = expr.replaceAll("\\s*\\-\\s*", "-");
		expr = LUCENE_PATTERN.matcher(expr).replaceAll(REPLACEMENT_STRING);
		
		QueryParser parser = new QueryParser(luceneVersion, field, analyzer);
		Query query = parser.parse(expr);

		expr = query.toString().replace(field + ":", "");
		expr = expr.replace("\"", "");
		expr = expr.toLowerCase();
		expr = expr.replaceAll(":", " ");

		// // replace single-letter words with empty strings
		// Pattern mask = Pattern.compile(SINGLE_LETTER_WORD_PATTERN);
		// Matcher matcher = mask.matcher(expr);
		// boolean found = matcher.find();
		//		
		// if (found) {
		// expr = expr.replace(matcher.group(), "");
		// }

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

		return (Term[]) terms.toArray(new Term[terms.size()]);
	}
}
