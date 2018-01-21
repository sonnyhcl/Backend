package com.zbq;

import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
public class GlobalEventQueue {
    private Long sLastID;
    private Long rLastID;
    private LinkedBlockingQueue<VWFEvent> sendQueue;
    private LinkedBlockingQueue<ACTFEvent> recQueue;

    public GlobalEventQueue() {
        this.sLastID = 0L;
        this.rLastID = 0L;
        this.setSendQueue(new LinkedBlockingQueue<VWFEvent>());
        this.setRecQueue(new LinkedBlockingQueue<ACTFEvent>());
    }

    public LinkedBlockingQueue<ACTFEvent> getRecQueue() {
        return recQueue;
    }

    public void setRecQueue(LinkedBlockingQueue<ACTFEvent> recQueue) {
        this.recQueue = recQueue;
    }

    public LinkedBlockingQueue<VWFEvent> getSendQueue() {
        return sendQueue;
    }

    public void setSendQueue(LinkedBlockingQueue<VWFEvent> sendQueue) {
        this.sendQueue = sendQueue;
    }

    public Long getsLastID() {
        return sLastID;
    }

    public void setsLastID(Long sLastID) {
        this.sLastID = sLastID;
    }

    public Long getrLastId() {
        return rLastID;
    }

    public void setrLastId(Long rLastId) {
        this.rLastID = rLastId;
    }

    public void sendMsg(VWFEvent e) {
        try {
            System.out.println("Send a " + e.getType().toString() + " message");
            this.sendQueue.put(e);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
