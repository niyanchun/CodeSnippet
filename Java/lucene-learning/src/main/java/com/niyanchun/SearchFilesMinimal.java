package com.niyanchun;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
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
        final String indexPath = "indices/poems-index";
        // 搜索的字段
        final String searchField = "contents";

        // 从索引目录读取索引信息
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        // 创建索引查询对象
        IndexSearcher searcher = new IndexSearcher(indexReader);
        // 使用标准分词器
        Analyzer analyzer = new StandardAnalyzer();

        // 从终端获取查询语句
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        // 创建查询语句解析对象
        QueryParser queryParser = new QueryParser(searchField, analyzer);
        while (true) {
            System.out.println("\nEnter query: ");

            String input = in.readLine();
            if (input == null) {
                break;
            }

            input = input.trim();
            if (input.length() == 0) {
                break;
            }

            // 解析用户输入的查询语句：build query
            Query query = queryParser.parse(input);
            System.out.println("searching for: " + query.toString(searchField));
            // 查询
            TopDocs results = searcher.search(query, 10);
            ScoreDoc[] hits = results.scoreDocs;
            if (results.totalHits.value == 0) {
                System.out.println("no result matched!");
                continue;
            }

            // 输出匹配到的结果
            System.out.println(results.totalHits.value + " results matched: ");
            for (ScoreDoc hit : hits) {
                Document doc = searcher.doc(hit.doc);
                System.out.println("doc=" + hit.doc + " score=" + hit.score + " file: " + doc.get("path"));
            }
        }
    }
}
