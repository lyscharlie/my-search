package my;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class HighLight {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String text = "UC浏览器";
		TermQuery query = new TermQuery(new Term("field", "u"));
		Scorer scorer = new QueryScorer(query);
		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");
		Highlighter hig = new Highlighter(formatter, scorer);
		Analyzer analyzer = new IKAnalyzer();

		TokenStream tokens = analyzer.tokenStream("field", new StringReader(text));
		try {
			System.out.println(hig.getBestFragment(tokens, text));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		analyzer.close();
	}

}
