package my;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IKAnalyzerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// String str = "IK Analyzer是一个开源的，基于java语言" +
		// "开发的轻量级的中文分词工具包。从2006年12月推出1.0版开始， "
		// + "IKAnalyzer已经推出了4个大版本。最初，它是以开源项目Luence为" +
		// "应用主体的，结合词典分词和文法分析算法的中文分词组件。从3.0版"
		// + "本开始，IK发展为面向Java的公用分词组件，独立于Lucene项目，同时" + "提供了对Lucene的默认优化实现。";

		String str = "开发的轻量级的中文分词工具包";

		boolean useSmart = false;

		// 基于Lucene实现
		Analyzer analyzer = new IKAnalyzer(useSmart);// true智能切分
		StringReader reader = new StringReader(str);
		TokenStream ts = analyzer.tokenStream("", reader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		try {
			while (ts.incrementToken()) {
				System.out.print(term.toString() + "|");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader.close();
		System.out.println();

		// 独立Lucene实现
		StringReader re = new StringReader(str);
		IKSegmenter ik = new IKSegmenter(re, useSmart);
		Lexeme lex = null;
		try {
			while ((lex = ik.next()) != null) {
				System.out.print(lex.getLexemeText() + "|");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		analyzer.close();

	}

}
