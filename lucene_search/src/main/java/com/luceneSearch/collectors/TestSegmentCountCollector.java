package com.luceneSearch.collectors;

import com.luceneSearch.common.Constants;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

import static org.apache.lucene.index.DirectoryReader.open;

public class TestSegmentCountCollector {
    final public static Query query = new MatchAllDocsQuery();

    public static IndexSearcher getIndexSearcher() throws IOException {
        // create index searcher
        Directory index = FSDirectory.open(new File(Constants.INDEX_DIR).toPath());
        IndexReader reader = open(index);

        IndexSearcher searcher = new IndexSearcher(reader);

        return searcher;
    }

    public static void main(String[] args) throws IOException {
        SegmentCountCollector collector = new SegmentCountCollector(TopScoreDocCollector.create(10, 0));

        IndexSearcher searcher = getIndexSearcher();

        searcher.search(query, collector);
        System.out.println("Count of segments: " + collector.getSegmentsCount());
    }
}
