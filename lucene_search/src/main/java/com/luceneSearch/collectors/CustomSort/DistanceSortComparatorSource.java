package com.luceneSearch.collectors.CustomSort;

import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.search.Scorable;

import java.io.IOException;


public class DistanceSortComparatorSource extends FieldComparatorSource {
    private Integer x;
    private Integer y;

    public DistanceSortComparatorSource(Integer xDoc, Integer yDoc) {
        x = xDoc;
        y = yDoc;
    }

    @Override
    public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) {
        return new DistanceSortComparatorField(numHits, x, y);
    }

    private class DistanceSortComparatorField extends FieldComparator implements LeafFieldComparator {
        BinaryDocValues dv;

        private float[] values;
        private float bottom;

        public DistanceSortComparatorField(int numHits, int xX, int yY) {
            super();
            values = new float[numHits];
            x = xX;
            y = yY;
        }

        @Override
        public int compare(int slot1, int slot2) {
            if (values[slot1] < values[slot2]) return -1;
            if (values[slot1] > values[slot2]) return 1;

            return 0;
        }

        @Override
        public void setTopValue(Object value) {

        }

        @Override
        public Object value(int slot) {
            return Float.valueOf(values[slot]);
        }

        @Override
        public LeafFieldComparator getLeafComparator(LeafReaderContext context) throws IOException {
            dv = DocValues.getBinary(context.reader(), Constants.DOC_COORDINATES);
            return this;
        }


        // LeafFieldComparator implements

        public float getDistance(int doc) throws IOException {
            dv.advance(doc);

            int xCurrent = dv.binaryValue().bytes[0];
            int yCurrent = dv.binaryValue().bytes[1];

            int deltaX = x - xCurrent;
            int deltaY = y - yCurrent;

            return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        }

        @Override
        public void setBottom(int slot) throws IOException {
            bottom = values[slot];
        }

        @Override
        public int compareBottom(int doc) throws IOException {
            float docDistance = getDistance(doc);
            if (bottom < docDistance) return -1;
            if (bottom > docDistance) return 1;
            return 0;
        }

        @Override
        public int compareTop(int doc) throws IOException {
            return 0;
        }

        @Override
        public void copy(int slot, int doc) throws IOException {
            values[slot] = getDistance(doc);
        }

        @Override
        public void setScorer(Scorable scorer) throws IOException {

        }

    }
}
