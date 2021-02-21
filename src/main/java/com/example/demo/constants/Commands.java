package com.example.demo.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands {
    public final static String START = "/start";
    public final static String HELP = "/help";
    public final static String ADD = "/add";
    public final static String EDIT = "/edit";
    public final static String DELETE = "/delete";
    public final static String NEW = "/unknown";
    public final static String BACKUP = "/backup";
    public final static String ADMIN = "/admin";
    public final static String STAT = "/stat";

    public static List<String> getAllCommands() {
        return new ArrayList<>(Arrays.asList(START, HELP, ADD, EDIT, DELETE, NEW, BACKUP, ADMIN, STAT));
    }
}
