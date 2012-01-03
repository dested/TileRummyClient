package com.TileRummy.LampLight;

public abstract class LampMessager {
	public enum LampMessagerType {
		UserLoggedOut, UserLoggedIn, NewMessage
	}

	public abstract void SendUpdate(MessageUpdate mu);
}
