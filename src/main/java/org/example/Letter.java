package org.example;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "letters")
public class Letter {
    @DatabaseField(id = true)
    private int id;

    @DatabaseField(columnName = "letter")
    private String letter;

    public Letter() {}

    public int getId() {
        return id;
    }

    public String getLetter() {
        return letter;
    }
}