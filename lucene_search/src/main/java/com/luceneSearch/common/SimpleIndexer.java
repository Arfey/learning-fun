package com.luceneSearch.common;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class SimpleIndexer {
    // Example: http://www.lucenetutorial.com/sample-apps/textfileindexer-java.html

    private IndexWriter writer;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("Start indexing...");

        SimpleIndexer indexer = new SimpleIndexer();

        try {
            indexer.indexing();
        } finally {
            System.out.println("Finished at: " + (System.currentTimeMillis() - startTime) + "ms.");
            System.out.println("Indexed " + indexer.writer.getDocStats().numDocs + " documents.");

            if (indexer.writer.isOpen()) {
                indexer.writer.close();
            }
        }
    }

    public SimpleIndexer() throws IOException {
        createWriter();
    }

    public void createWriter() throws IOException {
        FSDirectory dir = FSDirectory.open(new File(Constants.INDEX_DIR).toPath());
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        writer = new IndexWriter(dir, config);
    }

    public void indexing() throws IOException {
        for (File file: new File(Constants.DATA_DIR).listFiles()) {
            Document doc = createDocument(file);
            writer.addDocument(doc);
        }
    }

    public Document createDocument(File file) throws IOException {
        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        Document doc = new Document();

        doc.add(new StringField(DocumentFields.DOC_NAME, file.getName(), Field.Store.YES));
        doc.add(new StringField(DocumentFields.DOC_PATH, file.getPath(), Field.Store.YES));
        doc.add(new TextField(DocumentFields.DOC_CONTENT, content, Field.Store.YES));

        return doc;
    }
}
