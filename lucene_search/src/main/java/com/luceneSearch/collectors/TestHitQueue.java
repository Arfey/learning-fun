package com.luceneSearch.collectors;


import org.apache.lucene.util.PriorityQueue;


public class TestHitQueue {
    public static void main(String[] args) {
        HitQueue queue = new HitQueue(5);

        queue.add(2);
        queue.add(3);
        queue.add(1);
        queue.add(4);
        queue.add(5);


        for (Integer i: queue) {
            System.out.print(i);
            System.out.print(",");
        }


        printQueue(queue);
    }

    static public void printQueue(HitQueue queue) {
        System.out.print("(");

        for (int i = queue.size() - 1; i >= 0; i--) {
            System.out.print(queue.pop());
            System.out.print(",");
        }

        System.out.println(")");
    }

    static class HitQueue extends PriorityQueue<Integer> {

        public HitQueue(int maxSize) {
            super(maxSize);
        }

        @Override
        protected boolean lessThan(Integer a, Integer b) {
            if (a < b) {
                return true;
            }

            return false;
        }
    }
}
