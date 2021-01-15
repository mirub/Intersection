package com.apd.tema2.intersections;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class SimpleStrictXIntersection {
    public static int laneNum;
    public static int roundaboutTime;
    public static int numPassingCars;
    public static ConcurrentHashMap<Integer, Semaphore> sems;
    public static CyclicBarrier bar;
    public static CyclicBarrier carBar;
}
