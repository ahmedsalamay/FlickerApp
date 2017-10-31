package com.example.ahmed.flickr.Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.ahmed.flickr.DataModel.DBHandler;
import com.example.ahmed.flickr.DataModel.GsonPhoto;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class QueryUtils {
    public static final String LOG_TAG=QueryUtils.class.getName();


    private QueryUtils(){
    }

    /**
     * Check the connection
     * @param context for systemService
     * @return flag that indicate the connecttion sataus(on/off)
     */
    public static boolean  isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context
                        .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    /**
     * COnvert image URl into Byte array to be inserted into data base
     * @param url image source url
     * @return byte[]  of the image
     */
    public static byte[] convertImgUrl2ByteArray(URL url){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = url.openStream ();
            byte[] byteChunk = new byte[4096];
            int n;
            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            Log.e("byte[]","IOE");
            e.printStackTrace ();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return baos.toByteArray();
    }
    /**get the image from flickr api Insert data into data base
     * @param  db database that data will be inserted into it
     * @param  photoInfo array of images data like server farm and id to form a url
     * @param  context for check internet conniction
     * */

    public static void insertData(DBHandler db, ArrayList<GsonPhoto.PhotosBean.PhotoBean> photoInfo,
                                  Context context){
        db.delete();
            for (int i = 0; i < photoInfo.size(); i++) {
                String url = String.format("https://farm%s.staticflickr.com/%s/%s_%s_%s.jpg",
                        photoInfo.get(i).getFarm(), photoInfo.get(i).getServer()
                        , photoInfo.get(i).getId(), photoInfo.get(i).getSecret(), "n");
                URL Url = QueryUtils.createUrl(url);
                if(QueryUtils.isConnected(context)) {
                    byte[] bytes = QueryUtils.convertImgUrl2ByteArray(Url);
                long id = db.insert(url, bytes);
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    //those functions was used in the begging of the code now we use volley
    ////////////////////////////////////////////////////////////////////////////////////////////
    public static GsonPhoto fetchPhotoData(String urlString){
        URL url=createUrl(urlString);
        String jsonResponse="";
        try {
            jsonResponse= makeHttpRequest(url);
            Log.e(LOG_TAG,jsonResponse);

        }catch (IOException e){
            Log.e(LOG_TAG,"problem in make http request",e);
        }
        return ParsePhoto(jsonResponse);
    }

    public static GsonPhoto ParsePhoto(String resultInputStream){
        return GsonPhoto.objectFromData(resultInputStream);
    }
    public static String makeHttpRequest(URL url)throws IOException{
        String jsonResponse="";
        InputStream inputStream=null;
        HttpURLConnection urlConnection=null;
        if(url ==null){
            return jsonResponse;
        }
        try {
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            if(urlConnection.getResponseCode()==200){
                inputStream=urlConnection.getInputStream();
                jsonResponse=readFromSream(inputStream);
            }
            else{
                Log.e(LOG_TAG,"Problem in site response" +urlConnection.getResponseCode()+"\n URL: "+url.toString());
            }
        }catch (IOException e){
            Log.e(LOG_TAG,"Problem retrive json",e);
        }
        finally {
            if(urlConnection!=null){
                urlConnection.disconnect();

            }
            if(inputStream!=null){
                inputStream.close();
            }
        }
        Log.e("Response",jsonResponse);

        return jsonResponse;
    }
    public static String readFromSream(InputStream inputStream)throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader=
                    new InputStreamReader(inputStream,
                            Charset.forName("UTF-8"));
            BufferedReader reader=new BufferedReader(inputStreamReader);
            String line=reader.readLine();
            while (line!=null){
                stringBuilder.append(line);
                line=reader.readLine();
            }
        }

        return stringBuilder.toString();
    }
    public static URL createUrl(String urlString){
        URL url=null;
        try {
            url=new URL(urlString);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG,"Problem building URL:"+urlString,e);
        }
        return url;
    }


}


