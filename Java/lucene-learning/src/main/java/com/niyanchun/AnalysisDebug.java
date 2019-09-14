package com.niyanchun;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

/**
 * Debug Analysis Process.
 *
 * @author NiYanchun
 **/
public class AnalysisDebug {

    public static void main(String[] args) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        String sentence = "a good student, a gifted student.";
        try (TokenStream tokenStream = analyzer.tokenStream("sentence", sentence)) {
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                System.out.println("token: " + tokenStream.reflectAsString(false));
            }
            tokenStream.end();
        }
    }
}
