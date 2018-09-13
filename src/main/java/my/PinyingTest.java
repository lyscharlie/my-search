package my;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class PinyingTest {

	public static void main(String[] args) {
		try {
			String keyword = "weix";

			String indexDir = "E://indexes//pcsuit_1";
			Directory dir = FSDirectory.open(new File(indexDir));
			IndexSearcher is = new IndexSearcher(IndexReader.open(dir));

			Term t = new Term("pinyinName", keyword);
			PhraseQuery query = new PhraseQuery();
			query.add(t);

			System.out.println("-------------search---------------");

			System.out.println("query:" + query.toString());

			TopDocs hits = is.search(query, 10);

			System.out.println("total=[" + hits.totalHits + "]");

			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println("[" + scoreDoc.score + "]" + doc.get("appName") + "[" + doc.get("pinyinName") + "]");
			}

			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
