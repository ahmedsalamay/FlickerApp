package com.example.ahmed.flickr.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ahmed.flickr.DataModel.GsonPhoto;
import com.example.ahmed.flickr.DataModel.PhotoEntities;
import com.example.ahmed.flickr.R;
import com.example.ahmed.flickr.ViewModel.FullscreenActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ahmed on 10/28/2017.
 */
public class GalleryRVAdapter extends RecyclerView.Adapter<GalleryRVAdapter.MyViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList <GsonPhoto.PhotosBean.PhotoBean>data;
    private ArrayList <PhotoEntities> mOfflineData;
    private GsonPhoto.PhotosBean.PhotoBean mPhotoInfo;
    private Bitmap mBitmapPhoto;
    private Context mContext;
    private String mUrl;
    private static final String IMAGE_URL = "https://farm%s.staticflickr.com/%s/%s_%s_%s.jpg";

    public GalleryRVAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext=context;
        data=new ArrayList <>();
        mOfflineData=new ArrayList <>();
    }
    public void clear(){
        data.clear();
        notifyDataSetChanged();
    }
    public void addAll(ArrayList <GsonPhoto.PhotosBean.PhotoBean>data){
        this.data.addAll(data);
        notifyDataSetChanged();
    }
    public void add(GsonPhoto.PhotosBean.PhotoBean photo){
        this.data.add(photo);
        //notifyDataSetChanged();
    }
    public void addAlloff(ArrayList<PhotoEntities>bitmaps){
        this.mOfflineData.addAll(bitmaps);
        notifyDataSetChanged();
    }
    public void add(PhotoEntities photo){
        this.mOfflineData.add(photo);

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =mInflater.inflate(R.layout.gallery_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(QueryUtils.isConnected(mContext)) {//if there is a internet connection set img by online data
            mPhotoInfo = data.get(position);
            mUrl = String.format(IMAGE_URL, mPhotoInfo.getFarm(), mPhotoInfo.getServer()
                    , mPhotoInfo.getId(), mPhotoInfo.getSecret(), "n");
            Picasso.with(mContext).load(mUrl).into(holder.img);
        }else {//else set image from offline data (database)
            byte[] data = mOfflineData.get(position).getbyteArrayPhoto();
            mBitmapPhoto = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.img.setImageBitmap(mBitmapPhoto);
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//get full screen image when i click on an image
                Intent intent = new Intent(mContext, FullscreenActivity.class);
                if (QueryUtils.isConnected(mContext)){//either send url to new intentclass by online data or data base
                    /*//this could happend if i was connected before i start the app
                    * and disconnected while app is running
                    * */
                    if(data.size()!=0){
                    mPhotoInfo = data.get(holder.getPosition());
                    mUrl = String.format(IMAGE_URL, mPhotoInfo.getFarm(), mPhotoInfo.getServer()
                            , mPhotoInfo.getId(), mPhotoInfo.getSecret(), "n");
                    intent.putExtra("URL", mUrl);
                    mContext.startActivity(intent);}
                    else{
                        mUrl = mOfflineData.get(holder.getPosition()).getUrl();
                        intent.putExtra("URL", mUrl);
                        mContext.startActivity(intent);
                    }
                }else{
                    /*//this could happend if i was disconnected before i start the app
                    * and connected while app is running
                    * */
                    if(mOfflineData.size()!=0) {
                        mUrl = mOfflineData.get(holder.getPosition()).getUrl();
                        intent.putExtra("URL", mUrl);
                        mContext.startActivity(intent);
                    }else{
                        mPhotoInfo = data.get(holder.getPosition());
                        mUrl = String.format(IMAGE_URL, mPhotoInfo.getFarm(), mPhotoInfo.getServer()
                                , mPhotoInfo.getId(), mPhotoInfo.getSecret(), "n");
                        intent.putExtra("URL", mUrl);
                        mContext.startActivity(intent);
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        if(QueryUtils.isConnected(mContext)){
            return data.size();
        }
        return mOfflineData.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        RecyclerView rcview;
        public MyViewHolder( View itemView) {
            super(itemView);
            img=(ImageView) itemView.findViewById(R.id.photo);
            rcview=(RecyclerView) itemView.findViewById(R.id.recycler_view);
        }
    }
}
