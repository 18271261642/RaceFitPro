package com.example.bozhilun.android.b31.hrv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.GlossaryExpandableListAdapter;
import com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary;
import com.example.bozhilun.android.b31.glossary.AGlossary;
import com.example.bozhilun.android.b31.glossary.BeathBreathGlossary;
import com.example.bozhilun.android.b31.glossary.BreathGlossary;
import com.example.bozhilun.android.b31.glossary.HeartGlossary;
import com.example.bozhilun.android.b31.glossary.LowOxgenGlossary;
import com.example.bozhilun.android.b31.glossary.LowRemainGlossary;
import com.example.bozhilun.android.b31.glossary.OsahsGlossary;
import com.example.bozhilun.android.b31.glossary.OxgenGlossary;
import com.example.bozhilun.android.b31.glossary.RateVariGlossary;
import com.example.bozhilun.android.b31.glossary.SleepBreathBreakGlossary;
import com.example.bozhilun.android.b31.glossary.SleepGlossary;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 名词解释
 */
public class GlossaryDetailActivity extends Activity {

    ExpandableListView mExpandList;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vpgloassay_activity_detail);
        ButterKnife.bind(this);
        mExpandList = (ExpandableListView) findViewById(R.id.glossary_list);
        commentB30BackImg.setVisibility(View.VISIBLE);
        initAdapter();
    }


    private void initAdapter() {
        int value = getIntent().getIntExtra("type", 0);
        AGlossary glossary = getGlossaryOsahs(getApplicationContext(), value);
        commentB30TitleTv.setText(glossary.getHead());
        GlossaryExpandableListAdapter adapter
                = new GlossaryExpandableListAdapter(getApplicationContext(), glossary);
        mExpandList.setGroupIndicator(null);// 将控件默认的左边箭头去掉，
        mExpandList.setAdapter(adapter);
        expandList();
    }

    private void expandList() {
        int groupCount = mExpandList.getCount();
        for (int i = 0; i < groupCount; i++) {
            mExpandList.expandGroup(i);
        }
        mExpandList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    private AGlossary getGlossaryOsahs(Context context, int value) {
        EnumGlossary enumGlossary = EnumGlossary.getEnum(value);
        switch (enumGlossary) {
            case OSHAHS:
                return new OsahsGlossary(context);
            case BREATHBREAK:
                return new BeathBreathGlossary(context);
            case LOWOXGEN:
                return new LowOxgenGlossary(context);
            case HEART:
                return new HeartGlossary(context);
            case RATEVARABLE:
                return new RateVariGlossary(context);
            case SLEEP:
                return new SleepGlossary(context);
            case LOWREAMIN:
                return new LowRemainGlossary(context);
            case SLEEPBREATHBREAKTIP:
                return new SleepBreathBreakGlossary(context);
            case BREATH:
                return new BreathGlossary(context);
            case OXGEN:
                return new OxgenGlossary(context);
        }
        return new OsahsGlossary(context);
    }

    @OnClick(R.id.commentB30BackImg)
    public void onClick() {
        finish();
    }
}
