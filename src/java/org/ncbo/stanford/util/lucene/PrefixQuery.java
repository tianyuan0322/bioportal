package org.ncbo.stanford.util.lucene;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.regex.SpanRegexQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;

public class PrefixQuery extends BooleanQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6160362866197315524L;
	private static final String SPACES = "\\s+";
	private static final char WILDCARD_CHAR = '*';

	private IndexReader reader;

	public PrefixQuery(IndexReader reader) {
		this.reader = reader;
	}

	public void parsePrefixQuery(String field, String expr) throws Exception {
		expr = prepareExpression(expr);

		if (expr.length() > 0) {
			Term t = new Term(field, expr);
			TermQuery tq = new TermQuery(t);
			tq.setBoost(10);
			add(tq, BooleanClause.Occur.SHOULD);

			MultiPhraseQuery mqr = new MultiPhraseQuery();
			String[] words = expr.split(SPACES);

			for (int i = 0; i < words.length; i++) {
				if (i == words.length - 1) {
					Term[] terms = expand(field, words[i]);
					mqr.add(terms);
				} else {
					mqr.add(new Term(field, words[i]));
				}
			}

			add(mqr, BooleanClause.Occur.SHOULD);
		}
	}

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
		return expr.trim().split(SPACES).length > 1;
	}

	private Term[] expand(String field, String prefix) throws IOException {
		ArrayList<Term> terms = new ArrayList<Term>(1);
		Term te = new Term(field, prefix);
		terms.add(te);

		TermEnum t = reader.terms(new Term(field, prefix));

		while (t.next() && t.term().text().startsWith(prefix)) {
			terms.add(t.term());
		}

		t.close();

		return (Term[]) terms.toArray(new Term[0]);
	}

	private String prepareExpression(String queryText) {
		if (endsWithWildcard(queryText)) {
			queryText = queryText.substring(0, queryText.length() - 1);
		}

		return queryText.toLowerCase().replaceAll(SPACES, " ");
	}
}
