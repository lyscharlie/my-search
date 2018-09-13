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
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class InnerChannelTest {

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
					text = ";2_;";
				} else if (result == 1) {
					text = ";2_;2_zjyd_;";
				} else if (result == 2) {
					text = ";2_;2_sdyd_;";
				} else if (result == 3) {
					text = ";2_zjyd_0572_;";
				} else if (result == 4) {
					text = ";2_zjyd_0571_;";
				} else if (result == 5){
					text = ";2_sdyd_;";
				}else{
					text = ";3_;";
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

			BooleanQuery finalQuery = new BooleanQuery();

			finalQuery.add(querybase, BooleanClause.Occur.MUST);
			
			BooleanQuery channelQuery = new BooleanQuery();

			String[] arr = channel.split("_");

			for (int i = 0; i < arr.length; i++) {
				StringBuffer key = new StringBuffer();
				for (int j = 0; j <= i; j++) {
					key.append(arr[j]).append("_");
				}
				System.out.println("key=" + key.toString());
				Query querybase1 = parser1.parse(key.toString());
				channelQuery.add(querybase1, BooleanClause.Occur.SHOULD);
			}
			
			finalQuery.add(channelQuery, BooleanClause.Occur.MUST);

			System.out.println(finalQuery.toString());

			TopDocs hits = is.search(finalQuery, 30);


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
