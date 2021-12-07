package org.newdawn.slick.svg;

import java.util.ArrayList;
import org.newdawn.slick.geom.MorphShape;
import org.newdawn.slick.svg.Diagram;
import org.newdawn.slick.svg.Figure;

public class SVGMorph
extends Diagram {
    private ArrayList figures = new ArrayList();

    public SVGMorph(Diagram diagram) {
        super(diagram.getWidth(), diagram.getHeight());
        for (int i = 0; i < diagram.getFigureCount(); ++i) {
            Figure figure = diagram.getFigure(i);
            Figure copy = new Figure(figure.getType(), new MorphShape(figure.getShape()), figure.getData(), figure.getTransform());
            this.figures.add(copy);
        }
    }

    public void addStep(Diagram diagram) {
        if (diagram.getFigureCount() != this.figures.size()) {
            throw new RuntimeException("Mismatched diagrams, missing ids");
        }
        block0: for (int i = 0; i < diagram.getFigureCount(); ++i) {
            Figure figure = diagram.getFigure(i);
            String id = figure.getData().getMetaData();
            for (int j = 0; j < this.figures.size(); ++j) {
                Figure existing = (Figure)this.figures.get(j);
                if (!existing.getData().getMetaData().equals(id)) continue;
                MorphShape morph = (MorphShape)existing.getShape();
                morph.addShape(figure.getShape());
                continue block0;
            }
        }
    }

    public void setExternalDiagram(Diagram diagram) {
        block0: for (int i = 0; i < this.figures.size(); ++i) {
            Figure figure = (Figure)this.figures.get(i);
            for (int j = 0; j < diagram.getFigureCount(); ++j) {
                Figure newBase = diagram.getFigure(j);
                if (!newBase.getData().getMetaData().equals(figure.getData().getMetaData())) continue;
                MorphShape shape = (MorphShape)figure.getShape();
                shape.setExternalFrame(newBase.getShape());
                continue block0;
            }
        }
    }

    public void updateMorphTime(float delta) {
        for (int i = 0; i < this.figures.size(); ++i) {
            Figure figure = (Figure)this.figures.get(i);
            MorphShape shape = (MorphShape)figure.getShape();
            shape.updateMorphTime(delta);
        }
    }

    public void setMorphTime(float time) {
        for (int i = 0; i < this.figures.size(); ++i) {
            Figure figure = (Figure)this.figures.get(i);
            MorphShape shape = (MorphShape)figure.getShape();
            shape.setMorphTime(time);
        }
    }

    @Override
    public int getFigureCount() {
        return this.figures.size();
    }

    @Override
    public Figure getFigure(int index) {
        return (Figure)this.figures.get(index);
    }
}
