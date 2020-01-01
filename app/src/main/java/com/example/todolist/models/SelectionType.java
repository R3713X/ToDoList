package com.example.todolist.models;

import java.util.HashMap;
import java.util.Map;

public enum SelectionType {
    DAY("day"),
    IMPORTANT("important"),
    LIST("list"),
    PLANNED("planned"),
    ALL("all");

    private String type;
     SelectionType(String type) {
        this.type=type;
    }

    public String getType() {
        return type;
    }
    private static final Map<String, SelectionType> lookup = new HashMap<>();

    //Populate the lookup table on loading time
    static
    {
        for(SelectionType selectionType : SelectionType.values())
        {
            lookup.put(selectionType.getType(), selectionType);
        }
    }

    //This method can be used for reverse lookup purpose
    public static SelectionType get(String url)
    {
        return lookup.get(url);
    }
}
