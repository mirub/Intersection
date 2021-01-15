package com.apd.tema2.intersections;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityIntersection {
    public static int highPriority;
    public static int lowPriority;
    public static ConcurrentLinkedQueue<AtomicInteger> lowQu;
    public static AtomicInteger numHigh;
    public static Object intersection;
    public static ConcurrentHashMap<Integer, Integer> alreadyIn;
    public static CyclicBarrier bar;
}
