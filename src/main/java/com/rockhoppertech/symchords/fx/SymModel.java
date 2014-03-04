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

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;
import com.rockhoppertech.music.midi.js.MIDITrack;
import com.rockhoppertech.music.midi.js.MIDITrackFactory;

public class SymModel {
    private static final Logger logger = LoggerFactory
            .getLogger(SymModel.class);

    private final IntegerProperty unitProperty = new SimpleIntegerProperty(1);
    private final IntegerProperty noctavesProperty = new SimpleIntegerProperty(
            1);
    private final BooleanProperty absoluteProperty = new SimpleBooleanProperty(
            true);
    private final BooleanProperty relativeProperty = new SimpleBooleanProperty(
            true);

    private final IntegerProperty basePitchProperty = new SimpleIntegerProperty();
    private final ObjectProperty<MIDITrack> midiTrackProperty = new SimpleObjectProperty<>();
    private final ObservableList<Integer> observableList = FXCollections
            .observableArrayList();
    private final ListProperty<Integer> intervalsProperty = new SimpleListProperty<>(
            observableList);

    public SymModel() {
        this.createTrack();
    }
    
    public IntegerProperty unitProperty() {
        return unitProperty;
    }
    public BooleanProperty relativeProperty() {
        return relativeProperty;
    }

    public int getUnit() {
        return unitProperty.get();
    }

    public IntegerProperty nOctavesProperty() {
        return noctavesProperty;
    }

    public int getNOctaves() {
        return noctavesProperty.get();
    }

    public BooleanProperty absoluteProperty() {
        return absoluteProperty;
    }

    public boolean getAbsolute() {
        return absoluteProperty.get();
    }

    public IntegerProperty basePitchProperty() {
        return basePitchProperty;
    }

    public int getBasePitch() {
        return this.basePitchProperty().get();
    }

    public ObjectProperty<MIDITrack> midiTrackProperty() {
        return midiTrackProperty;
    }

    public MIDITrack getMIDITrack() {
        return this.midiTrackProperty().get();
    }

    public void setMIDITrack(MIDITrack track) {
        this.midiTrackProperty().set(track);
    }

    public ListProperty<Integer> intervalsProperty() {
        return intervalsProperty;
    }

    public List<Integer> getIntervals() {
        return intervalsProperty.get();
    }

    public void createTrack() {

        int[] a = Ints.toArray(this.getIntervals());
        this.setMIDITrack(
                MIDITrackFactory.createFromIntervals(
                        a,
                        // this.getIntervals(),
                        this.getBasePitch(),
                        this.getUnit(),
                        this.getAbsolute(),
                        this.getNOctaves()));

        getMIDITrack().setName("Symmetrical");
        final String s = String
                .format(
                        "base midi pitch=%d intervals='%s' unit=%d num octaves=%d absolute=%b",
                        this.getBasePitch(),
                        ArrayUtils.toString(this.getIntervals()),
                        this.getUnit(),
                        this.getNOctaves(),
                        this.getAbsolute()
                );
        getMIDITrack().setDescription(s);
        logger.debug(s);
    }
}
