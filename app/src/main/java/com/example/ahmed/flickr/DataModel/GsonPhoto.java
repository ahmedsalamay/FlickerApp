package com.example.ahmed.flickr.DataModel;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Ahmed on 10/28/2017.
 */
public class GsonPhoto {

    private PhotosBean photos;

    public static GsonPhoto objectFromData(String str) {

        return new Gson().fromJson(str, GsonPhoto.class);
    }

    public PhotosBean getPhotos() {
        return photos;
    }

    public void setPhotos(PhotosBean photos) {
        this.photos = photos;
    }

    public static class PhotosBean {
        private int page;
        private int pages;
        private int perpage;
        private int total;
        private ArrayList<PhotoBean> photo;

        public static PhotosBean objectFromData(String str) {

            return new Gson().fromJson(str, PhotosBean.class);
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getPerpage() {
            return perpage;
        }

        public void setPerpage(int perpage) {
            this.perpage = perpage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public ArrayList<PhotoBean> getPhoto() {
            return photo;
        }

        public void setPhoto(ArrayList<PhotoBean> photo) {
            this.photo = photo;
        }

        public static class PhotoBean {
            private String id;
            private String secret;
            private String server;
            private int farm;

            public static PhotoBean objectFromData(String str) {

                return new Gson().fromJson(str, PhotoBean.class);
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public String getServer() {
                return server;
            }

            public void setServer(String server) {
                this.server = server;
            }

            public int getFarm() {
                return farm;
            }

            public void setFarm(int farm) {
                this.farm = farm;
            }
        }
    }
}
