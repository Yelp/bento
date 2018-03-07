package com.yelp.android.util;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import java.util.PriorityQueue;
import java.util.Queue;

/** General math utility methods not in {@link Math} class. */
public final class MathUtils {

    private MathUtils() {
        // Do not create.
    }

    /**
     * Calculates the Least Common Multiple. Copied from
     * http://www.geeksforgeeks.org/lcm-of-given-array-elements/
     *
     * @param inputs An array of integers to calculate the lcm of.
     * @return The lcm of all the integers.
     */
    public static int lcm(int[] inputs) {
        int currentLcm = 1;
        int divisor = 2;

        // We iterate all values and reduce them by a common factor until they are all one.

        // We loop until we find an lcm.
        while (true) {
            int counter = 0;
            boolean divisible = false;
            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i] == 0) {
                    // lcm of any set of numbers including 0 must be 0.
                    return 0;
                } else if (inputs[i] < 0) {
                    // This algorithm requires all values to be positive. Makes no difference in
                    // result.
                    inputs[i] = -inputs[i];
                }

                // This value is completely factored out. Mark it as complete.
                if (inputs[i] == 1) {
                    counter++;
                }

                // If it is divisible, factor out the common factor.
                if (inputs[i] % divisor == 0) {
                    divisible = true;
                    inputs[i] = inputs[i] / divisor;
                }
            }

            if (divisible) {
                currentLcm *= divisor;
            } else {
                divisor++;
            }

            // If all values are 1, we have factored out all common factors. Return currentLcm.
            if (counter == inputs.length) {
                return currentLcm;
            }
        }
    }

    /**
     * Converts absolute counts to whole-number percentages, while making sure that the percentages
     * sum up to 100%.
     */
    public static int[] absolutesToPercentages(double[] absolutes, double total) {
        Queue<NumToIndex> absoluteValToIndex = new PriorityQueue<>();
        for (int i = 0; i < absolutes.length; ++i) {
            absoluteValToIndex.add(new NumToIndex(absolutes[i] / total * 100, i));
        }

        int percentageSum = 100;
        int[] percentages = new int[absolutes.length];
        while (percentageSum > 0) {
            NumToIndex p = absoluteValToIndex.poll();
            double valToAdd = Math.max(1, p.first.intValue());
            percentages[p.second] += valToAdd;
            absoluteValToIndex.add(new NumToIndex(p.first - valToAdd, p.second));
            percentageSum -= valToAdd;
        }
        return percentages;
    }

    private static class NumToIndex extends Pair<Double, Integer>
            implements Comparable<NumToIndex> {

        NumToIndex(Double first, Integer second) {
            super(first, second);
        }

        @Override
        public int compareTo(@NonNull NumToIndex o) {
            return Math.abs(o.first - first) > 1.0e-10d
                    ? Double.compare(o.first, first)
                    : Integer.compare(second, o.second);
        }
    }
}
