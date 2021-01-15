package com.apd.tema2.intersections;

import com.apd.tema2.entities.Intersection;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class SimpleNintersection implements Intersection {
    public static int n;
    public static int t;
    public static Semaphore sem;

    SimpleNintersection(int n, int t) {
        this.n = n;
        this.t = t;
        sem = new Semaphore(n);
    }
}
