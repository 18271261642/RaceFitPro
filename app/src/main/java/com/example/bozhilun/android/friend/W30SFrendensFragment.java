package com.example.bozhilun.android.friend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bozhilun.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @aboutContent: 联系人界面
 * @author： An
 * @crateTime: 2018/3/5 17:04
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SFrendensFragment extends Fragment {

    @BindView(R.id.w30s_listView)
    ListView w30sListView;
    Unbinder unbinder;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.edit_seach)
    EditText editSeach;
    @BindView(R.id.frend_find_ok)
    TextView frendFindOk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fredens, container, false);
        unbinder = ButterKnife.bind(this, view);
        barTitles.setText(getResources().getString(R.string.string_contacts));

        inEdit();
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inEdit() {
        //rwb：取消EditText焦点，并且隐藏输入法。
        editSeach.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                editSeach.setFocusable(true);
                editSeach.setFocusableInTouchMode(true);
                editSeach.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(editSeach.getWindowToken(), 0);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });
        editSeach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    frendFindOk.setVisibility(View.VISIBLE);
                } else {
                    frendFindOk.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({ R.id.frend_find_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.frend_find_ok:
                editSeach.setText("");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                break;
        }
    }

}
