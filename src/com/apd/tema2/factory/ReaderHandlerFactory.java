package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.Pedestrians;
import com.apd.tema2.entities.ReaderHandler;
import com.apd.tema2.intersections.*;
import com.apd.tema2.utils.RailroadObj;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Returneaza sub forma unor clase anonime implementari pentru metoda de citire din fisier.
 */
public class ReaderHandlerFactory {

    public static ReaderHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of them)
        // road in maintenance - 1 lane 2 ways, X cars at a time
        // road in maintenance - N lanes 2 ways, X cars at a time
        // railroad blockage for T seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) {
                    // Exemplu de utilizare:
                    // Main.intersection = IntersectionFactory.getIntersection("simpleIntersection");
                }
            };
            case "simple_n_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    // To parse input line use:
                    // String[] line = br.readLine().split(" ");
                    String[] line = br.readLine().split(" ");
                    int n = Integer.parseInt(line[0]);;
                    SimpleNintersection.n = n;
                    SimpleNintersection.sem = new Semaphore(n);
                    int t = Integer.parseInt(line[1]);
                    SimpleNintersection.t = t;

                }
            };
            case "simple_strict_1_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int lane = Integer.parseInt(line[0]);;
                    SimpleStrict1Intersection.laneNum = lane;
                    int time = Integer.parseInt(line[1]);
                    SimpleStrict1Intersection.time = time;
                    Vector<Object> locks = new Vector<Object>(lane);
                    CyclicBarrier bar = new CyclicBarrier(lane);
                    SimpleStrict1Intersection.bar = bar;
                    SimpleStrict1Intersection.locks = locks;
                    for (int i = 0; i < lane; ++i) {
                        SimpleStrict1Intersection.locks.add(new Object());
                    }
                }
            };
            case "simple_strict_x_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int lane = Integer.parseInt(line[0]);;
                    SimpleStrictXIntersection.laneNum = lane;

                    int time = Integer.parseInt(line[1]);
                    SimpleStrictXIntersection.roundaboutTime = time;

                    int cars = Integer.parseInt(line[2]);
                    SimpleStrictXIntersection.numPassingCars = cars;

                    ConcurrentHashMap<Integer, Semaphore> sems = new ConcurrentHashMap<Integer, Semaphore>(lane);
                    SimpleStrictXIntersection.sems = sems;
                    for (int i = 0; i < lane; ++i) {
                        SimpleStrictXIntersection.sems.put(i, new Semaphore(cars));
                    }

                    CyclicBarrier bar = new CyclicBarrier(cars * lane);
                    SimpleStrictXIntersection.bar = bar;

                    CyclicBarrier carBar = new CyclicBarrier(cars);
                    SimpleStrictXIntersection.carBar = carBar;
                }
            };
            case "simple_max_x_car_roundabout" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int lane = Integer.parseInt(line[0]);;
                    SimpleMaxXIntersection.laneNum = lane;

                    int time = Integer.parseInt(line[1]);
                    SimpleMaxXIntersection.roundaboutTime = time;

                    int cars = Integer.parseInt(line[2]);
                    SimpleMaxXIntersection.numPassingCars = cars;

                    Vector<Semaphore> sems = new Vector<Semaphore>(lane);
                    SimpleMaxXIntersection.sems = sems;
                    for (int i = 0; i < lane; ++i) {
                        SimpleMaxXIntersection.sems.add(new Semaphore(cars));
                    }
                }
            };
            case "priority_intersection" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int low = Integer.parseInt(line[0]);
                    PriorityIntersection.lowPriority = low;

                    int high = Integer.parseInt(line[1]);
                    PriorityIntersection.highPriority = high;

                    PriorityIntersection.lowQu = new ConcurrentLinkedQueue<>();
                    PriorityIntersection.numHigh = new AtomicInteger(0);
                    PriorityIntersection.bar = new CyclicBarrier(Main.carsNo);
                    PriorityIntersection.intersection = new Object();
                    PriorityIntersection.alreadyIn = new ConcurrentHashMap<>();
                }
            };
            case "crosswalk" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int execTime = Integer.parseInt(line[0]);
                    Crosswalk.executeTime = execTime;

                    int numPedestrians = Integer.parseInt(line[0]);
                    Crosswalk.numPedestrians = numPedestrians;

                    Crosswalk.mutex = new Object();
                    Crosswalk.prevMsg = "";
                    Crosswalk.ped = new Pedestrians(Crosswalk.executeTime, Crosswalk.numPedestrians, 1);
                }
            };
            case "simple_maintenance" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    String[] line = br.readLine().split(" ");
                    int num = Integer.parseInt(line[0]);
                    SimpleMaintenance.num = num;

                    SimpleMaintenance.sem0 = new Semaphore(num);
                    SimpleMaintenance.sem1 = new Semaphore(0);
                    SimpleMaintenance.bar0 = new CyclicBarrier(num);
                    SimpleMaintenance.bar1 = new CyclicBarrier(num);
                }
            };

            case "complex_maintenance" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    
                }
            };

            case "railroad" -> new ReaderHandler() {
                @Override
                public void handle(final String handlerType, final BufferedReader br) throws IOException {
                    Railroad.quId = new ConcurrentLinkedQueue<>();
                    Railroad.quDir = new ConcurrentLinkedQueue<>();
                    Railroad.bar = new CyclicBarrier(Main.carsNo);
                }
            };
            default -> null;
        };
    }

}
