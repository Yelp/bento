package com.yelp.android.bento.utils;

/**
 * General math utility methods not in {@link Math} class.
 */
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
}
