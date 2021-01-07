package com.luceneSearch.TermVectorSimilarity;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class TestTermVectorSimilarity {

    // learning current article
    // http://sujitpal.blogspot.com/2011/10/computing-document-similarity-using.html
    // http://makble.com/what-is-term-vector-in-lucene

    private IndexWriter writer;
    private IndexReader reader;
    private IndexSearcher searcher;
    private RAMDirectory dir;
    private final String CONTENT_FIELD = "content";

    private String[] documents = {
        "Mitral valve surgery - minimally invasive (31825)",
        "Mitral valve surgery - open (31835)",
        "Laryngectomy (31706)",
        "Shaken baby syndrome (30)",
    };

    public static void main(String[] args) throws IOException {
        TestTermVectorSimilarity obj = new TestTermVectorSimilarity();

        System.out.println("\nDocuments: \n");

        for (String text : obj.documents) {
            System.out.println(text);
        }

        System.out.println("\n");

        obj.createIndexWriter();
        obj.createDocuments();
        obj.createReader();
        obj.createSearcher();

        TopDocs topDocs = obj.searcher.search(new MatchAllDocsQuery(), 10);

        Map<String, Integer> firstDoc = null;


        for (ScoreDoc doc: topDocs.scoreDocs) {
            obj.reader.document(doc.doc);

            if (firstDoc == null) {
                firstDoc = obj.getTermsMapFromDoc(doc.doc);
            }

            Map<String, Integer> otherDoc = obj.getTermsMapFromDoc(doc.doc);
            System.out.println(obj.calculateSimilarity(firstDoc, otherDoc));
        }

    }

    private void createIndexWriter() throws IOException {
        dir = new RAMDirectory();
        writer = new IndexWriter(dir, new IndexWriterConfig());
    }

    public void createReader() throws IOException {
        reader = DirectoryReader.open(dir);
    }

    public void createSearcher() {
        searcher = new IndexSearcher(reader);
    }

    private Map<String, Integer> getTermsMapFromDoc(Integer docId) throws IOException {
        Terms terms = reader.getTermVector(docId, CONTENT_FIELD);
        Map<String, Integer> hash = new HashMap<>();

        TermsEnum termsEnum = terms.iterator();
        BytesRef bytesRef = termsEnum.next();

        while (bytesRef != null) {
            hash.put(bytesRef.utf8ToString(), termsEnum.docFreq());
            bytesRef = termsEnum.next();
        }

        return hash;
    }

    private double calculateSimilarity(Map<String, Integer> obj1, Map<String, Integer> obj2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String key : obj1.keySet()) {

            Integer val1 = obj1.getOrDefault(key, 0);
            Integer val2 = obj2.getOrDefault(key, 0);

            dotProduct += val1 * val2;
            normA += Math.pow(val1, 2);
            normB += Math.pow(val2, 2);
        }

        if (normA == 0 || normB == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private void createDocuments() throws IOException {
        for (String text: documents) {

            Document doc = new Document();
            doc.add(new TextFieldWithTermVector(CONTENT_FIELD, text));

            writer.addDocument(doc);
        }

        writer.commit();
    }
}
