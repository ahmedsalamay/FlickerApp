package com.example.ahmed.flickr.DataModel;

/**
 * Created by Ahmed on 10/30/2017.
 */
//this class used to put data into data base
public class PhotoEntities {
    private String url;
    private byte []byteArrayPhoto;

    public PhotoEntities(String url, byte[] byteArrayPhoto) {
        this.url = url;
        this.byteArrayPhoto = byteArrayPhoto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getbyteArrayPhoto() {
        return byteArrayPhoto;
    }

    public void setByteArrayPhoto(byte[] byteArrayPhoto) {
        this.byteArrayPhoto = byteArrayPhoto;
    }
}
