package map.mapexample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends Fragment {
    private List<Photo> photos;
    private static final String TAG = "GalleryActivity";
    public RecyclerView recyclerView;
    @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       // super.onCreate(savedInstanceState);
        View view= inflater.inflate(R.layout.activity_gallery, container, false);
        //setContentView(R.layout.activity_gallery);
        System.out.printf("OnCreateView");
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_images);

        return view;
    }

@Override
public void onAttach(Context context) {
    super.onAttach(context);
    System.out.println("onAttach");

}
@Override
public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    System.out.println("onActivityCreated");
    photos= new ArrayList<>();
    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(layoutManager);
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference photoRef = database.child("photos");
    ValueEventListener photoListener = new ValueEventListener(){
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            // Get Pin object and use the values to update the UI
            photos.clear();
            System.out.println("onDataChanged");
            System.out.println(dataSnapshot);
            for (DataSnapshot photoSnapshot: dataSnapshot.getChildren()) {
                Photo newphoto = photoSnapshot.getValue(Photo.class);
                photos.add(newphoto);
            }

            ImageGalleryAdapter adapter = new ImageGalleryAdapter(getActivity(), photos);
            recyclerView.setAdapter(adapter);


        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };
    photoRef.addValueEventListener(photoListener);







}
    private class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>  {
        private List<Photo> mSpacePhotos = new ArrayList<>();
        private Context mContext;

        public ImageGalleryAdapter(Context context, List<Photo> photos) {
            System.out.println("photos=" + photos);
            mContext = context;
            mSpacePhotos = photos;
            for(Photo p: photos) {
                System.out.println(p.getUrl());
            }
        }
        @Override
        public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View photoView = inflater.inflate(R.layout.image_item, parent, false);
            ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {

            Photo sPhoto = mSpacePhotos.get(position);
            System.out.println("BindVIewHolder url=" + sPhoto.getUrl());
            ImageView imageView = holder.mPhotoImageView;
            Glide.with(mContext)
                    .load(sPhoto.getUrl())
                    .placeholder(R.drawable.progress_animation)
                    .into(imageView);

        }

        @Override
        public int getItemCount() {
            return (mSpacePhotos.size());
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mPhotoImageView;

            public MyViewHolder(View itemView) {

                super(itemView);
                mPhotoImageView = (ImageView) itemView.findViewById(R.id.rv_photo);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Photo sPhoto = mSpacePhotos.get(position);
                    Intent intent = new Intent(mContext, PhotoActivity.class);
                    intent.putExtra(PhotoActivity.EXTRA_SPACE_PHOTO, sPhoto);
                    startActivity(intent);
                }
            }
        }


    }
}
