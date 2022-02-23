package com.igloosec.generator.service.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class OutputInfoVO {
    private int port;
    private List<String> clients;
    private Map<Integer, EpsVO> producerEps;
    private EpsVO consumerEps;
    private int maxQueueSize;
    private int currentQueueSize;
    private transient ISocketServer server;
    private long startedTime;
    private long runningTime;
    
    public OutputInfoVO() {
        this.startedTime = System.currentTimeMillis();
    }
    
    public long getRunningTime() {
        this.runningTime = System.currentTimeMillis() - this.startedTime;
        return runningTime;
    }
    
    public List<String> getClients(){
        if (clients == null) {
            this.clients = new ArrayList<>();
        }
        return this.clients;
    }
}