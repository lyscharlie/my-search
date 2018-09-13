package my;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.dic.Hit;

public class IKDictionary {

	public static void main(String[] args) throws IOException {
		String s = "2012年高级会计实务基础班";
		Configuration cfg = DefaultConfig.getInstance();
		cfg.setUseSmart(true);
		Dictionary.initial(cfg);

		Dictionary dictionary = Dictionary.getSingleton();
		List<String> words = new ArrayList<String>();
		words.add("基础班");
		words.add("高级会计实务");
		dictionary.addWords(words);

		System.out.println(cfg.getMainDictionary());
		System.out.println(cfg.getQuantifierDicionary());

		Hit hit = dictionary.matchInMainDict("基础班".toCharArray());
		System.out.println(hit.isMatch());

		System.out.println(queryWords(s));

	}

	/**
	 * IK 分词
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public static List<String> queryWords(String query) throws IOException {
		List<String> list = new ArrayList<String>();
		StringReader input = new StringReader(query.trim());

		IKSegmenter ikSeg = new IKSegmenter(input, true);// true　用智能分词　，false细粒度
		for (Lexeme lexeme = ikSeg.next(); lexeme != null; lexeme = ikSeg.next()) {
			list.add(lexeme.getLexemeText());
		}

		return list;
	}
}
