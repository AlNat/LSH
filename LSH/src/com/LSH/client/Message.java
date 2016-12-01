package com.LSH.client;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by @author AlNat on 01.12.2016.
 * Licensed by Apache License, Version 2.0
 */
public class Message implements Serializable {
    String link;
    Timestamp timestamp;
    Integer maxVisits;
}
