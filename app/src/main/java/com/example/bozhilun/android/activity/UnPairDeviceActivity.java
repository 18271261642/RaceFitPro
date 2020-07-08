package com.example.bozhilun.android.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.inter.UnPairOperInterface;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.HidUtil;
import com.example.bozhilun.android.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin
 * Date 2019/10/11
 */
public class UnPairDeviceActivity extends WatchBaseActivity implements SwipeRefreshLayout.OnRefreshListener,UnPairOperInterface{


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.unpariRecyclerView)
    RecyclerView unpariRecyclerView;
    @BindView(R.id.unPairSwipe)
    SwipeRefreshLayout unPairSwipe;


    private BluetoothAdapter bluetoothAdapter;  //蓝牙适配器
    private List<BluetoothDevice> mapList;
    private UnPairAdapter unPairAdapter;
    //判断是否重复的list
    private List<String> repeatList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unpair_device_layout);
        ButterKnife.bind(this);



        initViews();

        registerReceiver(broadcastReceiver,new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        getPairDevice();


    }

    private void getPairDevice() {
        unPairSwipe.setRefreshing(true);
        //获取已配对设备
        Object[] lstDevice = bluetoothAdapter.getBondedDevices().toArray();
        if(lstDevice == null || lstDevice.length == 0){
            ToastUtil.showToast(UnPairDeviceActivity.this,"无配对设备");
            unPairSwipe.setRefreshing(false);
            return;
        }

        for(int i = 0;i<lstDevice.length;i++){
            BluetoothDevice bluetoothDevice = (BluetoothDevice) lstDevice[i];
            if(bluetoothDevice == null)
                return;
            String bleMac = bluetoothDevice.getAddress();
            if(bleMac == null)
                return;
            if(repeatList.contains(bleMac.trim()))
                return;
            repeatList.add(bleMac);
            mapList.add(bluetoothDevice);
            unPairAdapter.notifyDataSetChanged();
        }
        if(unPairSwipe != null)
            unPairSwipe.setRefreshing(false);
    }

    private void initViews() {
        commentB30TitleTv.setText("配对设备");
        commentB30BackImg.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        unpariRecyclerView.setLayoutManager(linearLayoutManager);
        mapList = new ArrayList<>();
        repeatList = new ArrayList<>();
        unPairAdapter = new UnPairAdapter(mapList);
        unpariRecyclerView.setAdapter(unPairAdapter);
        unPairSwipe.setOnRefreshListener(this);
        unPairAdapter.setUnPairOperInterface(this);
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bm.getAdapter();


    }

    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
    }


    //刷新
    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh(){
        repeatList.clear();
        mapList.clear();
        unPairAdapter.notifyDataSetChanged();
        getPairDevice();
    }

    @Override
    public void unPairOperater(int position) {
        BluetoothDevice bd = mapList.get(position);
        HidUtil.getInstance(MyApp.getContext()).unPair(bd);
        HidUtil.getInstance(MyApp.getContext()).unpairDevice(bd);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //adapter
    class UnPairAdapter extends RecyclerView.Adapter<UnPairAdapter.unPairViewHolder> {

        private UnPairOperInterface unPairOperInterface;

        public void setUnPairOperInterface(UnPairOperInterface unPairOperInterface) {
            this.unPairOperInterface = unPairOperInterface;
        }

        private List<BluetoothDevice> list;

        public UnPairAdapter(List<BluetoothDevice> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public unPairViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_un_pair_layout,viewGroup,false);
            return new unPairViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final unPairViewHolder holder, int i) {
            holder.bleNameTv.setText(list.get(i).getName());
            holder.bleMacTv.setText(list.get(i).getAddress());
            holder.unpairBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    if(unPairOperInterface != null)
                        unPairOperInterface.unPairOperater(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class unPairViewHolder extends RecyclerView.ViewHolder{

            TextView bleNameTv,bleMacTv;
            Button unpairBtn;

            public unPairViewHolder(@NonNull View itemView) {
                super(itemView);
                bleNameTv = itemView.findViewById(R.id.itemUnPairBleNameTv);
                bleMacTv = itemView.findViewById(R.id.itemUnPairBleMacTv);
                unpairBtn = itemView.findViewById(R.id.itemUnPairBtn);


            }
        }

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bd == null)
                    return;
                if (bd.getName() == null)
                    return;
                switch (bondState){
                    case BluetoothDevice.BOND_BONDED:   //已绑定 12

                        break;
                    case BluetoothDevice.BOND_BONDING:  //绑定中   11

                        break;
                    case BluetoothDevice.BOND_NONE: //绑定失败  10
                        refresh();
                        break;
                }
            }
        }
    };


}
