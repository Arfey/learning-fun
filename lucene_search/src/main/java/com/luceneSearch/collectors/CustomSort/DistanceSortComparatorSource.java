package com.luceneSearch.collectors.CustomSort;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.search.Scorable;

import java.io.IOException;


/**
 * This class need to sort documents by how close documents is by geolocation:
 *
 *     Sort sort = new Sort(new SortField("name_of_field", new DistanceSortComparatorSource(0, 0)));
 *     TopDocs result = searcher.search(query, 10, sort);
 *
 *  x - x coordinate
 *  y - y coordinate
 *
 */
public class DistanceSortComparatorSource extends FieldComparatorSource {
    private Integer x;
    private Integer y;

    public DistanceSortComparatorSource(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) {
        return new DistanceSortComparatorField(fieldname, numHits, x, y);
    }

    private class DistanceSortComparatorField extends FieldComparator<Float> implements LeafFieldComparator {
        BinaryDocValues dv;

        private float[] values;
        private float bottom;
        private float topValue;
        int x;
        int y;
        String fieldname;

        public DistanceSortComparatorField(String fieldname, int numHits, int x, int y) {
            this.x = x;
            this.y = y;
            values = new float[numHits];
            this.fieldname = fieldname;

        }

        public float getDistance(int doc) throws IOException {
            if (dv.advanceExact(doc)) {
                int xCurrent = dv.binaryValue().bytes[0];
                int yCurrent = dv.binaryValue().bytes[1];

                int deltaX = x - xCurrent;
                int deltaY = y - yCurrent;

                return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            } else {
                return Float.MAX_VALUE;
            }
        }

        @Override
        public int compare(int slot1, int slot2) {
            // Use only inside FieldValueHitQueue (lessThan)

            return Float.compare(values[slot1], values[slot2]);
        }

        @Override
        public void setTopValue(Float value) {
            // This method need only if u use paging inside ur collector. It must set value inside current comparator
            // for using value in compareTop LeafComparator's method.

            topValue = value;
        }

        @Override
        public Float value(int slot) {
            return Float.valueOf(values[slot]);
        }

        @Override
        public LeafFieldComparator getLeafComparator(LeafReaderContext context) throws IOException {
            dv = DocValues.getBinary(context.reader(), fieldname);
            return this;
        }

        // LeafFieldComparator implements

        @Override
        public void setBottom(int slot) throws IOException {
            // todo: understand
            bottom = values[slot];
        }

        @Override
        public int compareBottom(int doc) throws IOException {
            return Float.compare(bottom, getDistance(doc));
        }

        @Override
        public int compareTop(int doc) throws IOException {
            return Float.compare(topValue, getDistance(doc));
        }

        @Override
        public void copy(int slot, int doc) throws IOException {
            values[slot] = getDistance(doc);
        }

        @Override
        public void setScorer(Scorable scorer) {}
    }
}
