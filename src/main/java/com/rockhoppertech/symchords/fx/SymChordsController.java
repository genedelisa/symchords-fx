package com.rockhoppertech.symchords.fx;

/*
 * #%L
 * symchords-fx
 * %%
 * Copyright (C) 2013 - 2014 Rockhopper Technologies
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

import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.rockhoppertech.collections.ListUtils;
import com.rockhoppertech.music.Duration;
import com.rockhoppertech.music.Pattern;
import com.rockhoppertech.music.PatternBuilder;
import com.rockhoppertech.music.PatternFactory;
import com.rockhoppertech.music.PitchFactory;
import com.rockhoppertech.music.PitchFormat;
import com.rockhoppertech.music.Timed;
import com.rockhoppertech.music.fx.cmn.GrandStaff;
import com.rockhoppertech.music.midi.js.MIDITrack;
import com.rockhoppertech.music.midi.js.MIDITrackFactory;
import com.rockhoppertech.music.midi.js.function.StartTimeFunction;
import com.rockhoppertech.music.modifiers.DurationModifier;
import com.rockhoppertech.music.modifiers.Modifier.Operation;
import com.rockhoppertech.music.modifiers.NoteModifier;
import com.rockhoppertech.music.modifiers.PitchModifier;
import com.rockhoppertech.music.modifiers.StartBeatModifier;

/**
 * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
 * 
 */
public class SymChordsController {
    private static final Logger logger = LoggerFactory
            .getLogger(SymChordsController.class);

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    private Stage stage;

    @FXML
    private AnchorPane root;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private RadioButton divideModifierCB;

    @FXML
    private RadioButton durationModifierCB;
    @FXML
    private RadioButton modModifierCB;

    @FXML
    private ToggleGroup modifierGroup;

    @FXML
    private TextField modifierValuesTextField;

    @FXML
    private RadioButton multiplyModifierCB;

    @FXML
    private ToggleGroup operationGroup;

    @FXML
    private RadioButton pitchModifierCB;

    @FXML
    private RadioButton setModifierCB;

    @FXML
    private RadioButton startBeatModifierCB;

    @FXML
    private RadioButton subtractModifierCB;

    @FXML
    private GrandStaff grandStaff;

    @FXML
    private GrandStaff patternStaff;

    @FXML
    private TextField IntervalsTextField;

    @FXML
    private ComboBox<String> baseOctaveCombo;

    @FXML
    private ComboBox<String> basePitchCombo;

    @FXML
    private Slider octavesSlider;

    @FXML
    private TextField patternTextField;

    @FXML
    private ToggleGroup relationshipGroup;

    @FXML
    private RadioButton relativeRadio;

    @FXML
    private RadioButton absoluteRadio;

    @FXML
    private Slider unitSlider;

    @FXML
    private Slider pitchSlider;

    @FXML
    private Label sliderText;

    @FXML
    private ListView<MIDITrack> trackList;

    @FXML
    private ListView<int[]> patternListView;

    @FXML
    private CheckBox patternReverseCB;

    @FXML
    private CheckBox patternSpreadCB;

    @FXML
    private CheckBox patternUpAndDownCB;

    @FXML
    private CheckBox patternUsePCCB;

    @FXML
    private RadioButton addModifierCB;

    private DurationModifier durationModifier = new DurationModifier();
    private StartBeatModifier startBeatModifier = new StartBeatModifier();
    private PitchModifier pitchModifier = new PitchModifier();
    private NoteModifier noteModifier = this.durationModifier;

    @FXML
    void durationModifierAction(ActionEvent event) {
        noteModifier = durationModifier;
        modifierValuesTextField.setText(MIDITrack
                .getDurationsAsString(model.getMIDITrack()));
    }

    @FXML
    void startBeatModifierAction(ActionEvent event) {
        noteModifier = startBeatModifier;
        modifierValuesTextField.setText(MIDITrack
                .getStartBeatsAsString(model.getMIDITrack()));
    }

    @FXML
    void pitchModifierAction(ActionEvent event) {
        noteModifier = pitchModifier;
        modifierValuesTextField.setText(MIDITrack
                .getPitchesMIDINumbersAsString(model.getMIDITrack()));
    }

