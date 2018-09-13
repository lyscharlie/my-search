package my.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Index {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String indexDir = "E://my_search//my_index/iktest";
		boolean useSmart = false;

		Analyzer analyzer = new IKAnalyzer(useSmart);

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

		try {
			Directory dir = FSDirectory.open(new File(indexDir));
			// Directory dir = new RAMDirectory();

			IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_35, analyzer));

			for (String item : list) {
				System.out.println("Indexing :" + item);
				Document doc = new Document();
				doc.add(new Field("text", item, Field.Store.YES, Field.Index.ANALYZED));
				writer.addDocument(doc);
			}
			int num = writer.numDocs();
			System.out.println("total:" + num);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
