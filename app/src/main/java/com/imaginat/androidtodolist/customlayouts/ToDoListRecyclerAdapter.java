package com.imaginat.androidtodolist.customlayouts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.businessModels.IListItem;
import com.imaginat.androidtodolist.businessModels.ToDoListItem;

import java.util.ArrayList;

/**
 * Created by nat on 5/1/16.
 */
public class ToDoListRecyclerAdapter extends RecyclerView.Adapter<ToDoListRecyclerAdapter.ToDoListItemHolder> {

    private Context mContext;
    private ArrayList<IListItem>mToDoListItems;
    private ReminderListRecycleAdapter.IHandleListClicks mClickInterface;
    public ToDoListRecyclerAdapter(Context context,ArrayList<IListItem> arrayList,ReminderListRecycleAdapter.IHandleListClicks ilistClicks){
        mContext=context;
        mToDoListItems = arrayList;
        mClickInterface = ilistClicks;
    }
    @Override
    public ToDoListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.to_do_list_line_item, parent, false);

        Button deleteButton = (Button)view.findViewById(R.id.deleteLineItemButton);
        Button moreButton=(Button)view.findViewById(R.id.editLineItemButton);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickInterface.handleClick("Clicked");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"deletebutton pressed",Toast.LENGTH_SHORT).show();
            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickInterface.handleClick("MORE_OPTIONS");
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            int downX, upX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = (int) event.getX();
                    Log.i("event.getX()", " downX " + downX);
                    return true;
                }

                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    upX = (int) event.getX();
                    Log.i("event.getX()", " upX " + downX);
                    if (upX - downX > 100) {
                        Toast.makeText(mContext,"Swipe Right",Toast.LENGTH_SHORT).show();
                        LinearLayout ll = (LinearLayout)v.findViewById(R.id.lineItemOptionsButton);
                        ll.setVisibility(View.GONE);
                        // swipe right
                    }

                    else if (downX - upX > -100) {
                        Toast.makeText(mContext,"Swipe Left",Toast.LENGTH_SHORT).show();
                        LinearLayout ll = (LinearLayout)v.findViewById(R.id.lineItemOptionsButton);
                        ll.setVisibility(View.VISIBLE);
                        // swipe left
                    }
                    return true;

                }
                return false;

            }
        });

        return new ToDoListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoListItemHolder holder, int position) {
        ToDoListItem toDoListItem =(ToDoListItem)mToDoListItems.get(position);
        holder.mTextView.setText(toDoListItem.getText());



    }



    @Override
    public int getItemCount() {
        return mToDoListItems.size();
    }

    //====================================================================================
    public class ToDoListItemHolder extends RecyclerView.ViewHolder{

        public RadioButton mRadioButton;
        public TextView mTextView;

        public ToDoListItemHolder(View itemView) {
            super(itemView);
            mRadioButton = (RadioButton)itemView.findViewById(R.id.completedRadioButton);
            mTextView = (TextView)itemView.findViewById(R.id.listItemTextView);

        }
    }

}
