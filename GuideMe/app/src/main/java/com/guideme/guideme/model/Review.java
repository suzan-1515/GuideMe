package com.guideme.guideme.model;

import java.io.Serializable;

public class Review implements Serializable {

    public long id;
    public String userId;
    public String placeId;
    public String userName;
    public int rating;
    public String review;

}
