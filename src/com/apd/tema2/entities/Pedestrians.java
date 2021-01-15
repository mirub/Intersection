package com.apd.tema2.entities;

import com.apd.tema2.intersections.Crosswalk;
import com.apd.tema2.utils.Constants;

import static java.lang.Thread.sleep;

/**
 * Clasa utilizata pentru gestionarea oamenilor care se strang la trecerea de pietoni.
 */
public class Pedestrians implements Runnable {
    private int pedestriansNo = 0;
    private int maxPedestriansNo;
    private boolean pass = false;
    private boolean finished = false;
    private int executeTime;
    private long startTime;
    private int task;

    public Pedestrians(int executeTime, int maxPedestriansNo, int task) {
        this.startTime = System.currentTimeMillis();
        this.executeTime = executeTime;
        this.maxPedestriansNo = maxPedestriansNo;
        // 1 for crosswalk
        this.task = task;
    }

    @Override
    public void run() {
        while(System.currentTimeMillis() - startTime < executeTime) {
            try {
                pedestriansNo++;
                sleep(Constants.PEDESTRIAN_COUNTER_TIME);

                if (pedestriansNo == maxPedestriansNo) {
                    pedestriansNo = 0;
                    pass = true;

                    sleep(Constants.PEDESTRIAN_PASSING_TIME);
                    pass = false;

                    // Cars can pass now
                    synchronized (Crosswalk.mutex) {
                        if (this.task == 1) {
                            Crosswalk.mutex.notifyAll();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        finished = true;
    }

    public synchronized boolean isPass() {
        return pass;
    }

    public synchronized boolean isFinished() {
        return finished;
    }
}
