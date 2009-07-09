package org.ncbo.stanford.util.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.regex.SpanRegexQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;

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
	private static final int EXACT_MATCH_BOOST = 10;
	private static HashMap<String, String> exceptionTermMap = new HashMap<String, String>(
			0);
	private IndexReader reader;

	static {
		exceptionTermMap.put("algorith", "algorithm");
	}

	public PrefixQuery(IndexReader reader) {
		this.reader = reader;
	}

	/**
	 * Constructs a Lucene query that finds all possible matches for words or
	 * phrases that contain a wildcard character at the end (i.e. "bloo*" or
	 * "cutaneous mela*")
	 * 
	 * @param field -
	 *            field to search on
	 * @param expr
	 * @throws Exception
	 */
	public void parsePrefixQuery(String field, String expr) throws Exception {
		expr = prepareExpression(expr);

		if (expr.length() > 0) {
			TermQuery tq = new TermQuery(new Term(field, expr));
			tq.setBoost(EXACT_MATCH_BOOST);
			add(tq, BooleanClause.Occur.SHOULD);

			MultiPhraseQuery mpq = new MultiPhraseQuery();
			String[] words = expr.split(SPACES_PATTERN);

			for (int i = 0; i < words.length; i++) {
				if (i == words.length - 1) {
					Term[] terms = expand(field, words[i]);
					mpq.add(terms);
				} else {
					mpq.add(new Term(field, words[i]));
				}
			}

			add(mpq, BooleanClause.Occur.SHOULD);
		}
	}

	/**
	 * Constructs a Lucene query that finds all possible matches for words or
	 * phrases that start with a word or expression and contain a wildcard
	 * character at the end (i.e. "bloo*" or "cutaneous mela*")
	 * 
	 * @param field -
	 *            field to search on
	 * @param expr
	 */
	public void parseStartsWithPrefixQuery(String field, String expr) {
		expr = prepareExpression(expr);

		if (expr.length() > 0) {
			SpanRegexQuery srq = new SpanRegexQuery(new Term(field, expr));
			SpanFirstQuery sfq = new SpanFirstQuery(srq, 1);
			add(sfq, BooleanClause.Occur.SHOULD);
		}
	}

	public static boolean endsWithWildcard(String expr) {
		int len = expr.length();

		return (len > 0 && expr.lastIndexOf(WILDCARD_CHAR) == len - 1);
	}

	public static boolean isMultiWord(String expr) {
		return expr.trim().split(SPACES_PATTERN).length > 1;
	}

	private Term[] expand(String field, String prefix) throws IOException {
		ArrayList<Term> terms = new ArrayList<Term>(1);
		terms.add(new Term(field, prefix));
		TermEnum te = reader.terms(new Term(field, prefix));

		for (String key : exceptionTermMap.keySet()) {
			if (prefix.equals(key)) {
				terms.add(new Term(field, exceptionTermMap.get(key)));
			}
		}

		while (te.next() && te.term().field().equals(field)
				&& te.term().text().startsWith(prefix)) {
			terms.add(te.term());
		}

		te.close();

		return (Term[]) terms.toArray(new Term[0]);
	}

	private String prepareExpression(String expr) {
		expr = expr.trim().toLowerCase();

		if (endsWithWildcard(expr)) {
			expr = expr.substring(0, expr.length() - 1);
		}

		expr = expr.replaceAll(SPACES_PATTERN, " ");

		// replace single-letter words with empty strings
		// Pattern mask = Pattern.compile(SINGLE_LETTER_WORD_PATTERN);
		// Matcher matcher = mask.matcher(expr);
		// boolean found = matcher.find();
		//
		// if (found) {
		// expr = expr.replace(matcher.group(), "");
		// }

		return expr;
	}
}
