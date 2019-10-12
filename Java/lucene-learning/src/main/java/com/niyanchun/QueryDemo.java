package com.niyanchun;

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

        // Term Query
        System.out.println("TermQuery, search for 'death':");
        resultPrint(searcher, searcher.search(getTermQuery("death"), 10));
        // Boolean Query
        System.out.println("\nBooleanQuery, must contain 'love' but absolutely not 'seek': ");
        resultPrint(searcher, searcher.search(getBooleanQuery(), 10));
        // Phrase Query
        System.out.println("\nPhraseQuery, search 'love that'");
        resultPrint(searcher, searcher.search(getPhraseQuery(), 10));
        System.out.println("\nPhraseQuery with slop: 'love <slop> never");
        resultPrint(searcher, searcher.search(getPhraseQueryWithSlop(), 10));
        System.out.println("\nMultiPhraseQuery");
        resultPrint(searcher, searcher.search(getMultiPhraseQuery(), 10));
        System.out.println("\nPrefixQuery, search terms begin with 'co'");
        resultPrint(searcher, searcher.search(getPrefixQuery(), 10));
        System.out.println("\nWildcardQuery, search terms 'har*'");
        resultPrint(searcher, searcher.search(getWildcardQuery(), 10));
        System.out.println("\nRegexpQuery, search regexp 'l[ao]*'");
        resultPrint(searcher, searcher.search(getRegexpQuery(), 10));
        System.out.println("\nFuzzyQuery, search 'remembre'");
        resultPrint(searcher, searcher.search(getFuzzyQuery(), 10));
        System.out.println("\nTermRangeQuery, search term between 'loa' and 'lov'");
        resultPrint(searcher, searcher.search(getTermRangeQuery(), 10));
        System.out.println("\nConstantScoreQuery:");
        resultPrint(searcher, searcher.search(getConstantScoreQuery(), 10));
    }

    private static Query getTermQuery(String keyword) {
        return new TermQuery(new Term(SEARCH_FIELD, keyword));
    }

    private static Query getBooleanQuery() {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(getTermQuery("love"), BooleanClause.Occur.MUST);
        builder.add(getTermQuery("seek"), BooleanClause.Occur.MUST_NOT);

        return builder.build();
    }

    private static Query getPhraseQuery() {
        System.out.println("Phrase Query search");

        // 通过以下的构造方式还可以搜索不同的term，即不同的字段
        //PhraseQuery.Builder builder = new PhraseQuery.Builder();
        //builder.add(new Term(SEARCH_FIELD, "love"));
        //builder.add(new Term(SEARCH_FIELD, "that"));
        //builder.build();

        return new PhraseQuery(SEARCH_FIELD, "love", "that");
    }

    private static Query getPhraseQueryWithSlop() {
        return new PhraseQuery(1, SEARCH_FIELD, "love", "never");
    }

    private static Query getMultiPhraseQuery() {
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

        return builder.build();
    }

    private static Query getPrefixQuery() {
        return new PrefixQuery(new Term(SEARCH_FIELD, "co"));
    }

    private static Query getWildcardQuery() {
        return new WildcardQuery(new Term(SEARCH_FIELD, "har*"));
    }

    private static Query getRegexpQuery() {
        return new RegexpQuery(new Term(SEARCH_FIELD, "l[ai].*"));
    }

    private static Query getFuzzyQuery() {
        return new FuzzyQuery(new Term(SEARCH_FIELD, "remembre"), 1);
    }

    private static Query getTermRangeQuery() {
        return new TermRangeQuery(SEARCH_FIELD, new BytesRef("loa"), new BytesRef("lov"), true, false);
    }

    private static Query getConstantScoreQuery() {
        return new ConstantScoreQuery(getFuzzyQuery());
    }

    private static void resultPrint(IndexSearcher searcher, TopDocs topDocs) throws IOException {
        if (topDocs.totalHits.value == 0) {
            System.out.println("not found!");
            return;
        }

        ScoreDoc[] hits = topDocs.scoreDocs;

        System.out.println(topDocs.totalHits.value + " result(s) matched: ");
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            System.out.println("doc=" + hit.doc + " score=" + hit.score + " file: " + doc.get("path"));
        }
    }
}
