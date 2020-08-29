package com.luceneSearch.collectors;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FilterCollector;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.ScoreMode;

import java.io.IOException;

/**
 * This simple Collector need for naive calculate count of segments in index for query.
 */
class SegmentCountCollector extends FilterCollector {
    int segmentsCount = 0;

    public SegmentCountCollector(Collector in) {
        super(in);
    }

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
        segmentsCount++;
        return super.getLeafCollector(context);
    }

    /** Return how many segments was iterated for current search. */
    final public int getSegmentsCount() {
        return segmentsCount;
    }

    @Override
    public ScoreMode scoreMode() {
        return ScoreMode.COMPLETE_NO_SCORES;
    }

}

