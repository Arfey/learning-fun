package com.luceneSearch.collectors.CustomTopScoreDocCollector;

import com.luceneSearch.collectors.TestSegmentCountCollector;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;


public class TestCustomScoreCollector extends TestSegmentCountCollector {
    public static void main(String[] args) throws IOException {
        TopScoreDocCollector collector = new TopScoreDocCollector(10);

        IndexSearcher searcher = getIndexSearcher();

        searcher.search(new TermQuery(new Term("content", "and")), collector);

        System.out.println("Top docs search: len - " + collector.scoreDocs().size() + " total: " + collector.totalHits);

        for (ScoreDoc doc: collector.scoreDocs()) {
            System.out.println("Doc id: " + doc.doc + " Score: " + doc.score);
        }
    }
}
