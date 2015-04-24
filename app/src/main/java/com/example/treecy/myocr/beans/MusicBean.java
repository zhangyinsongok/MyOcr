package com.example.treecy.myocr.beans;

/**
 * Created by TreecY on 2015/4/9.
 */
public class MusicBean {
    private String songId;
        private String songName;
        private String artistName;
        private String albumName;
        private String picUrl;
        private String audioUrl;

        public MusicBean(String songId,String songName,String artistName,String albumName,String picUrl,String audioUrl) {
            this.songId =songId;
            this.songName = songName;
            this.artistName =artistName;
            this.albumName = albumName;
            this.picUrl = picUrl;
            this.audioUrl = audioUrl;
        }

        public String getSongId() {
            return songId;
        }

        public void setSongId(String songId) {
            this.songId = songId;
        }

        public String getSongName() {
            return songName;
        }

        public void setSongName(String songName) {
            this.songName = songName;
        }

        public String getArtistName() {
            return artistName;
        }

        public void setArtistName(String artistName) {
            this.artistName = artistName;
        }

        public String getAlbumName() {
            return albumName;
        }

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }
}

