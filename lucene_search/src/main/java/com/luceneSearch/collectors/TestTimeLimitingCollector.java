package com.luceneSearch.collectors;

import com.luceneSearch.common.Constants;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TimeLimitingCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

import static org.apache.lucene.index.DirectoryReader.open;

public class TestTimeLimitingCollector {

    final public static Query query = new MatchAllDocsQuery();

    public static IndexSearcher getIndexSearcher() throws IOException {
        // create index searcher
        Directory index = FSDirectory.open(new File(Constants.INDEX_DIR).toPath());
        IndexReader reader = open(index);

        IndexSearcher searcher = new IndexSearcher(reader);

        return searcher;
    }

    /** In this test we check correct of work standard behavior of TopScoreDocCollector. TopScoreDocCollector is
     * default collector for search method.
     */
    public static void testOfCorrectingSearch() throws IOException  {

        IndexSearcher searcher = getIndexSearcher();

        TopDocs res = searcher.search(query, 10);

        assert res.scoreDocs.length == 10: "Result must be 10.";
    }

    /**
     * In this test we check of correcting work for TimeLimitingCollector. For that we set limit of time to zero for
     * check error.
     */
    public static void testExceptionForLimitCollector() throws IOException  {
        // search with limit
        IndexSearcher searcher = getIndexSearcher();

        TopScoreDocCollector collector = TopScoreDocCollector.create(10, 0);

        TimeLimitingCollector limitCollector = new TimeLimitingCollector(
                collector,
                TimeLimitingCollector.getGlobalCounter(),
                0
        );

        try {
            searcher.search(
                    query,
                    limitCollector
            );
        } catch (TimeLimitingCollector.TimeExceededException e) {
            // do nothing
        }

        assert collector.getTotalHits() == 0 : "Result must be equal to zero because was an error.";
    }

    public static void main(String[] args) throws IOException {
        testOfCorrectingSearch();
        testExceptionForLimitCollector();
    }
}
