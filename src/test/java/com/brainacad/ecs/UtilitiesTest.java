package com.brainacad.ecs;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by vibondarenko on 06.03.2016.
 */
public class UtilitiesTest {

    @Test
    public void testIsDigit() throws Exception {
        assertEquals(new Integer(12), Utilities.isDigit("12"));
    }

    @Test
    public void testCheckName() throws Exception {
        assertEquals("Andrey", Utilities.checkName("Andrey"));
        assertEquals(null, Utilities.checkName("an"));
    }

    @Test
    public void testSearchByName() throws Exception {
        Student student1 = new Student("Vitaliy", "Bondarenko");
        Student student2 = new Student("Andrey", "Bondarenko");
        List<Student> list = new ArrayList<>();
        list.add(list.size(), student1);
        list.add(list.size(), student2);
        assertNotNull(Utilities.searchByName(list, "Vitaliy"));
    }

    @Test
    public void testSearchById() throws Exception {

    }

    @Test
    public void testPressEnter() throws Exception {

    }

    @Test
    public void testListToString() throws Exception {

    }

    @Test
    public void testReadIntValue() throws Exception {

    }

    @Test
    public void testReadStringValue() throws Exception {

    }
}