package com.bottrade.model;

import lombok.Data;

@Data
public class EventData {

    private String event;

    private Object data;

    public EventData(String event, Object data) {
        this.event = event;
        this.data = data;
    }

    public static EventData from(String event, Object data){
        return new EventData(event, data);
    }
}
