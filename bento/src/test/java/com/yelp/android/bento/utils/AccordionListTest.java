package com.yelp.android.bento.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.yelp.android.bento.utils.AccordionList.Range;
import com.yelp.android.bento.utils.AccordionList.RangedValue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Test;

/** Unit tests for {@link AccordionList}. */
public class AccordionListTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_RangeCreateInvalidArguments() {
        new Range(3, 2);
    }

    @Test
    public void test_RangeSize() {
        assertEquals(1, new Range(2, 3).getSize());
    }

    @Test
    public void test_RangeOffset() {
        Range offsetRange = new Range(2, 3).offset(1);
        assertEquals(3, offsetRange.mLower);
        assertEquals(4, offsetRange.mUpper);
    }

    @Test
    public void test_RangeValueOffset() {
        Range offsetRange = new Range(2, 3).offset(1);
        assertEquals(3, offsetRange.mLower);
        assertEquals(4, offsetRange.mUpper);
    }

    @Test
    public void test_Add() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        assertEquals(1, list.size());
        assertEquals(new Range(0, 1), list.span());
        assertEquals("a", list.valueAt(0));
        assertEquals(new RangedValue<>("a", new Range(0, 1)), list.rangedValueAt(0));
    }

    @Test
    public void test_AddZero() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_AddNegative() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", -1);
    }

    @Test
    public void test_AddAppends() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        list.add("b", 2);
        assertEquals(2, list.size());
        assertEquals(new Range(0, 3), list.span());
        assertEquals(new RangedValue<>("a", new Range(0, 1)), list.rangedValueAt(0));
        assertEquals(new RangedValue<>("b", new Range(1, 3)), list.rangedValueAt(2));
        assertEquals(new RangedValue<>("a", new Range(0, 1)), list.get(0));
        assertEquals(new RangedValue<>("b", new Range(1, 3)), list.get(1));
    }

    @Test
    public void test_InsertAdd() {
        AccordionList<String> list = new AccordionList<>();
        list.add(0, "a", 1);
    }

    @Test
    public void test_InsertZero() {
        AccordionList<String> list = new AccordionList<>();
        list.add(0, "a", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_InsertAddNegative() {
        AccordionList<String> list = new AccordionList<>();
        list.add(0, "a", -1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_InsertAddOutOfBounds() {
        AccordionList<String> list = new AccordionList<>();
        list.add(1, "a", 1);
    }

    @Test
    public void test_InsertAddShifts() {
        AccordionList<String> list = new AccordionList<>();
        list.add(0, "a", 1);
        list.add(0, "b", 2);
        assertEquals(new RangedValue<>("b", new Range(0, 2)), list.rangedValueAt(0));
        assertEquals(new RangedValue<>("a", new Range(2, 3)), list.rangedValueAt(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_SetZeroIndexOnEmpty() {
        AccordionList<String> list = new AccordionList<>();
        list.set(0, "a", 1);
    }

    @Test
    public void test_SetUpdates() {
        AccordionList<String> list = new AccordionList<>();
        list.add(0, "a", 1);
        list.set(0, "b", 1);
        assertEquals(new RangedValue<>("b", new Range(0, 1)), list.rangedValueAt(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_SetOutOfBounds() {
        AccordionList<String> list = new AccordionList<>();
        list.set(2, "a", 1);
    }

    @Test
    public void test_Remove() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        list.remove(0);
        assertTrue(list.isEmpty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_RemoveOutOfBounds() {
        AccordionList<String> list = new AccordionList<>();
        list.remove(2);
    }

    @Test
    public void test_IsEmptyTrue() {
        AccordionList<String> list = new AccordionList<>();
        assertTrue(list.isEmpty());
    }

    @Test
    public void test_IsEmptyFalse() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        assertFalse(list.isEmpty());
    }

    @Test
    public void test_Clear() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    public void test_Span() {
        AccordionList<String> list = new AccordionList<>();
        list.add(0, "a", 1);
        list.add(0, "b", 2);
        assertEquals(new Range(0, 3), list.span());
    }

    @Test
    public void test_SpanEmpty() {
        assertEquals(new Range(0, 0), new AccordionList().span());
    }

    @Test
    public void test_IteratorEmptyHasNext() {
        AccordionList<String> list = new AccordionList<>();
        assertFalse(list.iterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_IteratorEmptyNext() {
        AccordionList<String> list = new AccordionList<>();
        list.iterator().next();
    }

    @Test(expected = IllegalStateException.class)
    public void test_IteratorEmptyRemove() {
        AccordionList<String> list = new AccordionList<>();
        list.iterator().remove();
    }

    @Test
    public void test_IteratorSingle() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        Iterator<RangedValue<String>> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(new RangedValue<>("a", new Range(0, 1)), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test_IteratorSingleRemove() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        Iterator<RangedValue<String>> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(new RangedValue<>("a", new Range(0, 1)), iterator.next());
        assertFalse(iterator.hasNext());
        iterator.remove();
        assertFalse(iterator.hasNext());
        assertTrue(list.isEmpty());
    }

    @Test
    public void test_IteratorMultiple() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        list.add("b", 1);
        Iterator<RangedValue<String>> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(new RangedValue<>("a", new Range(0, 1)), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(new RangedValue<>("b", new Range(1, 2)), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test_IteratorMultipleRemove() {
        AccordionList<String> list = new AccordionList<>();
        list.add("a", 1);
        list.add("b", 1);
        Iterator<RangedValue<String>> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(new RangedValue<>("a", new Range(0, 1)), iterator.next());
        assertTrue(iterator.hasNext());
        iterator.remove();
        assertTrue(iterator.hasNext());
        assertEquals(new RangedValue<>("b", new Range(0, 1)), iterator.next());
        assertFalse(iterator.hasNext());
        iterator.remove();
        assertFalse(iterator.hasNext());
        assertTrue(list.isEmpty());
    }
}
