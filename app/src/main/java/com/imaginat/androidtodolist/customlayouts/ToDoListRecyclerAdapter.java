package com.imaginat.androidtodolist.customlayouts;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.businessModels.ToDoListItem;

import java.util.ArrayList;

/**
 * Created by nat on 5/1/16.
 */
public class ToDoListRecyclerAdapter extends RecyclerView.Adapter<ToDoListRecyclerAdapter.ToDoListItemHolder> {

    private static final String TAG = ToDoListRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<ToDoListItem> mToDoListItems;
    private ToDoListRecyclerAdapter.IHandleListClicks mClickInterface;

    public interface IHandleListClicks {
        public void handleClick(String data);

        public void handleMoreOptions(String list_id,String item_id);

        public void handleClickToCreateNewReminder(String data);

        public void handleClickToUpdateReminder(String id, String data);

        public void handleClickToUpdateCheckStatus(String listId,String id,boolean isChecked);

        public void handleDeleteButton(String id);

        public void handleShowMoreOptions(String listId,String reminderID);
    }

    public ToDoListRecyclerAdapter(Context context, ArrayList<ToDoListItem> arrayList, ToDoListRecyclerAdapter.IHandleListClicks ilistClicks) {
        mContext = context;
        mToDoListItems = arrayList;
        mClickInterface = ilistClicks;

    }

    public void setToDoListArray(ArrayList<ToDoListItem> list) {
        mToDoListItems = list;
    }

