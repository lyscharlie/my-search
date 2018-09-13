package my;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
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

public class BlackChannelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String keyword = "中国";
		String channel = "2_zjyd_0571_0000_123123";

		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i <= 20; i++) {
			list.add(i);
		}

		Analyzer analyzer = new IKAnalyzer();
		Directory dir = new RAMDirectory();

		try {

			IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_35, analyzer));

			for (Integer item : list) {
				Integer result = item % 7;

				System.out.print("Indexing :" + item + "[" + item + "][" + result + "]");

				String text = "";

				if (result == 0) {
					text = ";2_zjyd_0571_;2_sdyd_0578_0571111_;";
				} else if (result == 1) {
					text = ";2_sdyd_0578_0571111_;";
				} else if (result == 2) {
					text = ";2_zjyd_;";
				} else if (result == 3) {
					text = ";2_zjyd_0572_;";
				} else if (result == 4) {
					text = ";2_;";
				}
				System.out.println("[" + text + "]");

				Document doc = new Document();
				doc.add(new Field("name", "中国人", Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("item", item.toString(), Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("result", result.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("text", text, Field.Store.YES, Field.Index.ANALYZED));
				writer.addDocument(doc);
			}

			int num = writer.numDocs();
			System.out.println("total:" + num);
			writer.close();

			System.out.println("-------------search---------------");

			IndexSearcher is = new IndexSearcher(IndexReader.open(dir));

			QueryParser parser = new QueryParser(Version.LUCENE_35, "name", analyzer);
			Query querybase = parser.parse(keyword);

			QueryParser parser1 = new QueryParser(Version.LUCENE_35, "text", new StandardAnalyzer(Version.LUCENE_35));
			// QueryParser parser1 = new QueryParser(Version.LUCENE_35, "text",
			// analyzer);
			// Query querybase1 = parser1.parse("2_zjyd_0571_");
			// Query querybase2 = parser1.parse("2_zjyd_");

			Query query2 = new TermQuery(new Term("result", "3"));
			Query query3 = new TermQuery(new Term("result", "1"));

			BooleanQuery booleanquery = new BooleanQuery();

			booleanquery.add(querybase, BooleanClause.Occur.MUST);

			String[] arr = channel.split("_");
//			if (arr.length <= 2) {
//				Query querybase1 = parser1.parse(channel);
//				booleanquery.add(querybase1, BooleanClause.Occur.MUST_NOT);
//			} else {
				for (int i = 0; i < arr.length; i++) {
					// if (i < 1) {
					// continue;
					// }
					StringBuffer key = new StringBuffer();
					for (int j = 0; j <= i; j++) {
						key.append(arr[j]).append("_");
					}
					System.out.println("key=" + key.toString());
					Query querybase1 = parser1.parse(key.toString());
					booleanquery.add(querybase1, BooleanClause.Occur.MUST_NOT);
				}
//			}

			// booleanquery.add(querybase1, BooleanClause.Occur.MUST_NOT);
			// booleanquery.add(querybase2, BooleanClause.Occur.MUST_NOT);
			
			
			booleanquery.add(query2, BooleanClause.Occur.MUST);
//			booleanquery.add(query3, BooleanClause.Occur.SHOULD);

			System.out.println(booleanquery.toString());

			TopDocs hits = is.search(booleanquery, 30);

			// for (int j = 0; j < 5; j++) {
			// System.out.println(hits.score(j));
			// }

			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println("[" + scoreDoc.score + "]" + doc.get("name") + doc.get("item") + "["
						+ doc.get("result") + "][" + doc.get("text") + "]");
			}

			is.close();

		} catch (Exception e) {
			e.getStackTrace();
		}

	}

}
