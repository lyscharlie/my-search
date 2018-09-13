package my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class QueryFileTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String path = "E://my_search//my_index//queryFileTest";
		String keyword = "时代 QQ";
		boolean useSmart = true;

		List<String> list = new ArrayList<String>();
		list.add("微信2012");
		list.add("微博21");
		list.add("微动力21");
		list.add("微信2013");
		list.add("飞信");
		list.add("短信吖滴偶");
		list.add("哈乐视哈哈");
		list.add("时代QQ同步助手的看法吖滴偶");
		list.add("时代微信阿飞说的看法");
		list.add("迅飞说的吖滴偶看法");
		list.add("时代科技阿飞说的看法");
		list.add("时代微吖滴偶博阿飞说的看法");
		list.add("QQ2013");
		list.add("时代科技吖滴偶手机阿飞说的看法");
		list.add("时代科技乐打手说的看法");
		list.add("时代科技酷我滴偶打手电说的看法");
		list.add("时代科技酷吖滴偶手电筒的看法");
		list.add("时代科技我电筒说的看法");
		list.add("时代科电筒说的看法");
		list.add("时代科技打电优酷筒电视的看法");

		Analyzer analyzer = new IKAnalyzer(useSmart);

		try {
			Directory dir = FSDirectory.open(new File(path));

			IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_35, analyzer));

			Integer i = 1;
			int n = 7;

			for (String item : list) {
				Integer result = i % n;

				System.out.println("Indexing :" + item + "[" + i + "][" + result + "]");

				Document doc = new Document();
				doc.add(new Field("item", item, Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("num", i.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("result", result.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc);

				i++;
			}

			int num = writer.numDocs();
			System.out.println("total:" + num);
			writer.close();

			System.out.println("-------------search---------------");

			IndexSearcher is = new IndexSearcher(IndexReader.open(dir));

			QueryParser parser = new QueryParser(Version.LUCENE_35, "item", analyzer);
			Query querybase = parser.parse(keyword);

			Query query1 = new TermQuery(new Term("num", "18"));
			// query4.setBoost(2.0f);

			BooleanQuery booleanquery1 = new BooleanQuery();
			booleanquery1.add(querybase, BooleanClause.Occur.SHOULD);
			booleanquery1.add(query1, BooleanClause.Occur.MUST_NOT);

			// booleanquery1.setBoost(10.0f);

			Query query2 = new TermQuery(new Term("result", "5"));

			BooleanQuery booleanquery2 = new BooleanQuery();
			booleanquery2.add(querybase, BooleanClause.Occur.MUST);
			booleanquery2.add(query2, BooleanClause.Occur.MUST);

			// booleanquery2.setBoost(1.0f);

			BooleanQuery booleanquery = new BooleanQuery();

			booleanquery.add(booleanquery2, BooleanClause.Occur.SHOULD);
			booleanquery.add(booleanquery1, BooleanClause.Occur.SHOULD);

			// System.out.println("[" + booleanquery.toString() + "]");

			TopDocs hits = is.search(booleanquery1, 30);

			// for (int j = 0; j < 5; j++) {
			// System.out.println(hits.score(j));
			// }

			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				System.out.println("[" + scoreDoc.score + "]" + doc.get("item") + "[" + doc.get("num") + "]["
						+ doc.get("result") + "]");
			}

			is.close();

		} catch (Exception e) {
			e.getStackTrace();
		}

	}

}
