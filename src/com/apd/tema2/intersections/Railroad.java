package com.apd.tema2.intersections;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import com.apd.tema2.utils.RailroadObj;

public class Railroad {
    public static ConcurrentLinkedQueue<AtomicInteger> quId;
    public static ConcurrentLinkedQueue<AtomicInteger> quDir;
    public static CyclicBarrier bar;
}
