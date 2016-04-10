package com.bluetask.database;

import java.util.Date;
import java.util.List;

/**
 * Created by Erik on 29/03/2016.
 * Each instance of the reminder class holds to-dos and Positions at which those to-dos can be
 * accomplished.
 */
public class Reminder {

    private int id;
    private String name;
    private int date;
    private String description;
    private List<Position> positionsList;
    private boolean done;

    public Reminder(int id, String name, String descr, int date, boolean done, List<Position> positionsList) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = descr;
        this.done = done;
        this.positionsList = positionsList;
    }

    public Reminder(String name, String descr, int date, boolean done, List<Position> positionsList) {
        this.name = name;
        this.description = descr;
        this.date = date;
        this.done = done;
        this.positionsList = positionsList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Position> getPositionsList() {
        return positionsList;
    }

    public void setPositionsList(List<Position> positionsList) {
        this.positionsList = positionsList;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
