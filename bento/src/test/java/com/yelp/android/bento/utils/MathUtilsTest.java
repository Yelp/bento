package com.yelp.android.bento.utils;

/** 
 * Unit tests for {@link MathUtils}.
 * Added by DK96-OS : 2022
 */
public class MathUtilsTest {

	@Test
	public void test_LcmEmptyArray() {
		int[] array = new int[0];
		assertEquals(1, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmOneNumber() {
		int[] array = new int[]{3};
		assertEquals(3, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmOneNumberZero() {
		int[] array = new int[]{0};
		assertEquals(0, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmOneNumberNegative() {
		int[] array = new int[]{-10};
		assertEquals(10, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmTwoNumbers() {
		int[] array = new int[]{3, 4};
		assertEquals(12, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmTwoNumbersOneZero() {
		int[] array = new int[]{3, 0};
		assertEquals(0, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmTwoNumbersOneOne() {
		int[] array = new int[]{3, 1};
		assertEquals(3, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmTwoNumbersLargePrimes() {
		int[] array = new int[]{101, 103};
		assertEquals(101 * 103, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmThreeNumbers() {
		int[] array = new int[]{3, 4, 5};
		assertEquals(60, MathUtils.lcm(array));
	}

	@Test
	public void test_LcmThreeNumbersLargePrimes() {
		int[] array = new int[]{101, 103, 109};
		assertEquals(101 * 103 * 109, MathUtils.lcm(array));
	}

}
