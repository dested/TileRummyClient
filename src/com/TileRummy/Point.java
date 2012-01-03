package com.TileRummy;

import java.io.Serializable;

public class Point implements Serializable {
    public float X;
    public float Y;

    public Point(float x, float y) {
        X = x;
        Y = y;
    }

    public Point(Point pm) {
        X = pm.X;
        Y = pm.Y;
    }

    public static Point Clone(Point point) {
        return new Point(point.X, point.Y);
    }

    public void Offset(float i, float j) {
        X += i;
        Y += j;

    }

    public void Offset(Point p) {
        X += p.X;
        Y += p.Y;

    }

    public boolean equals(Point p) {
        return p.X == X && p.Y == Y;

    }

    public int hashCode() {
        return (int) X ^ (int) Y;

    }

    public static Point Difference(Point lastMous2, Point point) {
        return new Point((lastMous2.X - point.X), (lastMous2.Y - point.Y));
    }

    public void Combine(Point point) {
        X += point.X;
        Y += point.Y;

    }

    public Point Negate(Point point) {
        X -= point.X;
        Y -= point.Y;
        return new Point(X, Y);

    }

    public Point Negate(float x, float y) {
        X -= x;
        Y -= y;
        return new Point(X, Y);
    }

    public Point Negative() {
        return new Point(-X, -Y);
    }

    public void reduce(int i) {
        //
        if (X > 0) {
            X -= i;
            if (X < 0) {
                X = 0;
            }
        }
        if (X < 0) {
            X += i;
            if (X > 0) {
                X = 0;
            }
        }
        if (Y > 0) {
            Y -= i;
            if (Y < 0) {
                Y = 0;
            }
        }
        if (Y < 0) {
            Y += i;
            if (Y > 0) {
                Y = 0;
            }
        }

    }

    public boolean Zero() {
        return X == 0 && Y == 0;
    }

    public void Magnify(float b) {
        X *= b;
        Y *= b;

    }

    public static Point Min(Point mouseVector, Point point) {
        float x = 0;
        float y = 0;
        if (mouseVector.X < point.X)
            x = mouseVector.X;
        else
            x = point.X;
        if (mouseVector.Y < point.Y)
            y = mouseVector.Y;
        else
            y = point.Y;
        return new Point(x, y);
    }

    public void OffsetToZero(Point point) {
        if(X ==0 ){

        }   else
        if (X > 0) {
            X -= point.X;
            if (X < 0) X = 0;
        }
        else if (X < 0) {
            X += point.X;
            if (X > 0) X = 0;
        }


        if(Y ==0 ){

        }   else
        if (Y > 0) {
            Y -= point.Y;
            if (Y < 0) Y = 0;
        }
        else if (Y < 0) {
            Y += point.Y;
            if (Y > 0) Y = 0;
        }


    }

    public Rectangle toRectangle( float width, float height) {
        return new Rectangle(this,width, height);
    }

    public void Limit(float v) {
        if(X ==0 ){

        }   else
        if (X > v) {
            X = v;
        }
        else if (X < -v) {
            X = -v;
        }


        if(Y ==0 ){

        }   else
        if (Y > v) {
            Y = v;
        }
        else if (Y < -v) {
            Y = -v;
        }
    }
}
