package com.apd.tema2.factory;

import com.apd.tema2.Main;
import com.apd.tema2.entities.*;
import com.apd.tema2.intersections.*;
import com.apd.tema2.utils.Constants;
import com.apd.tema2.utils.RailroadObj;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import static java.lang.Thread.sleep;

/**
 * Clasa Factory ce returneaza implementari ale InterfaceHandler sub forma unor
 * clase anonime.
 */
public class IntersectionHandlerFactory {

    public static IntersectionHandler getHandler(String handlerType) {
        // simple semaphore intersection
        // max random N cars roundabout (s time to exit each of them)
        // roundabout with exactly one car from each lane simultaneously
        // roundabout with exactly X cars from each lane simultaneously
        // roundabout with at most X cars from each lane simultaneously
        // entering a road without any priority
        // crosswalk activated on at least a number of people (s time to finish all of
        // them)
        // road in maintenance - 2 ways 1 lane each, X cars at a time
        // road in maintenance - 1 way, M out of N lanes are blocked, X cars at a time
        // railroad blockage for s seconds for all the cars
        // unmarked intersection
        // cars racing
        return switch (handlerType) {
            case "simple_semaphore" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    synchronized(car) {
                        System.out.println("Car " + car.getId() + " has reached the semaphore, now waiting...");
                        try {
                            sleep(car.getWaitingTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Car " + car.getId() + " has waited enough, now driving...");
                    }
                }
            };
            case "simple_n_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    synchronized (this) {
                        try {
                            System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");
                            SimpleNintersection.sem.acquire();
                            synchronized (this) {
                                try {
                                    System.out.println("Car " + car.getId() + " has entered the roundabout");
                                    sleep(SimpleNintersection.t);
                                    int numSeconds = SimpleNintersection.t / 1000;
                                    System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                                             numSeconds + " seconds");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            SimpleNintersection.sem.release();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            case "simple_strict_1_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    System.out.println("Car " + car.getId() + " has reached the roundabout");
                    try {
                        SimpleStrict1Intersection.bar.await();
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();

                    }
                    synchronized (SimpleStrict1Intersection.locks.get(car.getStartDirection())) {
                        try {
                            System.out.println("Car " + car.getId() + " has entered the roundabout from lane " +
                                    car.getStartDirection());
                            sleep(SimpleStrict1Intersection.time);
                            int numSeconds = SimpleStrict1Intersection.time / 1000;
                            System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                                    numSeconds + " seconds");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            case "simple_strict_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    synchronized (this) {
                        System.out.println("Car " + car.getId() + " has reached the roundabout, now waiting...");

                        try {
                            SimpleStrictXIntersection.carBar.await();
                        } catch (BrokenBarrierException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            SimpleStrictXIntersection.sems.get(car.getStartDirection()).acquire();
                            System.out.println("Car " + car.getId() + " was selected to enter the roundabout from lane " +
                                    car.getStartDirection());

                            try {
                                SimpleStrictXIntersection.bar.await();
                            } catch (BrokenBarrierException | InterruptedException e) {
                                e.printStackTrace();
                            }


                            try {
                                System.out.println("Car " + car.getId() + " has entered the roundabout from lane " +
                                        car.getStartDirection());
                                sleep(SimpleStrictXIntersection.roundaboutTime);
                                int numSeconds = SimpleStrictXIntersection.roundaboutTime / 1000;
                                System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                                        numSeconds + " seconds");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SimpleStrictXIntersection.sems.get(car.getStartDirection()).release();
                    }
                }
            };
            case "simple_max_x_car_roundabout" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI

                    // Continuati de aici

                    synchronized (this) {
                        System.out.println("Car " + car.getId() + " has reached the roundabout from lane " + car.getStartDirection());
                        try {
                            SimpleMaxXIntersection.sems.get(car.getStartDirection()).acquire();
                            System.out.println("Car " + car.getId()  + " has entered the roundabout from lane " +
                                    car.getStartDirection());

                            try {
                                sleep(SimpleMaxXIntersection.roundaboutTime);
                                int numSeconds = SimpleMaxXIntersection.roundaboutTime / 1000;
                                System.out.println("Car " + car.getId() + " has exited the roundabout after " +
                                        numSeconds + " seconds");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SimpleMaxXIntersection.sems.get(car.getStartDirection()).release();
                    }
                }
            };
            case "priority_intersection" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    // Get your Intersection instance

                    try {
                        sleep(car.getWaitingTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // NU MODIFICATI

                    // Continuati de aici
                    synchronized (this) {
                        if (car.getPriority() == 1) {
                            synchronized (PriorityIntersection.intersection) {
                                System.out.println("Car " + car.getId() + " with low priority is trying to enter the intersection...");
                                try {
                                    if (PriorityIntersection.numHigh.get() == 0) {
                                        System.out.println("Car " + car.getId() + " with low priority has entered the intersection");
                                        PriorityIntersection.alreadyIn.put(car.getId(), car.getId());
                                    } else {
                                        PriorityIntersection.lowQu.add(new AtomicInteger(car.getId()));
                                        PriorityIntersection.intersection.wait();
                                        while (!PriorityIntersection.lowQu.isEmpty()) {
                                            int aux = PriorityIntersection.lowQu.poll().get();
                                            System.out.println("Car " + aux + " with low priority has entered the intersection");
                                            PriorityIntersection.alreadyIn.put(aux, aux);
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            System.out.println("Car " + car.getId() + " with high priority has entered the intersection");
                            PriorityIntersection.numHigh.getAndAdd(1);

                            try {
                                sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            synchronized (PriorityIntersection.intersection) {
                                System.out.println("Car " + car.getId() + " with high priority has exited the intersection");
                                PriorityIntersection.numHigh.getAndAdd(-1);
                                if (PriorityIntersection.numHigh.get() == 0) {
                                PriorityIntersection.intersection.notifyAll();
                                }
                            }
                        }
                    }
                }
            };
            case "crosswalk" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    synchronized (this) {
                        if (!Crosswalk.ped.isFinished()) {
                            if (Crosswalk.ped.isPass()) {
                                System.out.println("Car " + car.getId() + " has now red light");
                                synchronized (Crosswalk.mutex) {
                                    try {
                                        Crosswalk.mutex.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                System.out.println("Car " + car.getId() + " has now green light");
                            }
                        }
                    }
                }
            };
            case "simple_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection() +
                               " has reached the bottleneck");

                    if (car.getStartDirection() == 0) {
                        try {
                            SimpleMaintenance.sem0.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                         System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection() +
                            " has passed the bottleneck");

                        try {
                            SimpleMaintenance.bar0.await();
                        } catch (BrokenBarrierException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        SimpleMaintenance.sem1.release();
                    } else {
                        try {
                            SimpleMaintenance.sem1.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection() +
                            " has passed the bottleneck");


                        try {
                            SimpleMaintenance.bar1.await();
                        } catch (BrokenBarrierException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        SimpleMaintenance.sem0.release();
                    }

                }
            };
            case "complex_maintenance" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    
                }
            };
            case "railroad" -> new IntersectionHandler() {
                @Override
                public void handle(Car car) {
                    synchronized (this) {
                        synchronized (Railroad.quDir) {
                            System.out.println("Car " + car.getId() + " from side number " + car.getStartDirection()
                                    + " has stopped by the railroad");

                            Railroad.quId.add(new AtomicInteger(car.getId()));
                            Railroad.quDir.add(new AtomicInteger(car.getStartDirection()));
                        }

                        try {
                            Railroad.bar.await();
                        } catch (BrokenBarrierException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (car.getId() == 0) {
                            System.out.println("The train has passed, cars can now proceed");
                            while (!Railroad.quId.isEmpty() && !Railroad.quDir.isEmpty()) {
                                int auxId = Railroad.quId.poll().get();
                                int auxDir = Railroad.quDir.poll().get();
                                System.out.println("Car " + auxId + " from side number " + auxDir +
                                        " has started driving");
                            }
                        }
                    }
                }
            };
            default -> null;
        };
    }
}
