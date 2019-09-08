package com.niyanchun;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * Minimal Search Files code
 **/
public class SearchFilesMinimal {

    public static void main(String[] args) throws Exception {
        // 索引保存目录
        final String indexPath = "/Users/allan/Git/allan/github/CodeSnippet/Java/lucene-learning/indices/poems-index";
        // 搜索的字段
        final String searchField = "contents";

        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new StandardAnalyzer();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        QueryParser queryParser = new QueryParser(searchField, analyzer);
        while (true) {
            System.out.println("Enter query: ");

            String input = in.readLine();
            if (input == null) {
                break;
            }

            input = input.trim();
            if (input.length() == 0) {
                break;
            }

            Query query = queryParser.parse(input);
            System.out.println("searching for: " + query.toString(searchField));

            TopDocs results = searcher.search(query, 10);
            ScoreDoc[] hits = results.scoreDocs;
            if (results.totalHits.value == 0) {
                System.out.println("no result matched!");
                continue;
            }

            System.out.println(results.totalHits.value + " results matched: ");
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                System.out.println("doc=" + hit.doc + " score=" + hit.score + " file: " + doc.get("path"));
            }
        }
    }
}
