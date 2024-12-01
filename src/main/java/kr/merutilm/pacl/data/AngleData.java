package kr.merutilm.pacl.data;

import java.util.ArrayList;
import java.util.List;

import kr.merutilm.pacl.al.event.events.action.Actions;
import kr.merutilm.pacl.al.event.events.decoration.Decorations;

public class AngleData{

    private double angle;

    private final List<Actions> actions;

    private final List<Decorations> decorations;

    public AngleData(double angle) {
        this.angle = angle;
        this.actions = new ArrayList<>();
        this.decorations = new ArrayList<>();
    }

    public AngleData(AngleData data) {
        this.angle = data.angle;
        this.actions = new ArrayList<>(data.actions); //Actions << immutable
        this.decorations = new ArrayList<>(data.decorations); //Decorations << immutable
    }

    public double angle() {
        return angle;
    }

    public List<Actions> currentTileActions() {
        return actions;
    }

    public List<Decorations> currentTileDecorations() {
        return decorations;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void rotate(double angle) {
        this.angle += angle;
    }

    public void invX() {
        this.angle *= -1;
    }

    public void invY() {
        this.angle = 180 - this.angle;
    }

    public void rotate90() {
        this.angle += 90;
    }

    public void rotate180() {
        this.angle += 180;
    }

    public void rotate270() {
        this.angle += 270;
    }
}
