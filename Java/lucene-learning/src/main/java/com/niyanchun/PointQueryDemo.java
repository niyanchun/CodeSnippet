package com.niyanchun;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

/**
 * Point Query Demo.
 *
 * @author NiYanchun
 **/
public class PointQueryDemo {

    public static void main(String[] args) throws Exception {
        // 索引保存目录
        final String indexPath = "indices/point-index";
        Directory indexDir = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(indexDir, iwc);

        for (int i = 0; i < 10; i++) {
            Document doc = new Document();
            Field pointField = new IntPoint("field", i);
            doc.add(pointField);
            writer.addDocument(doc);
        }
        writer.close();

        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(indexReader);

        Query query = IntPoint.newRangeQuery("field", 5, 8);
        TopDocs topDocs = searcher.search(query, 10);

        if (topDocs.totalHits.value == 0) {
            System.out.println("not found!");
            return;
        }

        ScoreDoc[] hits = topDocs.scoreDocs;

        System.out.println(topDocs.totalHits.value + " result(s) matched: ");
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            System.out.println("doc=" + hit.doc + " score=" + hit.score);
        }
    }
}
