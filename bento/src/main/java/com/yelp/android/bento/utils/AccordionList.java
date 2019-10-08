package com.yelp.android.bento.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yelp.android.bento.utils.AccordionList.RangedValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A continuous ordered list of sized entries that starts from zero and keeps track of the range
 * that each entry occupies across additions, updates, and removals. Queries look up the entry
 * associated with the range.
 */
public class AccordionList<T> implements Iterable<RangedValue<T>> {

    private List<RangedValue<T>> mRangeList = new ArrayList<>();

    /**
     * Returns an iterator for the AccordionList. Not concurrent modification safe.
     */
    @NonNull
    @Override
    public Iterator<RangedValue<T>> iterator() {
        return new AccordionListIterator();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mRangeList).toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (object.getClass() != getClass()) {
            return false;
        }

        AccordionList that = (AccordionList) object;

        return new EqualsBuilder().append(this.mRangeList, that.mRangeList).isEquals();
    }

    /**
     * Returns the value associated with the range this location belongs to.
     */
    @NonNull
    public T valueAt(int location) {
        return rangedValueAt(location).mValue;
    }

    /**
     * Returns the range and its associated value that this location belongs to.
     */
    @NonNull
    public RangedValue<T> rangedValueAt(int location) {
        int foundLocation = Collections.binarySearch(mRangeList, location);

        if (foundLocation < 0) {
            throw new ArrayIndexOutOfBoundsException(
                    "Could not find value at index: " + location + ".\n" +
                            "BinarySearch returned: " + foundLocation + ".\n" +
                            describeAccordionList()
            );
        }
        return mRangeList.get(foundLocation);
    }

    /**
     * Returns the indexed range and its associated value.
     */
    @NonNull
    public RangedValue<T> get(int entryIndex) {
        return mRangeList.get(entryIndex);
    }

    /**
     * Returns the span of the {@link AccordionList}, which corresponds to the sum of all entry
     * sizes.
     */
    @NonNull
    public Range span() {
        return new Range(
                0, mRangeList.isEmpty() ? 0 : mRangeList.get(mRangeList.size() - 1).mRange.mUpper);
    }

    /**
     * Returns the number of entries in the {@link AccordionList}.
     */
    public int size() {
        return mRangeList.size();
    }

    public boolean isEmpty() {
        return mRangeList.isEmpty();
    }

    /**
     * Inserts the specified element to the end of the list.
     *
     * @param value Value to append
     * @param size  Size to associate with the value. Cannot be negative.
     */
    public void add(@NonNull T value, int size) {
        add(mRangeList.size(), value, size);
    }

    /**
     * Inserts the specified entry at the specified position in this list.
     *
     * @param entryIndex Position to insert
     * @param value      Value to insert
     * @param size       Size to associate with the value. Cannot be negative.
     */
    public void add(int entryIndex, @NonNull T value, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        int sizeAtPosition = entryIndex == 0 ? 0 : mRangeList.get(entryIndex - 1).mRange.mUpper;
        mRangeList.add(
                entryIndex,
                new RangedValue<T>(value, new Range(sizeAtPosition, sizeAtPosition + size)));
        // Update following sections.
        for (int i = entryIndex + 1; i < mRangeList.size(); i++) {
            mRangeList.set(i, mRangeList.get(i).offset(size));
        }
    }

    public void addAll(@NonNull AccordionList<T> values) {
        for (RangedValue<T> value : values) {
            add(value.mValue, value.mRange.getSize());
        }
    }

    /**
     * Updates the entry at the specified position in this list.
     *
     * @param entryIndex Position to update
     * @param value      Value to insert
     * @param size       Size to associate with the value. Cannot be negative.
     */
    public void set(int entryIndex, @NonNull T value, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        int sizeAtPosition = entryIndex == 0 ? 0 : mRangeList.get(entryIndex - 1).mRange.mUpper;
        int offset = size - mRangeList.get(entryIndex).mRange.getSize();
        mRangeList.set(
                entryIndex,
                new RangedValue<T>(value, new Range(sizeAtPosition, sizeAtPosition + size)));
        // Update following sections.
        for (int i = entryIndex + 1; i < mRangeList.size(); i++) {
            mRangeList.set(i, mRangeList.get(i).offset(offset));
        }
    }

    public void clear() {
        mRangeList.clear();
    }

    public void remove(int entryIndex) {
        int offset = -mRangeList.get(entryIndex).mRange.getSize();
        mRangeList.remove(entryIndex);
        // Update following sections.
        for (int i = entryIndex; i < mRangeList.size(); i++) {
            mRangeList.set(i, mRangeList.get(i).offset(offset));
        }
    }

    private String describeAccordionList() {
        StringBuilder builder = new StringBuilder();
        builder.append("AccordionList has size: ")
                .append(mRangeList.size())
                .append(". /n")
                .append("Items in AccordionList:\n");

        for (RangedValue<T> range : mRangeList) {
            builder.append(range.toString());
        }

        return builder.toString();
    }

    private class AccordionListIterator implements Iterator<RangedValue<T>> {

        private int mRemaining = mRangeList.size();
        private int removalIndex = -1;

        @Override
        public boolean hasNext() {
            return mRemaining != 0;
        }

        @NonNull
        @Override
        public RangedValue<T> next() {
            if (mRemaining == 0) {
                throw new NoSuchElementException();
            }

            mRemaining--;
            return mRangeList.get(removalIndex = mRangeList.size() - 1 - mRemaining);
        }

        @Override
        public void remove() {
            if (removalIndex < 0) {
                throw new IllegalStateException();
            }

            AccordionList.this.remove(removalIndex);
            removalIndex = -1;
        }
    }

    /**
     * Immutable class for describing the range of two numeric values.
     * <p>
     * A range defines the inclusive-exclusive boundaries around a contiguous span of integers.
     * For example: {@code new Range(2, 5)} creates the interval [2, 5); that is, the values 2, 3,
     * and 4 would be represented by this range.
     * </p>
     * <p>
     * All ranges are bounded, and the left side of the range is always {@code <}
     * the right side of the range.
     * </p>
     */
    public static class Range {

        /**
         * The lower endpoint (inclusive)
         */
        public final int mLower;

        /**
         * The upper endpoint (exclusive)
         */
        public final int mUpper;

        /**
         * Create a new immutable range.
         *
         * <p>
         * The endpoints are {@code [lower, upper)}. {@code lower} must be less than {@code upper}.
         * </p>
         *
         * @param lower The lower endpoint (inclusive)
         * @param upper The upper endpoint (exclusive)
         * @throws IllegalArgumentException if {@code lower} > {@code upper}
         */
        public Range(int lower, int upper) {
            this.mLower = lower;
            this.mUpper = upper;

            if (lower > upper) {
                throw new IllegalArgumentException("lower must be less than or equal to upper");
            }
        }

        /**
         * @return The number of integers in the range. eg. {@code new Range(2, 5).getSize()}
         * returns 3 since the values 2, 3 and 4 are represented by this range.
         */
        public int getSize() {
            return mUpper - mLower;
        }

        /**
         * @param offset Some integer by which we want to offset the range of values.
         * @return A new {@link Range} that represents the values at the offset provided. eg.
         * {@code new Range(2, 5).offset(3)} returns a new range that represents [5, 8).
         */
        @NonNull
        public Range offset(int offset) {
            return new Range(mLower + offset, mUpper + offset);
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }

            if (object == this) {
                return true;
            }

            if (object.getClass() != getClass()) {
                return false;
            }

            Range that = (Range) object;

            return new EqualsBuilder()
                    .append(this.mLower, that.mLower)
                    .append(this.mUpper, that.mUpper)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(mLower).append(mUpper).toHashCode();
        }

        @NonNull
        @Override
        public String toString() {
            return "[" + mLower + ", " + mUpper + ")";
        }
    }

    /**
     * Immutable class for grouping a value with a {@link Range}.
     *
     * @param <V> The type of value
     */
    public static class RangedValue<V> implements Comparable<Integer> {

        public final V mValue;
        public final Range mRange;

        public RangedValue(@NonNull V value, @NonNull Range range) {
            mValue = value;
            mRange = range;
        }

        @NonNull
        public RangedValue<V> offset(int offset) {
            return new RangedValue<>(mValue, mRange.offset(offset));
        }

        @Override
        public int compareTo(@NonNull Integer index) {
            if (index >= mRange.mLower && index < mRange.mUpper) {
                return 0;
            }
            return index < mRange.mLower ? 1 : -1;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object == null) {
                return false;
            }

            if (object == this) {
                return true;
            }

            if (object.getClass() != getClass()) {
                return false;
            }

            RangedValue that = (RangedValue) object;

            return new EqualsBuilder()
                    .append(this.mValue, that.mValue)
                    .append(this.mRange, that.mRange)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(mValue).append(mRange).toHashCode();
        }

        @NonNull
        @Override
        public String toString() {
            return "Range: " + mRange + "\nValue: " + mValue;
        }
    }
}
