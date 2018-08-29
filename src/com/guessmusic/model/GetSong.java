package com.guessmusic.model;

public class GetSong {

	private String mSongName, mSongFileName;

	public String getSongName() {
		return mSongName;
	}

	public void setSongName(String SongName) {
		this.mSongName = SongName;
	}

	public String getSongFileName() {
		return mSongFileName;
	}

	public void setSongFileName(String SongFileName) {
		this.mSongFileName = SongFileName;
	}

	public int getSongNameLenth() {
		return mSongName.length();
	}

	public char[] getSongNameChar() {
		return mSongName.toCharArray();
	}

}
