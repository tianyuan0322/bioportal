package org.ncbo.stanford.util.lucene;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
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

			String[] words = expr.split(SPACES_PATTERN);
			StringBuffer contentsExpr = new StringBuffer();

			for (int i = 0; i < words.length; i++) {
				contentsExpr.append(words[i]);

				if (i == words.length - 1) {
					contentsExpr.append(WILDCARD_CHAR);
				} else {
					contentsExpr.append(" && ");
				}
			}

			QueryParser parser = new QueryParser(field, new StandardAnalyzer());
//			parser.setAllowLeadingWildcard(true);
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);

			try {
				add(parser.parse(contentsExpr.toString()),
						BooleanClause.Occur.SHOULD);
			} catch (ParseException e) {
				IOException ioe = new IOException(e.getMessage());
				ioe.initCause(e);
				throw ioe;
			}
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

	public static boolean startsWithWildcard(String expr) {
		int len = expr.length();

		return (len > 0 && expr.charAt(0) == WILDCARD_CHAR);
	}

	public static boolean isMultiWord(String expr) {
		return expr.trim().split(SPACES_PATTERN).length > 1;
	}

	private String prepareExpression(String expr) {
		expr = expr.trim();
		
		if (startsWithWildcard(expr)) {
			expr = expr.substring(1);
		}

		if (endsWithWildcard(expr)) {
			expr = expr.substring(0, expr.length() - 1);
		}

		expr = expr.toLowerCase();
		expr = expr.replaceAll(SPACES_PATTERN, " ");
		expr = expr.replaceAll(":", " ");
		expr = expr.replaceAll("_", " ");
		
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
