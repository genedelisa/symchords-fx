package com.rockhoppertech.symchords.fx;

/*
 * #%L
 * symchords-fx
 * %%
 * Copyright (C) 2013 - 2014 Rockhopper Tecnologies
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 * Just a dumb container of parameters.
 * 
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * 
 */
public class SymParams implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3372182458137524043L;
    private int basePitch;
    private List<Integer> intervals;
    private boolean upAndDown;
    private boolean relative;
    private int unit;
    private int nOctaves;

    /**
     * @param basePitch
     * @param intervals
     * @param upAndDown
     * @param relative
     * @param unit
     * @param nOctaves
     */
    public SymParams(int basePitch, List<Integer> intervals,
            boolean relative) {
        this.basePitch = basePitch;
        this.intervals = intervals;
        this.relative = relative;
    }

    /**
     * @param basePitch
     * @param intervals
     * @param relative
     * @param unit
     * @param nOctaves
     */
    public SymParams(int basePitch, List<Integer> intervals,
            boolean relative, int unit, int nOctaves) {
        super();
        this.basePitch = basePitch;
        this.intervals = intervals;
        this.relative = relative;
        this.unit = unit;
        this.nOctaves = nOctaves;
    }

    /**
     * @return the basePitch
     */
    public int getBasePitch() {
        return basePitch;
    }

    /**
     * @param basePitch
     *            the basePitch to set
     */
    public void setBasePitch(int basePitch) {
        this.basePitch = basePitch;
    }

    /**
     * @return the intervals
     */
    public List<Integer> getIntervals() {
        return intervals;
    }

    /**
     * @param intervals
     *            the intervals to set
     */
    public void setIntervals(List<Integer> intervals) {
        this.intervals = intervals;
    }

    /**
     * @return the upAndDown
     */
    public boolean isUpAndDown() {
        return upAndDown;
    }

    /**
     * @param upAndDown
     *            the upAndDown to set
     */
    public void setUpAndDown(boolean upAndDown) {
        this.upAndDown = upAndDown;
    }

    /**
     * @return the relative
     */
    public boolean isRelative() {
        return relative;
    }

    /**
     * @param relative
     *            the relative to set
     */
    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    /**
     * @return the unit
     */
    public int getUnit() {
        return unit;
    }

    /**
     * @param unit
     *            the unit to set
     */
    public void setUnit(int unit) {
        this.unit = unit;
    }

    /**
     * @return the nOctaves
     */
    public int getnOctaves() {
        return nOctaves;
    }

    /**
     * @param nOctaves
     *            the nOctaves to set
     */
    public void setnOctaves(int nOctaves) {
        this.nOctaves = nOctaves;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + basePitch;
        result = prime * result
                + ((intervals == null) ? 0 : intervals.hashCode());
        result = prime * result + nOctaves;
        result = prime * result + (relative ? 1231 : 1237);
        result = prime * result + unit;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SymParams other = (SymParams) obj;
        if (basePitch != other.basePitch) {
            return false;
        }
        if (intervals == null) {
            if (other.intervals != null) {
                return false;
            }
        } else if (!intervals.equals(other.intervals)) {
            return false;
        }
        if (nOctaves != other.nOctaves) {
            return false;
        }
        if (relative != other.relative) {
            return false;
        }
        if (unit != other.unit) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SymParams [basePitch=").append(basePitch)
                .append(", intervals=").append(intervals)
                .append(", upAndDown=").append(upAndDown).append(", relative=")
                .append(relative).append(", unit=").append(unit)
                .append(", nOctaves=").append(nOctaves).append("]");
        return builder.toString();
    }

    /**
     * Serializaiton method.
     * 
     * @param in
     *            the input stream
     * @throws IOException
     *             if something went wrong
     * @throws ClassNotFoundException
     *             if the class is not found
     */
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        // our "pseudo-constructor"
        in.defaultReadObject();
        // now we are a "live" object again

    }

    public JsonObject toJSON() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Integer i : this.intervals) {
            jsonArrayBuilder.add(Json.createObjectBuilder().add("interval", i));
        }
        JsonArray intervalsJson = jsonArrayBuilder.build();

        JsonObject o = Json.createObjectBuilder()
                .add("basePitch", this.basePitch)
                .add("intervals", intervalsJson)
                .add("unit", this.unit)
                .add("nOctaves", this.nOctaves)
                .add("relative", this.relative)
                .build();
        return o;
    }
}
