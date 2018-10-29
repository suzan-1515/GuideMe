package com.guideme.guideme.model;

public enum Category {

    TEMPLE("hindu_temple"),
    ZOO("zoo"),
    STADIUM("stadium"),
    CHURCH("church"),
    MUSEUM("museum"),
    MOSQUE("mosque"),
    NIGHTCLUB("night_club"),
    SYNAGOGUE("synagogue");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name();
    }

}
