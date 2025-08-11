package com.brainacad.ecs;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Utilities {
    private static final Logger logger = Logger.getLogger(Utilities.class.getName());
    private static final Scanner inputScanner = new Scanner(System.in);
    
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
        if (list == null || string == null) return null;
        for (T item : list) {
            if (item == null) continue; // Skip null items
            try {
                Object nameObj = item.getClass().getMethod("getName").invoke(item);
                if (nameObj != null && string.equals(nameObj.toString())) {
                    return item;
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                logger.log(Level.WARNING, "Error accessing getName method for item: " + item.getClass().getSimpleName(), e);
                // Continue searching other items instead of failing completely
            }
        }
        return null;
    }
    public static  <T> T searchById(List<T> list, int id) {
        if (list == null) return null;
        for (T item : list) {
            if (item == null) continue; // Skip null items
            try {
                Object idObj = item.getClass().getMethod("getId").invoke(item);
                if (idObj instanceof Integer) {
                    int elemId = (Integer) idObj;
                    if (id == elemId){
                        return item;
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.log(Level.WARNING, "Error accessing getId method for item: " + item.getClass().getSimpleName(), e);
                // Continue searching other items instead of failing completely
            }
        }
        return null;
    }

    public static void ClearConsole(){
        try{
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system

            if(operatingSystem.contains("Windows")){
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        }catch(Exception e){
            logger.log(Level.WARNING, "Could not clear console", e);
            // Fallback - print some newlines
            System.out.println("\n\n\n\n\n\n\n\n\n\n");
        }
    }

    public static void pressEnter() {
        System.out.println("");
        System.out.print("Please, press 'Enter'");
        inputScanner.nextLine();
        ClearConsole();
    }
    public static <T> String listToString(List<T> list) {
        String strList = "";
        if (list == null) return strList;
        StringBuilder buf = new StringBuilder();
        for (T item : list) {
            if (item == null) continue; // Skip null items
            try {
                Object nameObj = item.getClass().getMethod("getName").invoke(item);
                if (nameObj != null) {
                    String name = nameObj.toString();
                    buf.append("'").append(name).append("'; ");
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.log(Level.WARNING, "Error accessing getName method for item: " + item.getClass().getSimpleName(), e);
                // Skip this item and continue with others
            }
        }
        return buf.toString();
    }
    
    // Modern Optional-based search methods for safer null handling
    public static <T> Optional<T> findByName(List<T> list, String name) {
        if (list == null || name == null) return Optional.empty();
        
        return list.stream()
                   .filter(item -> item != null)
                   .filter(item -> {
                       try {
                           Object nameObj = item.getClass().getMethod("getName").invoke(item);
                           return nameObj != null && name.equals(nameObj.toString());
                       } catch (Exception e) {
                           logger.log(Level.WARNING, "Error accessing getName for item: " + item.getClass().getSimpleName(), e);
                           return false;
                       }
                   })
                   .findFirst();
    }
    
    public static <T> Optional<T> findById(List<T> list, int id) {
        if (list == null) return Optional.empty();
        
        return list.stream()
                   .filter(item -> item != null)
                   .filter(item -> {
                       try {
                           Object idObj = item.getClass().getMethod("getId").invoke(item);
                           return idObj instanceof Integer && ((Integer) idObj) == id;
                       } catch (Exception e) {
                           logger.log(Level.WARNING, "Error accessing getId for item: " + item.getClass().getSimpleName(), e);
                           return false;
                       }
                   })
                   .findFirst();
    }
    
    public static Integer readIntValue() {
        try {
            return inputScanner.nextInt();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Invalid integer input", e);
            System.err.println("Sorry, enter valid value!");
            inputScanner.nextLine(); // Clear the invalid input
            return Integer.MIN_VALUE;
        }
    }
    public  static String readStringValue() {
        return inputScanner.nextLine();
    }
}