    @FXML
    void modifyAction(ActionEvent event) {

        if (subtractModifierCB.isSelected()) {
            noteModifier.setOperation(NoteModifier.Operation.SUBTRACT);
        } else if (this.setModifierCB.isSelected()) {
            noteModifier.setOperation(NoteModifier.Operation.SET);
        } else if (this.modModifierCB.isSelected()) {
            noteModifier.setOperation(NoteModifier.Operation.MOD);
        } else if (this.divideModifierCB.isSelected()) {
            noteModifier.setOperation(NoteModifier.Operation.DIVIDE);
        } else if (this.multiplyModifierCB.isSelected()) {
            noteModifier.setOperation(NoteModifier.Operation.MULTIPLY);
        } else if (this.addModifierCB.isSelected()) {
            noteModifier.setOperation(NoteModifier.Operation.ADD);
        }

        double[] values = getModifierValues();
        if (values == null) {
            logger.debug("no modifier values");
            return;
        }
        logger.debug(ArrayUtils.toString(values));
        noteModifier.setValues(values);
        model.getMIDITrack().map(noteModifier);
        grandStaff.setTrack(model.getMIDITrack());
        grandStaff.drawShapes();

    }

    private double[] getModifierValues() {
        final String s = this.modifierValuesTextField.getText();
        if ((s == null) || s.equals("")) {
            logger.debug("null values");
            return null;
        }
        final String[] a = s.split("\\s+");
        final double[] array = new double[a.length];
        int i = 0;
        for (final String ss : a) {
            try {
                array[i++] = Double.parseDouble(ss);
            } catch (final Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
        return array;
    }

    @FXML
    void patternCBAction(ActionEvent event) {
        patternCalculate();
    }

    @FXML
    void exitAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private CheckBox chainCB;

    @FXML
    private CheckBox mirrorCB;

    @FXML
    private CheckBox playAsChordCB;

    @FXML
    void doitAction(ActionEvent event) {
        model.createTrack();
        MIDITrack track = model.getMIDITrack();
        track.sequential();

        if (this.pattern != null) {
            this.patternTextField.setText(ArrayUtils.toString(this.pattern));
            Pattern p = new PatternBuilder(track.getPitchClasses(),
                    this.pattern).build();
            this.patternedTrack = p.createTrack();
        } else {
            this.resetPattern(track.size());
        }
        updatePatternList();

        if (mirrorCB.isSelected()) {
            final MIDITrack inversion = this.model.getMIDITrack()
                    .getInversion();
            inversion.remove(0);
            track = model.getMIDITrack();
            inversion.append(track);
            inversion.sortByAscendingPitches();
            inversion.sequential();
            this.model.setMIDITrack(inversion);
        }

        if (this.chainCB.isSelected()) {
            // track = this.model.getMIDITrack();
            track = new MIDITrack();
            for (Integer midiNumber : this.model.getMIDITrack()
                    .getPitchesAsIntegers()) {
                MIDITrack list = MIDITrackFactory.createFromIntervals(
                        this.model.getIntervals(),
                        midiNumber,
                        this.model.getUnit(),
                        this.model.getAbsolute(),
                        this.model.getNOctaves());
                if (mirrorCB.isSelected()) {
                    final MIDITrack inversion = list.getInversion();
                    inversion.remove(0);
                    list = inversion.append(list);
                    list.sortByAscendingPitches();
                }
                System.err.println(list);
                track.append(list);

            }
            // model.setMIDITrack(this.model.getMIDITrack().append(list));
            model.setMIDITrack(track);
        }
        // track = model.getMIDITrack();
        // grandStaff.setTrack(track);
        // grandStaff.drawShapes();
        logger.debug("track {}", track);
        logger.debug("track {}", track.getDescription());
    }

    /*
     
     */

    @FXML
    void playAction(ActionEvent event) {
        MIDITrack track = model.getMIDITrack();

        if (this.playAsChordCB.isSelected()) {
            // StartTimeFunction function = new StartTimeFunction();
            // function.setOperation(Operation.ADD);
            // List<Timed> newnotes = Lists.transform(track.getNotes(),
            // function);

            StartBeatModifier sbm = new StartBeatModifier(1d);
            sbm.setOperation(Operation.SET);
            track.map(sbm);
        } else {
            track.sequential();
        }

        track.play();

        //if (((Node) event.getSource()).getId().equals("")) {
        //}
    }

    @FXML
    void playSelectedAction(ActionEvent event) {
        grandStaff.getMIDITrack().play();
    }

    private SymModel model;
    MIDITrack patternedTrack;

    private IntegerProperty pitchProperty = new SimpleIntegerProperty(60);

    @FXML
    void initialize() {
        assert IntervalsTextField != null : "fx:id=\"IntervalsTextField\" was not injected: check your FXML file 'symchords.fxml'.";
        assert baseOctaveCombo != null : "fx:id=\"baseOctaveCombo\" was not injected: check your FXML file 'symchords.fxml'.";
        assert basePitchCombo != null : "fx:id=\"basePitchCombo\" was not injected: check your FXML file 'symchords.fxml'.";
        assert octavesSlider != null : "fx:id=\"octavesSlider\" was not injected: check your FXML file 'symchords.fxml'.";
        assert patternTextField != null : "fx:id=\"patternTextField\" was not injected: check your FXML file 'symchords.fxml'.";
        assert relationshipGroup != null : "fx:id=\"relationshipGroup\" was not injected: check your FXML file 'symchords.fxml'.";
        assert relativeRadio != null : "fx:id=\"relativeRadio\" was not injected: check your FXML file 'symchords.fxml'.";
        assert unitSlider != null : "fx:id=\"unitSlider\" was not injected: check your FXML file 'symchords.fxml'.";

        grandStaff.setFontSize(24d);
        grandStaff.drawShapes();
        patternStaff.setFontSize(24d);
        patternStaff.drawShapes();

        baseOctaveCombo.getSelectionModel().select(5);
        basePitchCombo.getSelectionModel().select(0);

        pitchProperty.bindBidirectional(pitchSlider.valueProperty());

        // sliderText.setText(Math.round(pitchSlider.getValue()) + "");
        PitchFormat.getInstance().setWidth(4);
        sliderText.setText(PitchFormat.getInstance().format(
                (int) pitchSlider.getValue()));
        pitchSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue, Number newValue) {
                if (newValue == null) {
                    sliderText.setText("");
                    return;
                }
                basePitchCombo.setValue(PitchFormat.getPitchString(newValue
                        .intValue()));
                baseOctaveCombo.setValue("" + newValue.intValue() / 12);
                sliderText.setText(PitchFormat.getInstance().format(
                        newValue.intValue()));

                // sliderText.setText(Math.round(newValue.intValue()) + "");
            }
        });

