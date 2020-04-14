package com.example.bozhilun.android.w30s.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.carema.OnAlbumItemClickListener;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/31.
 */

public class W30sAlbumActivity extends WatchBaseActivity implements OnAlbumItemClickListener {

    private static final String TAG = "W30sAlbumActivity";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    File photos[];
    private List<String> tempList;
    private ArrayList<String> urlList;
    RecyclerView recyclerView;
    AlbumRecyAdapter albumRecyAdapter;

    String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures";
//    String folderPath = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, "W30sPic");
//    String folderPath = Environment.getExternalStorageDirectory()+"/Pictures";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30s_album_list);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.take_Album));
        recyclerView = (RecyclerView) findViewById(R.id.albumRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        tempList = new ArrayList<>();
        urlList = new ArrayList<>();
        albumRecyAdapter = new AlbumRecyAdapter();
        recyclerView.setAdapter(albumRecyAdapter);
        albumRecyAdapter.setOnAlbumItemClickListener(this);
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        scanFiles();

    }

    private void scanFiles() {
        Log.e(TAG, "-------foloPath=" + folderPath);
        if (!WatchUtils.isEmpty(folderPath)) {
            File file = new File(folderPath);
            photos = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jpg");
                }
            });
            if (photos == null || photos.length <= 0){
                urlList.clear();
                albumRecyAdapter.notifyDataSetChanged();
                return;
            }
            tempList.clear();
            for (int i = 0; i < photos.length; i++) {
                tempList.add(photos[i].getAbsolutePath());
            }
            urlList.clear();
            urlList.addAll(tempList);
            albumRecyAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void doItemClick(int position) {
        Intent intent = new Intent(W30sAlbumActivity.this, ShowAlbumActivity.class);
        intent.putExtra("imgPosition", position);
        intent.putStringArrayListExtra("imgUrl", urlList);
        startActivity(intent);

    }

    class AlbumRecyAdapter extends RecyclerView.Adapter<AlbumRecyAdapter.AlbumHolder> {

        private OnAlbumItemClickListener onAlbumItemClickListener;

        public void setOnAlbumItemClickListener(OnAlbumItemClickListener onAlbumItemClickListener) {
            this.onAlbumItemClickListener = onAlbumItemClickListener;
        }

        public AlbumRecyAdapter() {
        }

        @Override
        public AlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(W30sAlbumActivity.this).inflate(R.layout.item_publish, parent, false);
            return new AlbumHolder(view);
        }

        @Override
        public void onBindViewHolder(final AlbumHolder holder, int position) {
            Glide.with(W30sAlbumActivity.this)
                    .load(urlList.get(position))
                    .thumbnail(0.1f)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    if (onAlbumItemClickListener != null) {
                        onAlbumItemClickListener.doItemClick(position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return urlList.size();
        }

        class AlbumHolder extends RecyclerView.ViewHolder {

            ImageView imageView;

            public AlbumHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.item_grid_image);
            }
        }

    }
}
