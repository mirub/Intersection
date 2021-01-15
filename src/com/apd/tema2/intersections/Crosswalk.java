package com.apd.tema2.intersections;

import com.apd.tema2.entities.Pedestrians;

import java.util.concurrent.CyclicBarrier;

public class Crosswalk {
    public static int executeTime;
    public static int numPedestrians;
    public static Object mutex;
    public static String prevMsg;
    public static Pedestrians ped;
}
