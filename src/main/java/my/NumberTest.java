package my;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class NumberTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String keyword = "1;25";

		List<String> list = new ArrayList<String>();
		list.add("1;20;122");
		list.add("2;25;125");
		list.add("3;12;305");

		Analyzer analyzer = new IKAnalyzer();
		Directory dir = new RAMDirectory();

		try {

			IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_35, analyzer));

			for (String item : list) {

				Document doc = new Document();
				doc.add(new Field("item", item.toString(), Field.Store.YES, Field.Index.ANALYZED));
				writer.addDocument(doc);
			}

			int num = writer.numDocs();
			System.out.println("total:" + num);
			writer.close();

			System.out.println("-------------search---------------");

			IndexSearcher is = new IndexSearcher(IndexReader.open(dir));

			QueryParser parser = new QueryParser(Version.LUCENE_35, "item", new StandardAnalyzer(Version.LUCENE_35));

			BooleanQuery finalQuery = new BooleanQuery();

			String[] arr = StringUtils.split(keyword, ";");
			for (String item : arr) {
				finalQuery.add(parser.parse(item), Occur.SHOULD);
			}

			TopDocs hits = is.search(finalQuery, 30);

			// for (int j = 0; j < 5; j++) {
			// System.out.println(hits.score(j));
			// }

			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println("[" + scoreDoc.score + "]" + doc.get("item"));
			}

			is.close();

		} catch (Exception e) {
			e.getStackTrace();
		}

	}

}
