package my;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class NumberSearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String keyword = "10^3.0 9^2.0 12";

		int n = 13;

		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i <= 30; i++) {
			list.add(i);
		}

		Analyzer analyzer = new IKAnalyzer();
		Directory dir = new RAMDirectory();

		try {
			IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_35, analyzer));

			for (Integer item : list) {

				Integer result = item % n;

				System.out.println("Indexing :" + item + "[" + result + "]");

				Document doc = new Document();
				doc.add(new Field("item", item.toString(), Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("result", result.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc);
			}

			int num = writer.numDocs();
			System.out.println("total:" + num);
			writer.close();

			System.out.println("-------------search---------------");

			IndexSearcher is = new IndexSearcher(IndexReader.open(dir));

			Term term1 = new Term("result", "10");
			Term term2 = new Term("item", "10");
			
			Query query1 = new TermQuery(term1);
			Query query2 = new TermQuery(term2);

			BooleanQuery booleanquery = new BooleanQuery();
			booleanquery.add(query1, BooleanClause.Occur.SHOULD);
			booleanquery.add(query2, BooleanClause.Occur.MUST_NOT);

			// QueryParser parser = new QueryParser(Version.LUCENE_35, "result",
			// analyzer);
			// Query query = parser.parse(keyword);

			TopDocs hits = is.search(booleanquery, 30);

			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println(doc.get("item") + "[" + doc.get("result") + "]");
			}

			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
