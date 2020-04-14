package com.example.bozhilun.android.b31;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.R;

import java.util.List;
import java.util.Map;

/**疲劳度测试历史记录的适配器
 * Created by Admin
 * Date 2019/1/8
 */
public class B31ManFatigueExplandAdapter extends BaseExpandableListAdapter {

    private String[] parentStr ;
    private Map<String,List<String>> childStr;
    private Context mContext;

    private LayoutInflater layoutInflater;

    public B31ManFatigueExplandAdapter(String[] parentStr, Map<String,List<String>> childStr, Context mContext) {
        this.parentStr = parentStr;
        this.childStr = childStr;
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
    }

    //获取所有父项的数量
    @Override
    public int getGroupCount() {
        return parentStr.length;
    }

    //获取某个父项的子项目
    @Override
    public int getChildrenCount(int groupPosition) {
        return childStr.get(parentStr[groupPosition]).size();
    }

    //获取某个父项
    @Override
    public Object getGroup(int groupPosition) {
        return parentStr[groupPosition];
    }

    //获取某个父项的某个子项的ID
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childStr.get(parentStr[groupPosition]).get(childPosition);
    }

    //获取某个父项的ID
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //允许和子选项是否有稳定的ID，就是说底层的改变不会影响到他们
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder = null;
        if(convertView == null){
//            convertView = layoutInflater.inflate(R.layout.item_parent_layout,parent,false);
//            groupViewHolder = new GroupViewHolder();
          //  groupViewHolder.groupTv = convertView.findViewById(R.id.itemParentTv);
            convertView.setTag(groupViewHolder);
        }else{
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.groupTv.setText(parentStr[groupPosition]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
       ChildViewHolder childViewHolder = null;
       if(convertView == null){
           convertView = layoutInflater.inflate(R.layout.item_b31_fait_childe_layout,parent,false);
           childViewHolder = new ChildViewHolder();
           childViewHolder.childDateTv = convertView.findViewById(R.id.childTimeTv);
           childViewHolder.childStatusTv = convertView.findViewById(R.id.childStatusTv);
           convertView.setTag(childViewHolder);
       }else{
           childViewHolder = (ChildViewHolder) convertView.getTag();
       }
        //childViewHolder.childDateTv.setText(childStr.get(parentStr[groupPosition]).get(childPosition).get(""));

        return convertView;
    }

    //设置位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    class GroupViewHolder{
        TextView groupTv;
    }

    class ChildViewHolder{
        TextView childDateTv,childStatusTv;
        ImageView statusImg;
    }
}
