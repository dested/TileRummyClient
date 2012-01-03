package com.TileRummy;


import android.graphics.Rect;
import android.graphics.RectF;

public class Rectangle {
    public float X;
    public float Y;
    public float Width;
    public float Height;

    public Rectangle(float x, float y, float width, float height) {
        X = x;
        Y = y;
        Width = width;
        Height = height;

    }

    public Rectangle(Rectangle tileArea) {
        X=tileArea.X;
        Y=tileArea.Y;
        Width=tileArea.Width;
        Height=tileArea.Height;
    }

    public boolean Collides(float x2, float y2) {
        return (X < x2 && X + Width > x2 && Y < y2 && Y + Height > y2);

    }

    public boolean Collides(Point p) {
        return Collides(p.X, p.Y);

    }

    public Rect toRect() {
        return new Rect((int)X, (int)Y, (int)X + (int)Width, (int)Y + (int)Height);
    }

    public float Bottom() {
        return Y + Height;
    }

    public void Bottom(float set) {
        Height = set - Y;
    }

    public float Right() {
        return X + Width;
    }

    public void Right(float set) {
        Width = set - X;
    }

    public RectF toRectF() {
        return new RectF(X, Y, X + Width, Y + Height);
    }
}
