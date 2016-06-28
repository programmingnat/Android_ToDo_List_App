package com.imaginat.androidtodolist.customlayouts.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.imaginat.androidtodolist.GlobalConstants;
import com.imaginat.androidtodolist.R;

/**
 * Created by nat on 6/27/16.
 */
public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.IconHolder> {



    private static RadioButton lastSelected=null;
    private static int lastCheckedPos=0;

    @Override
    public IconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_list_item, parent, false);
        return new IconHolder(v);
    }

    @Override
    public void onBindViewHolder(IconHolder holder, int position) {
        int resourceID = getImageResource(position);
        holder.mImageView.setImageResource(resourceID);
        holder.mRadioButton.setTag(position);

        holder.mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb=(RadioButton)v;
                if(rb.isChecked()){
                    if(lastSelected!=null){
                        lastSelected.setChecked(false);
                    }
                    lastSelected=rb;

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return GlobalConstants.TOTAL_ICONS;
    }


    public int getSelectedIcon(){
        if(lastSelected==null)
            return 0;
        return (Integer)lastSelected.getTag();
    }
    private int getImageResource(int position){
        switch(position){
            case GlobalConstants.GENERIC_REMINDER_ICON:
                return R.drawable.generic_reminder;
            case GlobalConstants.SHOPPING_CART_ICON:
                return R.drawable.shopping_cart;
            case GlobalConstants.WORKOUT_ICON:
                return R.drawable.workout;
            case GlobalConstants.WORK_ICON:
                return R.drawable.laptop;
            case GlobalConstants.KIDS_ICON:
                return R.drawable.kids_logo;
            case GlobalConstants.BEACH_ICON:
                return R.drawable.beach_logo;
            default:
                return R.drawable.generic_reminder;


        }
    }
    class IconHolder extends RecyclerView.ViewHolder{

        ImageView mImageView;
        RadioButton mRadioButton;
        public IconHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView)itemView.findViewById(R.id.listIcon_imageView);
            mRadioButton = (RadioButton)itemView.findViewById(R.id.listIcon_radioButton);
        }
    }


}
