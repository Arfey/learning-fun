package com.luceneSearch.sorting;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

public class TestSort {
    final static String NAME_FIELD = "name";
    final static String AGE_FIELD = "age";

    public static void main(String[] args) throws IOException {
        // create directory
        RAMDirectory dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(
            dir,
            new IndexWriterConfig()
        );


        // add new documents
        Document doc1 = new Document();
        doc1.add(new StringField(NAME_FIELD, "Tom", Store.NO));
        doc1.add(new StringField(AGE_FIELD, "1", Store.NO));

        writer.addDocument(doc1);

        Document doc2 = new Document();
        doc2.add(new StringField(NAME_FIELD, "Alex", Store.NO));
        doc2.add(new StringField(AGE_FIELD, "3", Store.NO));

        writer.addDocument(doc2);

        writer.commit();

        IndexSearcher search = new IndexSearcher(DirectoryReader.open(dir));

        TopDocs docs = search.search(new MatchAllDocsQuery(), 10);


        TopDocs age_docs = search.search(
            new MatchAllDocsQuery(),
            10,
            new Sort(new SortField(AGE_FIELD, SortField.Type.FLOAT))
        );

    }
}