        basePitchCombo.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov,
                            String oldValue, String newValue) {

                        int pitch = PitchFactory.getPitch(newValue + "0")
                                .getMidiNumber();
                        int oct = Integer.parseInt(baseOctaveCombo
                                .getSelectionModel().getSelectedItem());
                        pitchProperty.set(pitch + (oct * 12));
                    }
                });
        baseOctaveCombo.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> ov,
                            String oldValue, String newValue) {
                        int oct = Integer.parseInt(newValue);
                        int pitch = PitchFactory.getPitch(basePitchCombo
                                .getSelectionModel().getSelectedItem() + "0")
                                .getMidiNumber();
                        pitchProperty.set(pitch + (oct * 12));
                    }
                });

        // java 8
        // basePitchCombo.getSelectionModel().selectedItemProperty().addListener(
        // (ObservableValue<? extends String> ov, String oldValue, String
        // newValue) -> {
        // int pitch = Integer.parseInt(newValue);
        // int oct =
        // Integer.parseInt(baseOctaveCombo.getSelectionModel().getSelectedItem());
        // pitchProperty.set(pitch + oct);
        // });
        // baseOctaveCombo.getSelectionModel().selectedItemProperty().addListener(
        // (ObservableValue<? extends String> ov, String oldValue, String
        // newValue) -> {
        // int oct = Integer.parseInt(newValue);
        // int pitch =
        // Integer.parseInt(basePitchCombo.getSelectionModel().getSelectedItem());
        // pitchProperty.set(pitch + oct);
        // });

        model = new SymModel();

        model.absoluteProperty().bind(this.absoluteRadio.selectedProperty());
        // model.relativeProperty().bind(this.relativeRadio.selectedProperty());
        model.basePitchProperty().bind(this.pitchProperty);
        model.nOctavesProperty().bind(this.octavesSlider.valueProperty());
        model.unitProperty().bind(this.unitSlider.valueProperty());

        // set up initial value
        ObservableList<Integer> ol = FXCollections.observableArrayList();
        ol.add(1);
        setIntervals(ol);
        // now handle input. turn the string into an array.
        this.IntervalsTextField.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> arg0,
                            String oldValue, String newValue) {
                        String[] a = newValue.split("\\s+");
                        ObservableList<Integer> ol = FXCollections
                                .observableArrayList();
                        for (final String ss : a) {
                            ol.add(Integer.parseInt(ss));
                        }
                        setIntervals(ol);
                    }
                });
        model.intervalsProperty().bind(this.intervalsProperty);

        this.resetPattern();

        this.patternTextField.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> arg0,
                            String oldValue, String newValue) {
                        logger.debug("new value '{}'", newValue);
                        newValue = newValue.replaceAll("\\{", "");
                        newValue = newValue.replaceAll("\\}", "");
                        newValue = newValue.replaceAll(",", " ");
                        logger.debug("new value '{}'", newValue);

                        String[] a = newValue.split("\\s+");
                        ObservableList<Integer> ol = FXCollections
                                .observableArrayList();
                        for (final String ss : a) {
                            ol.add(Integer.parseInt(ss));
                        }
                        // setIntervals(ol);
                    }
                });

        patternListView.setCellFactory(new Callback<ListView<int[]>,
                ListCell<int[]>>() {
                    @Override
                    public ListCell<int[]> call(ListView<int[]> list) {
                        return new PatternListCell();
                    }
                });
        updatePatternList();

        patternListView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<int[]>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends int[]> observable,
                            int[] oldValue, int[] newValue) {
                        pattern = newValue;
                        patternCalculate();
                        patternTextField.setText(ArrayUtils.toString(pattern));
                    }
                });

        setupDragonDrop();

        trackList.setCellFactory(new Callback<ListView<MIDITrack>,
                ListCell<MIDITrack>>() {
                    @Override
                    public ListCell<MIDITrack> call(ListView<MIDITrack> list) {
                        return new TrackListCell();
                    }
                });
        trackList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<MIDITrack>() {

                    @Override
                    public void changed(
                            ObservableValue<? extends MIDITrack> observable,
                            MIDITrack oldValue, MIDITrack newValue) {

                        model.setMIDITrack(newValue);

                        // SymParams params = model.getParamsForTrack(newValue);
                        SymParams params = (SymParams) newValue.getUserData();
                        logger.debug("params {}", params);
                        if (params != null) {
                            int pitch = params.getBasePitch() % 12;
                            String ps = PitchFormat.getPitchString(pitch);
                            basePitchCombo.setValue(ps);

                            String oct = "" + params.getBasePitch() / 12;
                            baseOctaveCombo.setValue(oct);

                            String intervals = ListUtils.asIntString(params
                                    .getIntervals());
                            IntervalsTextField.setText(intervals);

                            octavesSlider.setValue(params.getnOctaves());
                            unitSlider.setValue(params.getUnit());
                            relativeRadio.setSelected(params.isRelative());
                            absoluteRadio.setSelected(!params.isRelative());

                        }
                    }
                });

        model.midiTrackProperty().addListener(new ChangeListener<MIDITrack>() {
            @Override
            public void changed(ObservableValue<? extends MIDITrack> arg0,
                    MIDITrack arg1, MIDITrack newTrack) {

                if (durationModifierCB.isSelected()) {
                    modifierValuesTextField.setText(MIDITrack
                            .getDurationsAsString(newTrack));
                }
                if (pitchModifierCB.isSelected()) {
                    modifierValuesTextField.setText(MIDITrack
                            .getPitchesMIDINumbersAsString(newTrack));
                }
                if (startBeatModifierCB.isSelected()) {
                    modifierValuesTextField.setText(MIDITrack
                            .getStartBeatsAsString(newTrack));
                }

                logger.debug("midi track property changed {}", newTrack);
                grandStaff.setTrack(newTrack);
                grandStaff.drawShapes();
            }
        });

    }

    private final ListProperty<Integer> intervalsProperty = new SimpleListProperty<>();

    public void setIntervals(ObservableList<Integer> ol) {
        this.intervalsProperty.set(ol);
    }

    private int[] pattern;

    void resetPattern(int length) {
        pattern = new int[length];
        for (int i = 0; i < length; i++) {
            pattern[i] = i;
        }
    }

    void resetPattern() {
        resetPattern(model.getMIDITrack().size());
        this.patternTextField.setText(ArrayUtils.toString(this.pattern));
    }

    void updatePatternList() {
        int limit = 7;
        int size = 6;
        List<int[]> patterns = null;

        if (this.model.getMIDITrack() != null) {
            size = model.getMIDITrack().size();
            patterns = PatternFactory.getPatterns(size, size);
        } else {
            patterns = PatternFactory.getPatterns(limit, size);
        }

        ObservableList<int[]> items = FXCollections.observableArrayList();
        this.patternListView.setItems(items);
        for (int[] a : patterns) {
            items.add(a);
        }
    }

    /**
     * Make the array readable as a String.
     * 
     * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
     * 
     */
    static class PatternListCell extends ListCell<int[]> {
        @Override
        public void updateItem(int[] item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < item.length; i++) {
                    sb.append(item[i]).append(' ');
                }
                this.setText(sb.toString());
            }
        }
    }

    /**
     * Make the track readable as a String.
     * 
     * @author <a href="http://genedelisa.com/">Gene De Lisa</a>
     * 
     */
    static class TrackListCell extends ListCell<MIDITrack> {
        @Override
        public void updateItem(MIDITrack item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                this.setText(item.getName() + "-" + System.currentTimeMillis());
            }
        }
    }

    public void patternCalculate() {
        boolean reverse = patternReverseCB.isSelected();
        boolean upAndDown = patternUpAndDownCB.isSelected();
        boolean spread = patternSpreadCB.isSelected();
        boolean usePitchClasses = patternUsePCCB.isSelected();

        int nOctaves = 1;
        double duration = Duration.Q;
        double restBetweenPatterns = Duration.SIXTEENTH_NOTE;

        this.pattern = patternListView.getSelectionModel().getSelectedItem();

        if (this.pattern != null && model.getMIDITrack() != null) {

            Pattern p = null;

            if (usePitchClasses) {
                int[] degrees = Ints.toArray(model.getMIDITrack()
                        .getPitchClasses());
                p = PatternBuilder.create()
                        .degrees(degrees)
                        .pattern(this.pattern)
                        .duration(duration)
                        .numOctaves(nOctaves)
                        .reverse(reverse)
                        .upAndDown(upAndDown)
                        .build();
                spread = true;
            } else {

                List<Integer> pitches = model.getMIDITrack()
                        .getPitchesAsIntegers();

                p = PatternBuilder.create()
                        .pattern(pattern)
                        .degrees(Ints.toArray(pitches))
                        .duration(duration)
                        .numOctaves(nOctaves)
                        .startPitch(pitches.get(0))
                        .reverse(reverse)
                        .upAndDown(upAndDown)
                        .restBetweenPatterns(restBetweenPatterns)
                        .build();

            }

            this.patternedTrack = p.createTrack(0d, spread);
            this.patternStaff.setTrack(this.patternedTrack);
            patternStaff.drawShapes();
            updatePatternList();
            logger.debug("pattern {}", p);
            logger.debug("patterned track {}", this.patternedTrack);
        } else {
            this.resetPattern(model.getMIDITrack().size());
        }

    }

    // dragon drop
    private ImageView dragImageView;

    protected void setupDragonDrop() {
        Image image = new Image(getClass().getResourceAsStream(
                "/images/rocky-32-trans.png"));
        dragImageView = new ImageView(image);
        dragImageView.setFitHeight(32);
        dragImageView.setFitWidth(32);

        grandStaff.setOnDragDetected(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {

                if (!root.getChildren().contains(dragImageView)) {
                    root.getChildren().add(dragImageView);
                }

                // dragImageView.setOpacity(0.5);
                dragImageView.toFront();
                dragImageView.setMouseTransparent(true);
                dragImageView.setVisible(true);
                dragImageView.relocate(
                        (int) (me.getSceneX() - dragImageView
                                .getBoundsInLocal().getWidth() / 2),
                        (int) (me.getSceneY() - dragImageView
                                .getBoundsInLocal().getHeight() / 2));

                Dragboard db = grandStaff.startDragAndDrop(TransferMode.ANY);

                // TODO remove the custom image nonsense in javafx 8
                // javafx 8
                // db.setDragView(dragImageView);

                ClipboardContent content = new ClipboardContent();
                // MIDITrack track = grandStaff.getMIDITrack();
                MIDITrack track = model.getMIDITrack();
                content.put(midiTrackDataFormat, track);
                db.setContent(content);
                me.consume();
            }
        });

        grandStaff.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent e) {
                dragImageView.setVisible(false);
                e.consume();
            }
        });

        // Parent root = grandStaff.getScene().getRoot();
        // stage.getScene().getRoot();

        if (root != null) {
            root.setOnDragOver(new EventHandler<DragEvent>() {
                public void handle(DragEvent e) {
                    Point2D localPoint = grandStaff
                            .getScene()
                            .getRoot()
                            .sceneToLocal(
                                    new Point2D(e.getSceneX(), e
                                            .getSceneY()));
                    dragImageView.relocate(
                            (int) (localPoint.getX() - dragImageView
                                    .getBoundsInLocal().getWidth() / 2),
                            (int) (localPoint.getY() - dragImageView
                                    .getBoundsInLocal().getHeight() / 2));
                    e.consume();
                }
            });
        }

        trackList.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /*
                 * data is dragged over the target; accept it only if it is not
                 * dragged from the same node and if it has MIDITrack data
                 */
                if (event.getGestureSource() != trackList &&
                        event.getDragboard().hasContent(midiTrackDataFormat)) {
                    logger.debug("drag over");
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }

                // Don't consume the event. Let the layers below process the
                // DragOver event as well so that the
                // translucent container image will follow the cursor.
                // event.consume();
            }
        });

        trackList.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
                /* show to the user that it is an actual gesture target */
                logger.debug("drag entered");
                if (event.getGestureSource() != trackList &&
                        event.getDragboard().hasContent(midiTrackDataFormat)) {
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(5.0);
                    dropShadow.setOffsetX(3.0);
                    dropShadow.setOffsetY(3.0);
                    dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
                    trackList.setEffect(dropShadow);
                }
                event.consume();
            }
        });

        trackList.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */
                trackList.setEffect(null);
                event.consume();
            }
        });

        trackList.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasContent(midiTrackDataFormat)) {
                    MIDITrack track = (MIDITrack) db
                            .getContent(midiTrackDataFormat);
                    trackList.getItems().add(track);
                    success = true;

                }
                /*
                 * let the source know whether the data was successfully
                 * transferred and used
                 */
                event.setDropCompleted(success);
                event.consume();
            }
        });

        logger.debug("jvm mime {}", DataFlavor.javaJVMLocalObjectMimeType);
    }

    /**
     * For Dragon drop.
     * <p>
     * The string HAS to be in this format, otherwise on OSX, you'll get
     * "Cannot set data for an invalid UTI"
     */
    private static final DataFormat midiTrackDataFormat = new DataFormat(
            "com.rockhoppertech.music.midi.js.MIDITrack");

}
