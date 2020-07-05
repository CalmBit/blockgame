package blockgame.render.gui;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class GuiGrid extends GuiWidget {
    private int _columns;
    private int _rows;
    private float _horizontalUnit = 0.0f;
    private float _verticalUnit = 0.0f;
    private final List<GridEntry> _children = new ArrayList<>();

    private static class GridEntry {
        private final GuiWidget _widget;
        public int column;
        public int row;

        public GridEntry(GuiWidget widget, int column, int row) {
            _widget = widget;
            this.column = column;
            this.row = row;
        }
    }

    public GuiGrid(int columns, int rows) {
        super(new Vector2f(0,0), new Vector2f(GuiRenderer.getScreenWidth(), GuiRenderer.getScreenHeight()));
        resizesSelf = false;
        _columns = columns;
        _rows = rows;
        _horizontalUnit = size.x / (float)_columns;
        _verticalUnit = size.y / (float)_rows;
    }

    public void addChildAt(GuiWidget c, int column, int row) {
        GridEntry e = new GridEntry(c, column, row);
        resizeChild(e);
        _children.add(e);
    }

    public void mouseMovement(float x, float y) {
        for (GridEntry e : _children) {
            e._widget.mouseMovement(x,y);
        }
    }

    public void mouseClick(int button, int action) {
        for (GridEntry e : _children) {
            e._widget.mouseClick(button, action);
        }
    }

    public void render(Matrix4f proj) {
        for (GridEntry e : _children) {
            e._widget.render(proj);
        }
    }

    @Override
    public void setSize(Vector2f size) {
        super.setSize(size);
        _horizontalUnit = size.x / (float)_columns;
        _verticalUnit = size.y / (float)_rows;
        for(GridEntry e : _children) {
            resizeChild(e);
        }
    }

    public void resizeChild(GridEntry e) {
        e._widget.setPos(new Vector2f(pos.x + (e.column * _horizontalUnit), pos.y + (e.row * _verticalUnit)));
        if(!e._widget.resizesSelf) {
            e._widget.setSize(new Vector2f(_horizontalUnit, _verticalUnit));
        }
    }
}
