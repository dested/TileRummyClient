package com.TileRummy.LampLight;


public class MessageUpdate {
	public LampMessager.LampMessagerType Status;
	public String StringToUpdate;
	public String To;
	public String From;

	public MessageUpdate(LampMessager.LampMessagerType mt, String st) {
		Status = mt;
		StringToUpdate = st;
	}

	public MessageUpdate(LampMessager.LampMessagerType mt, String t, String f, String st) {
		Status = mt;
		To = t;
		From = f;
		StringToUpdate = st;
	}
}
