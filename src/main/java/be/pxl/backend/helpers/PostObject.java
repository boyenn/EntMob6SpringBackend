/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.pxl.backend.helpers;

import java.util.Date;

/**
 *
 * @author Boyen
 * @param <T>
 */
public class PostObject<T> {
    private T value;
    private Date timeStamp;

    public PostObject() {
    }

    public PostObject(T value, Date timeStamp) {
        this.value = value;
        this.timeStamp = timeStamp;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "PostObject{" + "value=" + value + ", valueType =" + value.getClass().getName() + ", timeStamp=" + timeStamp + '}';
    }
    
    
    
}
