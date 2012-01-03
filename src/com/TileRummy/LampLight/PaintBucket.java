package com.TileRummy.LampLight;

import android.graphics.Paint;

import java.util.HashMap;

public class PaintBucket {
	private HashMap<String, Paint> hs = new HashMap<String, Paint>();

	public Paint GetPaint(String n) {
		return hs.get(n);
	}

	public void AddPaint(String n, Paint p) {
		hs.put(n, p);
	}

	public Paint AddPaint(String n) {
		Paint ps;

		hs.put(n, ps = new Paint());
        ps.setDither(true);
		return ps;
	}
}
