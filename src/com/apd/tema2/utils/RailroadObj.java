package com.apd.tema2.utils;

import com.apd.tema2.intersections.Railroad;

public class RailroadObj extends Object {
    public static int id;
    public static int direction;

    public RailroadObj (int id, int direction) {
        this.id = id;
        this.direction = direction;
    }

    public RailroadObj () {
        this.id = 0;
        this.direction = 0;
    }
}
