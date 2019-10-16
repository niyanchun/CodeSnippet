package com.niyanchun.query;

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

        // 向索引中插入10条document，每个document包含一个field字段，字段值是0~10之间的数字
        for (int i = 0; i < 10; i++) {
            Document doc = new Document();
            Field pointField = new IntPoint("field", i);
            doc.add(pointField);
            writer.addDocument(doc);
        }
        writer.close();

        // 查询
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(indexReader);

        // 查询field字段值在[5, 8]范围内的文档
        Query query = IntPoint.newRangeQuery("field", 5, 8);
        TopDocs topDocs = searcher.search(query, 10);

        if (topDocs.totalHits.value == 0) {
            System.out.println("not found!");
            return;
        }

        ScoreDoc[] hits = topDocs.scoreDocs;

        System.out.println(topDocs.totalHits.value + " result(s) matched: ");
        for (ScoreDoc hit : hits) {
            System.out.println("doc=" + hit.doc + " score=" + hit.score);
        }
    }
}
