package com.luceneSearch.TestPriorityQueue;

import org.apache.lucene.util.PriorityQueue;

public class TestPriorityQueue {
    public static void main(String[] args) {
        // stage one
        IntValuePriorityQueue queue = new IntValuePriorityQueue(3);

        // stage two
        queue.add(new IntValue(5));
        queue.add(new IntValue(6));
        queue.add(new IntValue(7));
        Integer.valueOf('1').toString();



        IntValue bottom = queue.top();
        IntValue newValue = new IntValue(10);

        if (bottom.value < newValue.value) {
            bottom.value = newValue.value;
            bottom = queue.updateTop();
        }



        while (queue.size() != 0) {
            System.out.println(queue.pop().value);
        }
    }

    static class IntValue {
        Integer value;

        public IntValue(Integer value) {
            this.value = value;
        }
    }

    static class IntValuePriorityQueue
            extends PriorityQueue<IntValue> {

        public IntValuePriorityQueue(int maxSize) {
            super(maxSize);
        }

        @Override
        protected boolean lessThan(IntValue a, IntValue b) {
            return Integer.compare(a.value, b.value) == -1;
        }
    }
}
