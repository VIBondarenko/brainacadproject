package com.brainacad.ecs;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utilities {
    public static Integer isDigit(String string) {
        Integer intValue = null;
        try {
            intValue = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return intValue;
        }
        return intValue;
    }
    public static String checkName(String string) {
        String regexp = "[A-Z][a-z]{1,}";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            return string;
        }
        return null;
    }
    public static <T> T searchByName(List<T> list, String string) {
        if (list == null) return null;
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            T item = (T) itr.next();
            try {
                if (string.equals(item.getClass().getMethod("getName").invoke(item))) {
                    return item;
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static  <T> T searchById(List<T> list, int id) {
        if (list == null) return null;
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            T item = (T) itr.next();
            try {
                int elemId = (int) item.getClass().getMethod("getId").invoke(item);
                if (id == elemId){
                    return item;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static void pressEnter() {
        System.out.print("Please, press 'Enter'");
        new Scanner(System.in).nextLine();
    }
    public static <T> String listToString(List<T> list) {
        String strList = "";
        if (list == null) return strList;
        Iterator itr = list.iterator();
        StringBuffer buf = new StringBuffer();
        while (itr.hasNext()) {
            T item = (T) itr.next();
            try {
                buf.append("'" + item.getClass().getMethod("getName").invoke(item) + "'; ");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return buf.toString();
    }
    public static Integer readIntValue() {
        try {
            return new Scanner(System.in).nextInt();
        } catch (Exception e) {
            System.err.println("Sorry, enter valid value!");
            return Integer.MIN_VALUE;
        }
    }
    public  static String readStringValue() {
        return new Scanner(System.in).nextLine();
    }
}
