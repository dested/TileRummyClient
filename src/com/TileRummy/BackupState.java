package com.TileRummy;

public class BackupState {
    public String PlayerName;
    public int SetIndex;
    public int TileIndex;

    public BackupState(String name, int set, int tile) {
        PlayerName = name;
        SetIndex = set;
        TileIndex = tile;
    }

}
