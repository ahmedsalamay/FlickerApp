package com.example.ahmed.flickr.DataModel;

import android.provider.BaseColumns;

/**
 * Created by Ahmed on 10/30/2017.
 */
public class PhotoContract {

    public static final class PhotoEntry implements BaseColumns {
        public static final String TABLE_NAME = "photos";
        public static final String COLUMN_URL = "photo_url";
        public static final String COLUMN_BYTE_ARRAY = "photo_byte";
    }

}
