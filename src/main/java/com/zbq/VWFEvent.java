package com.zbq;

import org.activiti.engine.impl.util.json.JSONObject;

import java.util.concurrent.atomic.AtomicLong;


public class VWFEvent {
    private static final AtomicLong count = new AtomicLong(0);

    private EventType type;
    private JSONObject data;
    private Long id;

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public VWFEvent(EventType type) {
        super();
        this.type = type;
        this.data = new JSONObject();
        this.id = count.incrementAndGet();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JSONObject getJson() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("type", this.type.toString());
        json.put("data", this.data);
        return json;
    }
}
