package my.multi;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DuplicateFilter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Search {

	public static void main(String[] args) {
		String indexDir1 = "E://my_search//my_index/iktest1";
		String indexDir2 = "E://my_search//my_index/iktest2";

		String keyword = "微信";

		boolean useSmart = true;
		Analyzer analyzer = new IKAnalyzer(useSmart);

		try {
			Directory dir1 = FSDirectory.open(new File(indexDir1));
			Directory dir2 = FSDirectory.open(new File(indexDir2));

			System.out.println("-------------search---------------");

			IndexReader is1 = IndexReader.open(dir1);
			IndexReader is2 = IndexReader.open(dir2);

			MultiReader reader = new MultiReader(is1, is2);
			IndexSearcher is = new IndexSearcher(reader);

			// ParallelReader parallelReader = new ParallelReader();
			// parallelReader.add(is1);
			// parallelReader.add(is2);
			//
			// IndexSearcher is = new IndexSearcher(parallelReader);

			QueryParser parser = new QueryParser(Version.LUCENE_35, "text", analyzer);
			Query query = parser.parse(keyword);

			System.out.println("query:" + query);

			SortField[] arr = { new SortField("num", SortField.INT, true), SortField.FIELD_SCORE };
			Sort sort = new Sort(arr);

			DuplicateFilter filter = new DuplicateFilter("pkg");
			filter.setProcessingMode(DuplicateFilter.KM_USE_FIRST_OCCURRENCE);
			filter.setKeepMode(DuplicateFilter.PM_FAST_INVALIDATION);

			 TopDocs hits = is.search(query, filter, 10, sort);
			// TopDocs hits = is.search(query, 10, sort);
			// TopDocs hits = is.search(query, 10);

			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println(doc.get("num") + "[" + doc.get("text") + "]");
			}

			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
