package com.example.bozhilun.android.b31.hrv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.example.bozhilun.android.R;

import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.BREATH;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.BREATHBREAK;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.HEART;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.LOWOXGEN;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.LOWREAMIN;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.OSHAHS;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.OXGEN;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.RATEVARABLE;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.SLEEP;
import static com.example.bozhilun.android.b31.bpoxy.enums.EnumGlossary.SLEEPBREATHBREAKTIP;


/**
 * Created by Administrator on 2019/1/2.
 */

public class GlossaryActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vpgloassay_activity);
    }

    public void onOSHAHS(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = OSHAHS.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }

    public void onBREATHBREAK(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = BREATHBREAK.getValue();
        intent.putExtra("type", value);
        startActivity(intent);


    }

    public void onLOWOXGEN(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = LOWOXGEN.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }

    public void onHEART(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = HEART.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }

    public void onRATEVARABLE(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = RATEVARABLE.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }

    public void onSLEEP(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = SLEEP.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }

    public void onLOWREAMIN(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = LOWREAMIN.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }

    public void onSLEEPBREATHBREAKTIP(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = SLEEPBREATHBREAKTIP.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }

    public void onBREATH(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = BREATH.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }

    public void onOXGEN(View view) {
        Intent intent = new Intent(GlossaryActivity.this, GlossaryDetailActivity.class);
        int value = OXGEN.getValue();
        intent.putExtra("type", value);
        startActivity(intent);
    }
}
