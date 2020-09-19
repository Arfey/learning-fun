package com.luceneSearch.collectors.CustomSort;

import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;


public class TestCustomSort {
    Directory directory;
    public static void main(String[] args) throws IOException {
        TestCustomSort obj = new TestCustomSort();

        try (IndexWriter writer = obj.createIndexWriter()) {

            writer.addDocument(obj.createDocument("restaurant", 10, 11, "restaurant 1"));
            writer.commit();

            writer.addDocument(obj.createDocument("restaurant", 10, 11, "restaurant 2"));
            writer.commit();

            writer.addDocument(obj.createDocument("restaurant", 5, 6, "restaurant 3"));
            writer.commit();

            writer.addDocument(obj.createDocument("restaurant", 10, 11, "restaurant 4"));
            writer.commit();

            writer.addDocument(obj.createDocument("restaurant", 5, 5, "restaurant 5"));
            writer.commit();
        }

        obj.testSearch();
    }

    public IndexWriter createIndexWriter() throws IOException {
        directory = new RAMDirectory();
        IndexWriter writer = new IndexWriter(
                directory,
                new IndexWriterConfig()
        );

        return writer;
    }


    public Document createDocument(String type, Integer x, Integer y, String name) {
        Document doc = new Document();

        doc.add(new StringField(Constants.DOC_TYPE, type, Field.Store.YES));
        doc.add(new StringField(Constants.DOC_NAME, name, Field.Store.YES));


        byte[] coordinates = {Integer.valueOf(x).byteValue(), Integer.valueOf(y).byteValue()};

        doc.add(new BinaryDocValuesField(Constants.DOC_COORDINATES, new BytesRef(coordinates)));

        return doc;
    }

    public void testSearch() throws IOException {
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
        Query query = new TermQuery(new Term(Constants.DOC_TYPE, "restaurant"));
        Sort sort = new Sort(new SortField(Constants.DOC_COORDINATES, new DistanceSortComparatorSource(0, 0)));

        TopDocs result = searcher.search(query, 3, sort, true);

        for (ScoreDoc doc: result.scoreDocs) {
            Document document = searcher.doc(doc.doc);
            System.out.println("id: " + doc.doc + " name: " +document.getField(Constants.DOC_NAME).stringValue());
        }
    }

}
