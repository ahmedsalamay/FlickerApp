package com.example.ahmed.flickr.ViewModel;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ahmed.flickr.Controller.EndlessRecyclerViewScrollListener;
import com.example.ahmed.flickr.Controller.GalleryRVAdapter;
import com.example.ahmed.flickr.Controller.QueryUtils;
import com.example.ahmed.flickr.DataModel.DBHandler;
import com.example.ahmed.flickr.DataModel.GsonPhoto;
import com.example.ahmed.flickr.DataModel.PhotoEntities;
import com.example.ahmed.flickr.R;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Ahmed on 10/28/2017.
 */
public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private GsonPhoto mPhotos;
    private  String BASEURL="https://api.flickr.com/services/rest/?method=%s&api_key=%s&page=%s&format=%s&nojsoncallback=%s";
    private final static String METHOD_PARAM ="flickr.interestingness.getList";
    private final static String API_KEY_PARAM ="e85484a7ab7462d0a1429d25616e5545";
    private final static String format="json";
    private final static String nojsoncallback="1";
    private  String page="1";
    private String mUrl =String.format(BASEURL, METHOD_PARAM, API_KEY_PARAM,page,format,nojsoncallback);
    private RecyclerView mRecyclerView;
    private BroadcastReceiver mBroadcastReceiver;
    private GalleryRVAdapter mGalleryRVAdapter;
    private SwipeRefreshLayout mSwiper;
    private StaggeredGridLayoutManager mGridLayoutManager;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ArrayList<PhotoEntities> mPhotoEntities;
    private DBHandler mDBHandler;
    private IntentFilter mIntentFilter;
    private boolean mIsRegistered=false;
    private ProgressDialog progressDialog;



    public MainActivityFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialization of member Variable
        View rootView = inflater.inflate(R.layout.rv_gallery , container, false);
        mDBHandler =new DBHandler(getActivity());
        mGridLayoutManager=new StaggeredGridLayoutManager(3,0);
        mRecyclerView =(RecyclerView)rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mIntentFilter = new IntentFilter();
        progressDialog=new ProgressDialog(getActivity());
        /**Check internet connectivity
         * @param Context of Activity
         * @return boolen if there is connection returned value true
         * */
        if(QueryUtils.isConnected(getActivity())) {//if there is internet connection pull data from inter net
            mGalleryRVAdapter = new GalleryRVAdapter(getActivity());
            loadData(mUrl);
            /**Refresh the images every 1 minute
             *@param Context
             *@param BroadCastReceiver to determined what to do on receive and regeist
             *@param Intent Filter to set an Acation on it
             * */
            mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
            mBroadcastReceiver = new BroadcastReceiver() {
                int  i =0;
                @Override
                public void onReceive(Context context, Intent intent) {
                    /*update silence better than annoying user every minute
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();*/
                    loadData(mUrl);
                    mGalleryRVAdapter.notifyDataSetChanged();
                    Log.e("Auto Refreshing",i+"");
                }
            };
            getActivity().registerReceiver(mBroadcastReceiver, mIntentFilter);
            mIsRegistered=true;

        }else { //if there is no inter net connection pull data from data base
            mPhotoEntities= mDBHandler.getAllData();
            ArrayList <GsonPhoto.PhotosBean.PhotoBean> dummy=new ArrayList<>();
            mGalleryRVAdapter = new GalleryRVAdapter(getActivity());
            mGalleryRVAdapter.addAlloff(mPhotoEntities);
        }
        mRecyclerView.setAdapter(mGalleryRVAdapter);
        //swiper to pull refresh
        mSwiper =(SwipeRefreshLayout) rootView.findViewById(R.id.swiper);
        mSwiper.setOnRefreshListener(this);

        //Load More Pages (infinite scrolling)
        mScrollListener = new EndlessRecyclerViewScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(QueryUtils.isConnected(getActivity())) {//load more pages only and only if there is network connection
                    mUrl = String.format(BASEURL, METHOD_PARAM, API_KEY_PARAM, page, format, nojsoncallback);
                    progressDialog.setMessage("Loading...paga"+page);
                    progressDialog.show();
                    loadData(mUrl);
                }
            }
        };
        mRecyclerView.addOnScrollListener(mScrollListener);
        SlideInLeftAnimator animator=new SlideInLeftAnimator();
        mRecyclerView.setItemAnimator(animator);
        return rootView;
    }

    /**load Data of all images from flickr url using volley lib
     *@param  url of flickr site
     * */
    void loadData(String url){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GsonPhoto PhotosObject=GsonPhoto.objectFromData(response);
                        mSwiper.setRefreshing(false);
                        mGalleryRVAdapter.addAll(PhotosObject.getPhotos().getPhoto());
                        mGalleryRVAdapter.notifyDataSetChanged();
                        mPhotos=PhotosObject;
                        progressDialog.dismiss();
                        new photoQuerryAsyTask().execute();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Response Error","Failed to rich site");
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
    //override swiper Listener on refresh to update data
    @Override
    public void onRefresh() {
        if(QueryUtils.isConnected(getActivity())){
            mGalleryRVAdapter.clear();
            String url=String.format(BASEURL, METHOD_PARAM, API_KEY_PARAM,0,format,nojsoncallback);
            loadData(url);
            for(int i=0;i<mPhotos.getPhotos().getPhoto().size();i++){
                mGalleryRVAdapter.add(mPhotos.getPhotos().getPhoto().get(i));
            }
            mRecyclerView.getLayoutManager().scrollToPosition(1);
        }
        else mSwiper.setRefreshing(false);

    }
    /*Aystask to pull all images data and image by image to convert it to ByteArray
     *@param   url that hold all images
     * @return GsonPhoto object the hold all images and data
    */
    private class  photoQuerryAsyTask extends AsyncTask<String ,Void,GsonPhoto> {
        @Override
        protected GsonPhoto doInBackground(String... urls) {
            QueryUtils.insertData(mDBHandler,mPhotos.getPhotos().getPhoto());
            return mPhotos;
        }

        @Override
        protected void onPostExecute(GsonPhoto photos) {
            mSwiper.setRefreshing(false);
            //progressDialog.dismiss();
        }
    }

    @Override
    public void onStop()throws IllegalArgumentException {
        super.onStop();
        if (mIsRegistered) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
            mIsRegistered=false;
        }
    }

    @Override
    public void onPause()throws IllegalArgumentException {
        super.onPause();
        if (mIsRegistered ) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
            mIsRegistered=false;
        }
    }
}
