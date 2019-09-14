package com.niyanchun;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

/**
 * Show Term Vector.
 *
 * @author NiYanchun
 **/
public class TermVectorShow {

    public static void main(String[] args) throws Exception {
        // 构建索引
        final String indexPath = "indices/tv-show";
        Directory indexDir = FSDirectory.open(Paths.get(indexPath));

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(indexDir, iwc);

        String sentence = "a good student, a gifted student";
        // 保存词向量的所有相关信息
        FieldType fieldType = new FieldType();
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorOffsets(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        Field field = new Field("content", sentence, fieldType);
        Document document = new Document();
        document.add(field);
        writer.addDocument(document);
        writer.close();

        // 从索引读取Term Vector信息
        IndexReader indexReader = DirectoryReader.open(indexDir);
        Terms termVector = indexReader.getTermVector(0, "content");
        TermsEnum termIter = termVector.iterator();
        while (termIter.next() != null) {
            PostingsEnum postingsEnum = termIter.postings(null, PostingsEnum.ALL);
            while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                int freq = postingsEnum.freq();
                System.out.printf("term: %s, freq: %d,", termIter.term().utf8ToString(), freq);
                while (freq > 0) {
                    System.out.printf(" nextPosition: %d,", postingsEnum.nextPosition());
                    System.out.printf(" startOffset: %d, endOffset: %d",
                            postingsEnum.startOffset(), postingsEnum.endOffset());
                    freq--;
                }
                System.out.println();
            }
        }
    }
}
