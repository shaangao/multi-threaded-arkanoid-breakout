package model;


import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameOpsQueue extends LinkedList<GameOp> {    // see Game class processGameOpsQueue method

    //this data structure is in contention by the "Event Dispatch" thread aka main-swing-thread, and the animation
    // thread. We must restrict access to it by one thread at a time by using a Lock.
    private final Lock lock;


    public GameOpsQueue() {
        this.lock = new ReentrantLock();
    }


    public void enqueue(Sprite sprite, GameOp.Action action) {
        try {
            lock.lock();
            addLast(new GameOp(sprite, action));
        } finally {
            lock.unlock();
        }
    }

    public GameOp dequeue() {
        try {
            lock.lock();
            return removeFirst();
        } finally {
            lock.unlock();
        }
    }
}
