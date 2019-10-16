package com.niyanchun.query;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Query Demo.
 *
 * @author NiYanchun
 **/
public class QueryDemo {

    /**
     * 搜索的字段
     */
    private static final String SEARCH_FIELD = "contents";

    public static void main(String[] args) throws Exception {
        // 索引保存目录
        final String indexPath = "indices/poems-index";
        // 读取索引
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(indexReader);

        // TermQuery
        termQueryDemo(searcher);

        // BooleanQuery
        booleanQueryDemo(searcher);

        // PhraseQuery
        phraseQueryDemo(searcher);
        phraseQueryWithSlopDemo(searcher);

        // MultiPhraseQuery
        multiPhraseQueryDemo(searcher);

        // PrefixQuery
        prefixQueryDemo(searcher);

        // WildcardQuery
        wildcardQueryDemo(searcher);

        // RegexpQuery
        regexpQueryDemo(searcher);

        // FuzzyQuery
        fuzzyQueryDemo(searcher);

        // TermRangeQuery
        termRangeQueryDemo(searcher);

        // ConstantScoreQuery
        constantScoreQueryDemo(searcher);

        // MatchAllDocsQuery
        matchAllDocsQueryDemo(searcher);
    }

    private static void termQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("TermQuery, search for 'death':");
        TermQuery termQuery = new TermQuery(new Term(SEARCH_FIELD, "death"));

        resultPrint(searcher, termQuery);
    }

    private static void booleanQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("BooleanQuery, must contain 'love' but absolutely not 'seek': ");
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new TermQuery(new Term(SEARCH_FIELD, "love")), BooleanClause.Occur.MUST);
        builder.add(new TermQuery(new Term(SEARCH_FIELD, "seek")), BooleanClause.Occur.MUST_NOT);
        BooleanQuery booleanQuery = builder.build();

        resultPrint(searcher, booleanQuery);
    }

    private static void phraseQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("\nPhraseQuery, search 'love that'");

        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term(SEARCH_FIELD, "love"));
        builder.add(new Term(SEARCH_FIELD, "that"));
        PhraseQuery phraseQueryWithSlop = builder.build();

        resultPrint(searcher, phraseQueryWithSlop);
    }

    private static void phraseQueryWithSlopDemo(IndexSearcher searcher) throws IOException {
        System.out.println("PhraseQuery with slop: 'love <slop> never");
        PhraseQuery phraseQueryWithSlop = new PhraseQuery(1, SEARCH_FIELD, "love", "never");

        resultPrint(searcher, phraseQueryWithSlop);
    }

    private static void multiPhraseQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("MultiPhraseQuery:");

        // On Death: I know not what into my ear
        // Fog: It sits looking over harbor and city
        // 以下的查询可以匹配 "know harbor, know not, over harbor, over not" 4种情况
        MultiPhraseQuery.Builder builder = new MultiPhraseQuery.Builder();
        Term[] termArray1 = new Term[2];
        termArray1[0] = new Term(SEARCH_FIELD, "know");
        termArray1[1] = new Term(SEARCH_FIELD, "over");
        Term[] termArray2 = new Term[2];
        termArray2[0] = new Term(SEARCH_FIELD, "harbor");
        termArray2[1] = new Term(SEARCH_FIELD, "not");
        builder.add(termArray1);
        builder.add(termArray2);
        MultiPhraseQuery multiPhraseQuery = builder.build();

        resultPrint(searcher, multiPhraseQuery);
    }

    private static void prefixQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("PrefixQuery, search terms begin with 'co'");
        PrefixQuery prefixQuery = new PrefixQuery(new Term(SEARCH_FIELD, "co"));

        resultPrint(searcher, prefixQuery);
    }

    private static void wildcardQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("WildcardQuery, search terms 'har*'");
        WildcardQuery wildcardQuery = new WildcardQuery(new Term(SEARCH_FIELD, "har*"));

        resultPrint(searcher, wildcardQuery);
    }

    private static void regexpQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("RegexpQuery, search regexp 'l[ao]*'");
        RegexpQuery regexpQuery = new RegexpQuery(new Term(SEARCH_FIELD, "l[ai].*"));

        resultPrint(searcher, regexpQuery);
    }

    private static void fuzzyQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("FuzzyQuery, search 'remembre'");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(SEARCH_FIELD, "remembre"), 1);

        resultPrint(searcher, fuzzyQuery);
    }

    private static void termRangeQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("TermRangeQuery, search term between 'loa' and 'lov'");
        TermRangeQuery termRangeQuery = new TermRangeQuery(SEARCH_FIELD, new BytesRef("loa"), new BytesRef("lov"), true, false);

        resultPrint(searcher, termRangeQuery);
    }

    private static void constantScoreQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("ConstantScoreQuery:");
        ConstantScoreQuery constantScoreQuery = new ConstantScoreQuery(
                new FuzzyQuery(new Term(SEARCH_FIELD, "remembre"), 1));

        resultPrint(searcher, constantScoreQuery);
    }

    private static void matchAllDocsQueryDemo(IndexSearcher searcher) throws IOException {
        System.out.println("MatchAllDocsQueryDemo:");
        MatchAllDocsQuery matchAllDocsQuery = new MatchAllDocsQuery();

        resultPrint(searcher, matchAllDocsQuery);
    }

    private static void resultPrint(IndexSearcher searcher, Query query) throws IOException {
        TopDocs topDocs = searcher.search(query, 10);
        if (topDocs.totalHits.value == 0) {
            System.out.println("not found!\n");
            return;
        }

        ScoreDoc[] hits = topDocs.scoreDocs;

        System.out.println(topDocs.totalHits.value + " result(s) matched: ");
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            System.out.println("doc=" + hit.doc + " score=" + hit.score + " file: " + doc.get("path"));
        }
        System.out.println();
    }
}