    @Override
    public ToDoListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.to_do_list_line_item, parent, false);



        return new ToDoListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoListItemHolder holder, int position) {

        ToDoListItem toDoListItem = (ToDoListItem) mToDoListItems.get(position);
        if (toDoListItem == null) {

            holder.mEditText.setText("");
            holder.mEditText.setHint("ADD A REMINDER");
            holder.mRadioButton.setVisibility(View.GONE);
            return;
        }
        holder.mRadioButton.setVisibility(View.VISIBLE);
        holder.mRadioButton.setChecked(toDoListItem.isCompleted());
        holder.mEditText.setText(toDoListItem.getText());
        holder.mListID=toDoListItem.getListId();
        holder.mReminderId = toDoListItem.getReminder_id();
        //((LinearLayout)holder.itemView.findViewById(R.id.lineItemOptionsButton)).setVisibility(View.GONE);
        holder.mMoreOpts.setVisibility(View.INVISIBLE);
        holder.mDidIEdit=false;



    }


    @Override
    public int getItemCount() {
        return mToDoListItems.size();
    }


    //====================================================================================
    public class ToDoListItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnKeyListener,
            TextView.OnEditorActionListener,View.OnFocusChangeListener{

        public CheckBox mRadioButton;
        public Button mDeleteButton;
        public Button mOptionsButton;
        public TextView mTextView;
        public EditText mEditText;
        public String mReminderId;
        public View mItemView;
        public ImageButton mMoreOpts;
        public String mListID;
        public boolean mDidIEdit=false;




        public ToDoListItemHolder(View itemView) {
            super(itemView);
           mItemView =itemView;
            //mViewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.my_switcher);
            mEditText = (EditText)itemView.findViewById(R.id.listItemEdit);
            mRadioButton = (CheckBox) itemView.findViewById(R.id.completedRadioButton);
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
           // mDeleteButton = (Button)itemView.findViewById(R.id.deleteLineItemButton);
           // mOptionsButton=(Button)itemView.findViewById(R.id.editLineItemButton);
            mItemView.setOnClickListener(this);
            mMoreOpts = (ImageButton)itemView.findViewById(R.id.moreOptionsButton);
            mEditText.setOnKeyListener(this);
            mEditText.setOnFocusChangeListener(this);
            //mEditText.setOnLongClickListener(this);
            mEditText.setOnEditorActionListener(this);

            /*Button hideOptionsButton = (Button)itemView.findViewById(R.id.hideOptions);
            hideOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoreOpts.setVisibility(View.GONE);
                    LinearLayout ll = (LinearLayout)mItemView.findViewById(R.id.lineItemOptionsButton);
                    ll.setVisibility(View.GONE);
                    mEditText.setTextColor(Color.BLACK);
                }
            });*/

            mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Log.d(TAG,"onCheckedChanged "+mListID+" "+mReminderId);

                }
            });

            mRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickInterface.handleClickToUpdateCheckStatus(mListID,mReminderId,((CheckBox)v).isChecked());
                }
            });


            mItemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Log.d(TAG,"mItemView onKey ");
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        Log.d(TAG,"mItemView onKey entered pressed");
                        // Perform action on key press
                        // Toast.makeText(mContext, "ADDING TO DATABASE", Toast.LENGTH_SHORT).show();
                        if(mRadioButton.getVisibility()==View.GONE) {
                            Log.d(TAG,"mItemView onKey bisiblity of radio button is GONE");
                            mClickInterface.handleClickToCreateNewReminder(((EditText) v).getText().toString());
                        }else{
                            Log.d(TAG,"mItemView onKey visiblity of radio button is NOT gone");
                        }
                        //((ViewSwitcher) v.getParent()).showPrevious();

                    }
                    return false;
                }
            });

            mEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //ImageButton imageButton = (ImageButton)v.findViewById(R.id.moreOptionsButton);
                    mMoreOpts.setVisibility(View.VISIBLE);

                    return false;
                }
            });
            /*mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickInterface.handleDeleteButton(mReminderId);
                }
            });*/

            /*mOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mClickInterface.handleClick("MORE_OPTIONS");
                    mClickInterface.handleMoreOptions(mListID,mReminderId);
                }
            });*/


            mMoreOpts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* LinearLayout ll = (LinearLayout)mItemView.findViewById(R.id.lineItemOptionsButton);

                    if (ll.getVisibility()==View.VISIBLE){
                        Log.d(TAG, "setting delButton to GONE");
                        ll.setVisibility(View.GONE);
                        mEditText.setTextColor(Color.BLACK);

                    }else{*/

                        mClickInterface.handleShowMoreOptions(mListID,mReminderId);
/*
                        ll.setVisibility(View.VISIBLE);
                        Log.d(TAG, "setting delButton to VISIBLE");
                        mEditText.setTextColor(Color.WHITE);
                    }*/

                }
            });

        }



        @Override
        public void onClick(View v) {


            //Log.d(TAG,"onClick called");
            mEditText.requestFocus();
            if(mMoreOpts.getVisibility()!=View.VISIBLE && mRadioButton.getVisibility()==View.VISIBLE){
                mMoreOpts.setVisibility(View.VISIBLE);

            }


        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //Log.d(TAG,"onKey");
            mDidIEdit=true;

            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(TAG, "onFocusChange called");
            if(!hasFocus){

                mMoreOpts.setVisibility(View.INVISIBLE);
                mEditText.setTextColor(Color.BLACK);

                //((ViewSwitcher)v.getParent()).showPrevious();
                if(mDidIEdit && mRadioButton.getVisibility()==View.VISIBLE){
                    mClickInterface.handleClickToUpdateReminder(mReminderId,((EditText)v).getText().toString());
                    mDidIEdit=false;
                    Log.d(TAG,"CALL UPDATE STUFF HERE");
                }

            }else{
                //mMoreOpts.setVisibility(View.VISIBLE);
            }
        }



        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.d(TAG,"Inside onEditorAction");
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId==KeyEvent.KEYCODE_ENTER)
            {
                if(mRadioButton.getVisibility()==View.GONE) {
                    Toast.makeText(mContext, "ADDING TO DATABASE", Toast.LENGTH_SHORT).show();
                    mClickInterface.handleClickToCreateNewReminder(((EditText) v).getText().toString());
                    //((ViewSwitcher) v.getParent()).showPrevious();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        }



    }


    //=================================


}
