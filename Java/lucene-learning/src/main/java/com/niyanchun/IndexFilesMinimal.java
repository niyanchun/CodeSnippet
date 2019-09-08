package com.niyanchun;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Minimal Index Files code.
 **/
public class IndexFilesMinimal {

    public static void main(String[] args) throws Exception {
        // 原数据存放路径
        final String docsPath = "/Users/allan/Git/allan/github/CodeSnippet/Java/lucene-learning/data/poems";
        // 索引保存目录
        final String indexPath = "/Users/allan/Git/allan/github/CodeSnippet/Java/lucene-learning/indices/poems-index";

        final Path docDir = Paths.get(docsPath);
        Directory indexDir = FSDirectory.open(Paths.get(indexPath));
        // 使用标准分析器
        Analyzer analyzer = new StandardAnalyzer();

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        // 每次都重新创建索引
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // 创建IndexWriter用于写索引
        IndexWriter writer = new IndexWriter(indexDir, iwc);

        System.out.println("index start...");
        // 遍历数据目录，对目录下的每个文件进行索引
        Files.walkFileTree(docDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                indexDoc(writer, file);
                return FileVisitResult.CONTINUE;
            }
        });
        writer.close();

        System.out.println("index ends.");
    }

    private static void indexDoc(IndexWriter writer, Path file) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            System.out.println("indexing file " + file);
            // 创建文档对象
            Document doc = new Document();

            // 将文件绝对路径加入到文档中
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);
            // 将文件内容加到文档中
            Field contentsField = new TextField("contents", new BufferedReader(new InputStreamReader(stream)));
            doc.add(contentsField);

            // 将文档写入索引中
            writer.addDocument(doc);
        }
    }
}
