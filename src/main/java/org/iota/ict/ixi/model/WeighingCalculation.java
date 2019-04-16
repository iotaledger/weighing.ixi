package org.iota.ict.ixi.model;

import java.util.Set;

public class WeighingCalculation {

    private String vertex;
    private Attribute[] attributes;
    private Interval interval;
    private Set<String> result;

    public WeighingCalculation(String vertex, Attribute[] attributes) {
        this.vertex = vertex;
        this.attributes = attributes;
    }

    public WeighingCalculation(String vertex, Attribute[] attributes, Interval interval) {
        this(vertex, attributes);
        this.interval = interval;
    }

    public void setVertex(String vertex) {
        this.vertex = vertex;
    }

    public String getVertex() {
        return vertex;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setResult(Set<String> result){
        this.result = result;
    }

    public Set<String> getResult(){
        return result;
    }

}
