package com.nglinx.pulse.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nglinx.pulse.R;
import com.nglinx.pulse.models.DeviceModel;
import com.nglinx.pulse.models.DeviceModel;
import com.nglinx.pulse.models.GroupMemberModel;
import com.nglinx.pulse.models.GroupMemberModel;

import java.util.ArrayList;

/**
 * Created by android on 23/12/15.
 */
public class DevicesAdapter extends ArrayAdapter<GroupMemberModel> {

    private ViewHolder holder = null;

    private ArrayList<GroupMemberModel> arr2;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private LayoutInflater mInflater;
    private Context context;

    public DevicesAdapter(Context context, ArrayList<GroupMemberModel> arr2) {
        super(context, 0, arr2);
        this.context = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arr2 = arr2;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return arr2.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public GroupMemberModel getItem(int position) {
        return arr2.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = null;
        convertView = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.member_list_view, null);
            holder = new ViewHolder();
            holder.btn = (TextView) convertView.findViewById(R.id.membername);
//            holder.button = (ImageView) convertView.findViewById(R.id.imageView2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /**
         * Last button coding
         * */

		/*if(groupMembers.get(position).isFlag){
            // show button here
		}*/
        int count = arr2.size();
        /*if ((null == arr2.get(position).getId()) || (arr2.get(position).getId().equals(""))) {
            holder.btn.setBackgroundResource(R.drawable.add_user_icon);
        } else {*/
        String first_name;
        String[] separated = null;

        if (arr2.get(position).getName() != null) {
            separated = arr2.get(position).getName().split(" ");
        } else {
            separated = arr2.get(position).getId().split(" ");
        }


        if (separated[0].toCharArray().length > 5) {
            first_name = separated[0].substring(0, 5) + "...";
        } else {
            first_name = separated[0];
        }
        holder.btn.setText(first_name);
//        }
//        holder.imageView1.setTag(position + "");
        return convertView;
    }


    public static class ViewHolder {
        public TextView btn;
        ImageView button;
    }

    public void setArr2(ArrayList<GroupMemberModel> arr2) {
        this.arr2 = arr2;
    }
}
