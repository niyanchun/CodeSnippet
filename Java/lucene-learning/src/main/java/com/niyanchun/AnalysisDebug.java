package com.niyanchun;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * Debug Analysis Process.
 *
 * @author NiYanchun
 **/
public class AnalysisDebug {

    public static void main(String[] args) throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
//        Analyzer analyzer = new StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
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
