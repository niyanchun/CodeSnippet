package com.niyanchun;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;

/**
 * Compare Lucene Internal Analyzers.
 *
 * @author NiYanchun
 **/
public class AnalyzerCompare {
    private static final String[] CONTENTS = new String[]{
            "The quick brown fox jumped over the lazy dog",
            "You can contact me with the email niyanchun@outlook.com"
    };

    private static final Analyzer[] ANALYZERS = new Analyzer[]{
            new WhitespaceAnalyzer(),
            new KeywordAnalyzer(),
            new SimpleAnalyzer(),
            new StandardAnalyzer(),
            // 标准分词器会处理停用词，但默认其停用词库为空，这里我们使用英文的停用词
            new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet())
    };

    public static void main(String[] args) throws Exception {

        for (String content : CONTENTS) {
            System.out.println("content: " + content);
            for (Analyzer analyzer : ANALYZERS) {
                showTerms(analyzer, content);
            }
            System.out.println();
        }
    }

    private static void showTerms(Analyzer analyzer, String content) throws IOException {

        try (TokenStream tokenStream = analyzer.tokenStream("content", content)) {
            StringBuilder sb = new StringBuilder();
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokenStream.reflectWith(((attClass, key, value) -> {
                    if ("term".equals(key)) {
                        sb.append(value).append(", ");
                    }
                }));
            }
            tokenStream.end();

            System.out.println(analyzer.getClass().getSimpleName() + ": ["
                    + sb.toString().substring(0, sb.toString().length() - 2) + "]");
        }
    }
}
