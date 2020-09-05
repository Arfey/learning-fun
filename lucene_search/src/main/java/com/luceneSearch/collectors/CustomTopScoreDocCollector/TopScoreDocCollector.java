package com.luceneSearch.collectors.CustomTopScoreDocCollector;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Scorable;

import java.io.IOException;
import java.util.ArrayList;

public class TopScoreDocCollector extends TopDocCollector {

    public TopScoreDocCollector(Integer top) {
        super(top);
    }

    public ArrayList<ScoreDoc> scoreDocs() {
        return pq.items;
    }

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
        return new LeafCollector() {
            Scorable scorer;
            @Override
            public void setScorer(Scorable sc) throws IOException {
                scorer = sc;
            }

            @Override
            public void collect(int doc) throws IOException {
                Float score = scorer.score();
                pq.addDoc(new ScoreDoc(doc, score));
                totalHits++;
            }
        };
    }
}
