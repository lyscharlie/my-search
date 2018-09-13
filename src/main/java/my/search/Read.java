package my.search;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Read {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String indexDir = "E://my_search//my_index/iktest";
		boolean useSmart = false;
		String keyword = "乐视";

		Analyzer analyzer = new IKAnalyzer(useSmart);
		
		Configuration cfg = DefaultConfig.getInstance();
		Dictionary.initial(cfg);
		Dictionary dictionary = Dictionary.getSingleton();
//		
//		List<String> words = new ArrayList<String>();
//		words.add("乐视");
		dictionary.addWords(null);

		try {
			Directory dir = FSDirectory.open(new File(indexDir));

			System.out.println("-------------search---------------");

			IndexSearcher is = new IndexSearcher(IndexReader.open(dir));

			Query query = null;
			String q = null;

			if (keyword.equals("weixin")) {
				QueryParser parser = new QueryParser(Version.LUCENE_35, "text", analyzer);
				q = keyword + " 微信";
				query = parser.parse(q);
			} else {
				QueryParser parser = new QueryParser(Version.LUCENE_35, "text", analyzer);
				q = keyword;
				query = parser.parse(q);
			}

			System.out.println("search:[" + q + "]");

			TopDocs hits = is.search(query, 10);

			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println(doc.get("text"));
			}

			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
