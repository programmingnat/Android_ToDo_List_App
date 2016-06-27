package com.imaginat.androidtodolist.customlayouts;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.models.IListItem;
import com.imaginat.androidtodolist.models.ListTitle;

import java.util.ArrayList;

/**
 * Created by nat on 4/15/16.
 */
public class ReminderListRecycleAdapter extends RecyclerView.Adapter<ReminderListRecycleAdapter.ReminderHolder> {


    public interface IHandleListClicks {
        public void handleClick(String data);
        public void handleLongClick(String data);

    }

    private final static String TAG = ReminderListRecycleAdapter.class.getName();
    private Context mContext;
    private ArrayList<ListTitle> mReminders;
    private IHandleListClicks mIHandleListClicks;

    public class ReminderHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        //public RadioButton mRadioButton;
        public View mLineItemView;
        public ImageView mImageView;
        public String mList_id = "someListID";

        public ReminderHolder(View itemView) {
            super(itemView);
            mLineItemView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
            mImageView = (ImageView)itemView.findViewById(R.id.listImage);






            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClickon the main View item");
                    mIHandleListClicks.handleClick(mList_id);

                    v.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.textlines, null));

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    //v.setSelected(true);
                    mIHandleListClicks.handleLongClick(mList_id);
                    v.setBackgroundColor(Color.rgb(200,200,200));
                    return true;
                }
            });

            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                final View insideView = ReminderHolder.this.itemView;

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // run scale animation and make it bigger

                        ViewCompat.setElevation(insideView, 1);
                    } else {
                        // run scale animation and make it smaller

                        ViewCompat.setElevation(insideView, 0);
                    }
                }
            });
        }


    }


    public IListItem getItem(int position) {
        return mReminders.get(position);
    }

    public ReminderListRecycleAdapter(Context context, ArrayList<ListTitle> reminders, IHandleListClicks iHandleListClicks) {
        mContext = context;
        mReminders = reminders;
        mIHandleListClicks = iHandleListClicks;
    }

    public void setToRemindersArray(ArrayList<ListTitle>list){
        mReminders=list;
    }
    @Override
    public ReminderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.list_line_item, parent, false);

        return new ReminderHolder(view);
    }


    @Override
    public void onBindViewHolder(ReminderHolder holder, int position) {
        ListTitle reminder =  mReminders.get(position);
        if (reminder == null) {


            holder.itemView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.textlines, null));

//            holder.mLineItemView.setBackgroundColor(Color.argb(255,255,255,255));
            holder.mTextView.setTextColor(Color.BLACK);
           // holder.mRadioButton.setVisibility(View.VISIBLE);

            holder.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            holder.mTextView.setText("Add List Here");
        } else {
            holder.mList_id = reminder.getList_id();
            holder.itemView.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.textlines, null));

//            holder.mLineItemView.setBackgroundColor(Color.argb(255, 255, 255, 255));
            holder.mTextView.setTextColor(Color.BLACK);
            //holder.mRadioButton.setVisibility(View.VISIBLE);

            holder.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            holder.mTextView.setText(reminder.getText());

            //randomly assign image (temp)
            int imageIndex = (int) (Math.random() * 6);
            switch (imageIndex) {
                case 0:
                    holder.mImageView.setBackgroundResource(R.drawable.generic_reminder);
                    break;
                case 1:
                    holder.mImageView.setBackgroundResource(R.drawable.workout);
                    break;
                case 2:
                    holder.mImageView.setBackgroundResource(R.drawable.shopping_cart);
                    break;
                case 3:
                    holder.mImageView.setBackgroundResource(R.drawable.laptop);
                    break;
                case 4:
                    holder.mImageView.setBackgroundResource(R.drawable.kids_logo);
                    break;
                case 5:
                    holder.mImageView.setBackgroundResource(R.drawable.beach_logo);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }
}
