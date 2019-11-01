package com.hyd.dao.mate.swing.layout;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.SpringLayout;

public class SpringLayoutHelper {

    private SpringLayout layout;

    private Component c1, c2;

    private Edge edge1, edge2;

    private int padding;

    public static SpringLayoutHelper of(JComponent component) {
        return new SpringLayoutHelper(component);
    }

    private SpringLayoutHelper(JComponent component) {
        if (component.getLayout() != null && component.getLayout() instanceof SpringLayout) {
            this.layout = (SpringLayout) component.getLayout();
        } else {
            this.layout = new SpringLayout();
            component.setLayout(layout);
        }
    }

    private void apply() {
        this.layout.putConstraint(edge1.value, c1, padding, edge2.value, c2);
    }

    private static void apply(SpringLayout layout, Component c1, Edge e1, Component c2, Edge e2, int padding) {
        layout.putConstraint(e1.value, c1, padding, e2.value, c2);
    }

    public enum Edge {
        TOP(SpringLayout.NORTH),
        RIGHT(SpringLayout.EAST),
        BOTTOM(SpringLayout.SOUTH),
        LEFT(SpringLayout.WEST);

        private String value;

        Edge(String value) {
            this.value = value;
        }
    }

    public PutContext leftOf(Component c1) {
        this.c1 = c1;
        this.edge1 = Edge.LEFT;
        return new PutContext();
    }

    public PutContext rightOf(Component c1) {
        this.c1 = c1;
        this.edge1 = Edge.RIGHT;
        return new PutContext();
    }

    public PutContext topOf(Component c1) {
        this.c1 = c1;
        this.edge1 = Edge.TOP;
        return new PutContext();
    }

    public PutContext bottomOf(Component c1) {
        this.c1 = c1;
        this.edge1 = Edge.BOTTOM;
        return new PutContext();
    }

    public void paddingInside(Component parent, Component child, int padding, Edge... edges) {
        for (Edge edge : edges) {
            int finalPadding = edge == Edge.RIGHT || edge == Edge.BOTTOM ? -padding : padding;
            apply(this.layout, child, edge, parent, edge, finalPadding);
        }
    }

    public class PutContext {

        public OffsetContext toLeftOf(Component c2) {
            SpringLayoutHelper.this.c2 = c2;
            SpringLayoutHelper.this.edge2 = Edge.LEFT;
            return new OffsetContext();
        }

        public OffsetContext toRightOf(Component c2) {
            SpringLayoutHelper.this.c2 = c2;
            SpringLayoutHelper.this.edge2 = Edge.RIGHT;
            return new OffsetContext();
        }

        public OffsetContext toTopOf(Component c2) {
            SpringLayoutHelper.this.c2 = c2;
            SpringLayoutHelper.this.edge2 = Edge.TOP;
            return new OffsetContext();
        }

        public OffsetContext toBottomOf(Component c2) {
            SpringLayoutHelper.this.c2 = c2;
            SpringLayoutHelper.this.edge2 = Edge.BOTTOM;
            return new OffsetContext();
        }
    }

    public class OffsetContext {

        public void padding(int offset) {
            SpringLayoutHelper.this.padding = offset;
            apply();
        }
    }
}
