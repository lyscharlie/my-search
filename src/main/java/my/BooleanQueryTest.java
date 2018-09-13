package my;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class BooleanQueryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String keyword = "QQ";
		boolean useSmart = false;

		try {
			Directory dir = FSDirectory.open(new File("E://temp//index//pcsuit_2"));
			Analyzer analyzer = new IKAnalyzer(useSmart);

			BooleanQuery finalQuery = new BooleanQuery();
			// Query query = new TermQuery(new Term("sourceType", "10"));
			// Query query = new TermQuery( );
			Query query = NumericRangeQuery.newIntRange("sourceType", 10, 10, true, true);

			QueryParser parser = new QueryParser(Version.LUCENE_35, "appName", analyzer);
			Query originQuery = parser.parse(keyword);
			finalQuery.add(originQuery, BooleanClause.Occur.MUST);
			finalQuery.add(query, BooleanClause.Occur.MUST);

			System.out.println(finalQuery.toString());

			IndexSearcher is = new IndexSearcher(IndexReader.open(dir));

			SortField[] arr = { new SortField("sourceType", SortField.INT, true), SortField.FIELD_SCORE };
			Sort sort = new Sort(arr);

			TopDocs hits = is.search(finalQuery, 10, sort);

			System.out.println("---------------------- total=[" + hits.totalHits + "] ----------------------");

			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println("[" + doc.get("appId") + "]" + doc.get("appName") + "[" + doc.get("sourceType")
						+ "]");
			}

			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
