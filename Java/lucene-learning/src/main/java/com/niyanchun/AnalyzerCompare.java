package com.niyanchun;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Compare Lucene Internal Analyzers.
 *
 * @author NiYanchun
 **/
public class AnalyzerCompare {

    private static final Analyzer[] ANALYZERS = new Analyzer[]{
            new WhitespaceAnalyzer(),
            new KeywordAnalyzer(),
            new SimpleAnalyzer(),
            // 标准分词器会处理停用词，但默认其停用词库为空，这里我们使用英文的停用词
            new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet())
    };

    public static void main(String[] args) throws Exception {
        String content = "My name is Ni Yanchun, I'm 28 years old. You can contact me with the email niyanchun@outlook.com";
        System.out.println("原始数据:\n" + content + "\n\n分析结果：");
        for (Analyzer analyzer : ANALYZERS) {
            showTerms(analyzer, content);
        }
    }

    private static void showTerms(Analyzer analyzer, String content) throws IOException {

        try (TokenStream tokenStream = analyzer.tokenStream("content", content)) {
            StringBuilder sb = new StringBuilder();
            AtomicInteger tokenNum = new AtomicInteger();
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokenStream.reflectWith(((attClass, key, value) -> {
                    if ("term".equals(key)) {
                        tokenNum.getAndIncrement();
                        sb.append("\"").append(value).append("\", ");
                    }
                }));
            }
            tokenStream.end();

            System.out.println(analyzer.getClass().getSimpleName() + ":\n"
                    + tokenNum + " tokens: ["
                    + sb.toString().substring(0, sb.toString().length() - 2) + "]");
        }
    }
}
