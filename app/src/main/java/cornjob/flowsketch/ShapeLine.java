package cornjob.flowsketch;

import android.graphics.Path;

class ShapeLine extends Object {

    public enum ltype {
        WHOLE, START, END
    }

    Point endPoint;
    ltype currentltype;

    ShapeLine(MyCanvas mainCanvas, OBJTYPE type, float x1, float y1, float x2, float y2) {
        super(mainCanvas, x1, y1, type);
        endPoint = new Point(x2, y2);

        currentltype = ltype.WHOLE;
    }

    ShapeLine(MyCanvas mainCanvas, String inString) {
        super(mainCanvas, inString);
        String[] stuff = DECODE(inString);

        endPoint = new Point(Float.parseFloat(stuff[7]), Float.parseFloat(stuff[8]));

        currentltype = ltype.WHOLE;
    }

    @Override
    public boolean drawThis() {
        float deltaX = endPoint.getX() - objOrigin.getX();
        float deltaY = endPoint.getY() - objOrigin.getY();
        float frac = (float) 0.1;

        float point_x_1 = objOrigin.getX() + (1 - frac) * deltaX + frac * deltaY;
        float point_y_1 = objOrigin.getY() + (1 - frac) * deltaY - frac * deltaX;

        float point_x_2 = endPoint.getX();
        float point_y_2 = endPoint.getY();

        float point_x_3 = objOrigin.getX() + (1 - frac) * deltaX - frac * deltaY;
        float point_y_3 = objOrigin.getY() + (1 - frac) * deltaY + frac * deltaX;

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(point_x_1, point_y_1);
        path.lineTo(point_x_2, point_y_2);
        path.lineTo(point_x_3, point_y_3);
        path.lineTo(point_x_1, point_y_1);
        path.lineTo(point_x_1, point_y_1);
        path.close();

        if (objSelect) {
            updateSelectBorder();
            objCanvas.canvas.drawLine(objOrigin.getX(), objOrigin.getY(), endPoint.getX(), endPoint.getY(), selectBorder);
            objCanvas.canvas.drawPath(path, selectBorder);
        }

        objCanvas.canvas.drawLine(objOrigin.getX(), objOrigin.getY(), endPoint.getX(), endPoint.getY(), objPaintCurrent_Stroke);
        objCanvas.canvas.drawPath(path, objPaintCurrent_Fill);

        return true;
    }

    @Override
    public boolean contains(Point test) {
        Point closest = closestpointonline(objOrigin.getX(), objOrigin.getY(), endPoint.getX(), endPoint.getY(), test.getX(), test.getY());
        float distance = Point.distance(closest, test);
        return distance < 30;
    }

    @Override
    public void translate(float xdis, float ydis) {
        objOrigin.move(xdis, ydis);
        endPoint.move(xdis, ydis);
    }

    @Override
    public void scale(float factor) {
        //no time
    }


    @Override
    public String encode() {
        return ENCODE(objType, objOrigin.getX(), objOrigin.getY(), objPaintCurrent_Fill.getColor(), objPaintCurrent_Stroke.getColor(), -1, -1, endPoint.getX(), endPoint.getX(), -1, -1, "", "", -1, "");
    }

    static Point closestpointonline(float lx1, float ly1,
                                    float lx2, float ly2, float x0, float y0) {
        float A1 = ly2 - ly1;
        float B1 = lx1 - lx2;
        double C1 = (ly2 - ly1) * lx1 + (lx1 - lx2) * ly1;
        double C2 = -B1 * x0 + A1 * y0;
        double det = A1 * A1 - -B1 * B1;
        double cx;
        double cy;
        if (det != 0) {
            cx = (float) ((A1 * C1 - B1 * C2) / det);
            cy = (float) ((A1 * C2 - -B1 * C1) / det);
        } else {
            cx = x0;
            cy = y0;
        }
        return new Point((float) cx, (float) cy);
    }

    public ltype toggleType() {
        switch (currentltype) {
            case WHOLE:
                currentltype = ltype.START;
                break;
            case START:
                currentltype = ltype.END;
                break;
            case END:
                currentltype = ltype.WHOLE;
                break;
        }
        return currentltype;
    }

}

