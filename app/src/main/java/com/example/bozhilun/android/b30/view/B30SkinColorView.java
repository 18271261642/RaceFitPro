package com.example.bozhilun.android.b30.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.adapter.B30SkinColorAdapter;
import com.example.bozhilun.android.b30.bean.SkinColorBean;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * B30的肤色选择View
 */
public class B30SkinColorView extends Dialog implements View.OnClickListener ,AdapterView.OnItemClickListener {

    public B30SkinColorListener b30SkinColorListener;

    public void setB30SkinColorListener(B30SkinColorListener b30SkinColorListener) {
        this.b30SkinColorListener = b30SkinColorListener;
    }

    private ListView listView;
    //确认和取消按钮
    private Button cancleBtn,sureBtn;
    //图片数组
    public  static Integer[] imgStr = new Integer[]{R.drawable.register_skibn_color_1,
            R.drawable.register_skibn_color_2,R.drawable.register_skibn_color_3,
            R.drawable.register_skibn_color_4, R.drawable.register_skibn_color_5,R.drawable.register_skibn_color_6};
    private List<SkinColorBean> list;
    //适配器
    private B30SkinColorAdapter adapter;
    SkinColorBean skinColorBean;

    private int defaultPosition = -1;

    public B30SkinColorView(@NonNull Context context) {
        super(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b30_skin_color_select_view);


        initViews();

        initData();


    }

    private void initData() {
        list = new ArrayList<>();
        int skinColorPosition = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),"skin_position",2);
        //先给默认的数据
        for(int i = 0;i<imgStr.length;i++){
            skinColorBean = new SkinColorBean();
            skinColorBean.setImgId(imgStr[i]);
            if(skinColorPosition == i){
                skinColorBean.setChecked(true);
            }else{
                skinColorBean.setChecked(false);
            }
            list.add(skinColorBean);
        }

        adapter = new B30SkinColorAdapter(list,MyApp.getContext());
        listView.setAdapter(adapter);

        adapter.setOnCheckSkinItem(new B30SkinColorAdapter.OnCheckSkinItem() {
            @Override
            public void checkItemPosition(int position) {
                defaultPosition = position;
            }
        });
    }

    private void initViews() {
        listView = findViewById(R.id.skinColorListView);
        cancleBtn = findViewById(R.id.skinColorCancleBtn);
        sureBtn = findViewById(R.id.skinColorSureBtn);
        cancleBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
        listView.setOnItemClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.skinColorCancleBtn:   //取消
                if(b30SkinColorListener != null)
                    b30SkinColorListener.doCancleSkinClick();
                break;
            case R.id.skinColorSureBtn: //确认
                if(defaultPosition != -1){
                    if(b30SkinColorListener != null)
                        b30SkinColorListener.doSureSkinClick(imgStr[defaultPosition],defaultPosition);
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    public interface B30SkinColorListener{
        void doSureSkinClick(int selectImgId, int position);
        void doCancleSkinClick();
    }

}
