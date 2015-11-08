package com.github.joey11111000111.EasyPlan.core;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.07..
 */
public class SimpleTimeTest {

    @Test
    public void testAll() {
        SimpleTime st;
        // test construction exceptions
        try {
            st = new SimpleTime(26, 1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            st = new SimpleTime(12, -1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        try {
            st = new SimpleTime( (23 * 60 + 59) + 1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            st = new SimpleTime(-1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}


        // test general methods
        st = new SimpleTime(12, 9);
        assertEquals(12, st.getHours());
        assertEquals(9, st.getMinutes());
        assertEquals(12 * 60 + 9, st.getTimeAsMinutes());

        st = new SimpleTime(121);
        assertEquals(2, st.getHours());
        assertEquals(1, st.getMinutes());

        assertFalse(st.setHours(2));
        assertFalse(st.setMinutes(1));

        // test deep copy
        SimpleTime st2 = new SimpleTime(st);
        assertEquals(st.getHours(), st2.getHours());
        assertEquals(st.getMinutes(), st2.getMinutes());
        assertEquals(st.getTimeAsMinutes(), st2.getTimeAsMinutes());

        st2.setHours(9);
        st2.setMinutes(21);
        assertFalse(st.getHours() == st2.getHours());
        assertFalse(st.getMinutes() == st2.getMinutes());
        assertFalse(st.getTimeAsMinutes() == st2.getTimeAsMinutes());
    }

}//class

