package my.multi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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

	public static void main(String[] args) {
		String indexDir1 = "E://my_search//my_index/iktest1";
		String indexDir2 = "E://my_search//my_index/iktest2";

		List<String> list1 = new ArrayList<String>();
		list1.add("微信2012");
		list1.add("微博2012");
		list1.add("微信2013");
		list1.add("微博2013");

		List<String> list2 = new ArrayList<String>();
		list2.add("微信2012");
		list2.add("微博2012");
		list2.add("微信2013");
		list2.add("微博2013");

		createIndex(indexDir1, list1, 1);
		createIndex(indexDir2, list2, 2);
	}

	private static void createIndex(String indexDir, List<String> list, int f) {
		try {
			boolean useSmart = false;

			Analyzer analyzer = new IKAnalyzer(useSmart);

			Directory dir = FSDirectory.open(new File(indexDir));
			// Directory dir = new RAMDirectory();

			IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_35, analyzer));

			int i = 1;
			for (String item : list) {
				System.out.println("Indexing :" + item);
				Document doc = new Document();
				doc.add(new Field("num", f + "" + i, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("text", item, Field.Store.YES, Field.Index.ANALYZED));
				if (StringUtils.startsWith(item, "微信")) {
					doc.add(new Field("pkg", "com.weixin", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
				} else {
					doc.add(new Field("pkg", "com.weibo", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
				}
				writer.addDocument(doc);
				i++;
			}
			int num = writer.numDocs();
			System.out.println("total:" + num);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
