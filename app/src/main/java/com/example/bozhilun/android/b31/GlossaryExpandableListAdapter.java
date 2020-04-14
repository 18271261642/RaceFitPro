package com.example.bozhilun.android.b31;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b31.glossary.AGlossary;


public class GlossaryExpandableListAdapter extends BaseExpandableListAdapter {
    Context context;
    String[] groupString;
    String[][] itemString;

    public GlossaryExpandableListAdapter(Context context, AGlossary glossary) {
        this.context = context;
        this.groupString = glossary.getGroupString();
        this.itemString = glossary.getItemString();
    }


    @Override
    public int getGroupCount() {
        return groupString.length;
    }

    public int getChildrenCount(int groupPosition) {
        return itemString[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupString[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemString[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = View.inflate(context, R.layout.vpglossay_adapter_group, null);
            groupHolder.gruopTv = (TextView) convertView.findViewById(R.id.glossary_group);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.gruopTv.setText(groupString[groupPosition]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = View.inflate(context, R.layout.vpglossay_adapter_item, null);
            childHolder.itemTv = (TextView) convertView.findViewById(R.id.glossary_item);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        childHolder.itemTv.setText(itemString[groupPosition][childPosition]);
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupHolder {
        TextView gruopTv;

    }

    static class ChildHolder {
        TextView itemTv;
    }


}
