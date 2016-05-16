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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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

        public void handleClickToCreateNewReminder(String data);

        public void handleClickToUpdateReminder(String id, String data);

        public void handleDeleteButton(String id);
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


/*
        Button deleteButton = (Button)view.findViewById(R.id.deleteLineItemButton);
        Button moreButton=(Button)view.findViewById(R.id.editLineItemButton);
        RadioButton mRadioButton;

        mRadioButton = (RadioButton) view.findViewById(R.id.completedRadioButton);

        mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton b = (RadioButton) v;
                if (b.isChecked()) {
                    // b.setChecked(false);
                } else {
                    // b.setChecked(true);
                }


            }
        });
        mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "isChecked: onCheckedChanged called: CHECKED. SAVE IT");

                } else {
                    Log.d(TAG, "isChecked: onCheckedChanged called: NOT CHECKED. SAVE IT");

                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton r = (RadioButton)v.findViewById(R.id.completedRadioButton);
                if(r.getVisibility()==View.GONE){
                    Toast.makeText(mContext,"ADDING SOMETHING",Toast.LENGTH_SHORT).show();
                    ViewSwitcher switcher = (ViewSwitcher) v.findViewById(R.id.my_switcher);
                    switcher.showNext(); //or switcher.showPrevious();
                    EditText editText = (EditText) switcher.findViewById(R.id.listItemEdit);
                    editText.setHint("ADD A NEW REMINDER HERE");

                }else{
                    ViewSwitcher switcher = (ViewSwitcher) v.findViewById(R.id.my_switcher);
                    TextView textView = (TextView)v.findViewById(R.id.listItemTextView);
                    switcher.showNext(); //or switcher.showPrevious();
                    EditText editText = (EditText) switcher.findViewById(R.id.listItemEdit);
                    editText.setText(textView.getText());
                }
            }
        });

        ViewSwitcher switcher = (ViewSwitcher) view.findViewById(R.id.my_switcher);
        EditText editText = (EditText)switcher.findViewById(R.id.listItemEdit);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(mContext, "ADDING TO DATABASE", Toast.LENGTH_SHORT).show();
                    mClickInterface.handleClickToCreateNewReminder(((EditText) v).getText().toString());
                    ((ViewSwitcher)v.getParent()).showPrevious();

                    return true;

                }
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG,"onFocusChange called");
                if(!hasFocus){
                    
                    mClickInterface.handleClickToUpdateReminder(,((EditText)v).getText().toString());
                    ((ViewSwitcher)v.getParent()).showPrevious();

                }
            }
        });



*/

        return new ToDoListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoListItemHolder holder, int position) {

        ToDoListItem toDoListItem = (ToDoListItem) mToDoListItems.get(position);
        if (toDoListItem == null) {


            holder.mEditText.setText("+ CLICK HERE TO ADD A REMINDER");
            holder.mRadioButton.setVisibility(View.GONE);
            return;
        }
        holder.mRadioButton.setVisibility(View.VISIBLE);
        holder.mEditText.setText(toDoListItem.getText());
        holder.mReminderId = toDoListItem.getReminder_id();
        ((LinearLayout)holder.itemView.findViewById(R.id.lineItemOptionsButton)).setVisibility(View.GONE);
        holder.mMoreOpts.setVisibility(View.INVISIBLE);



    }


    @Override
    public int getItemCount() {
        return mToDoListItems.size();
    }


    //====================================================================================
    public class ToDoListItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnKeyListener,
            TextView.OnEditorActionListener,View.OnFocusChangeListener{

        public RadioButton mRadioButton;
        public Button mDeleteButton;
        public Button mOptionsButton;
        public TextView mTextView;
        public EditText mEditText;
        public String mReminderId;
        public View mItemView;
        public ImageButton mMoreOpts;




        public ToDoListItemHolder(View itemView) {
            super(itemView);
           mItemView =itemView;
            //mViewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.my_switcher);
            mEditText = (EditText)itemView.findViewById(R.id.listItemEdit);
            mRadioButton = (RadioButton) itemView.findViewById(R.id.completedRadioButton);
            mTextView = (TextView) itemView.findViewById(R.id.listItemTextView);
            mDeleteButton = (Button)itemView.findViewById(R.id.deleteLineItemButton);
            mOptionsButton=(Button)itemView.findViewById(R.id.editLineItemButton);
            mItemView.setOnClickListener(this);
            mMoreOpts = (ImageButton)itemView.findViewById(R.id.moreOptionsButton);
            itemView.setOnKeyListener(this);
            mEditText.setOnFocusChangeListener(this);
            //mEditText.setOnLongClickListener(this);
            mEditText.setOnEditorActionListener(this);

            Button hideOptionsButton = (Button)itemView.findViewById(R.id.hideOptions);
            hideOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMoreOpts.setVisibility(View.GONE);
                    LinearLayout ll = (LinearLayout)mItemView.findViewById(R.id.lineItemOptionsButton);
                    ll.setVisibility(View.GONE);
                    mEditText.setTextColor(Color.BLACK);
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
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickInterface.handleDeleteButton(mReminderId);
                }
            });

            mOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickInterface.handleClick("MORE_OPTIONS");
                }
            });


            mMoreOpts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout ll = (LinearLayout)mItemView.findViewById(R.id.lineItemOptionsButton);

                    if (ll.getVisibility()==View.VISIBLE){
                        Log.d(TAG, "setting delButton to GONE");
                        ll.setVisibility(View.GONE);
                        mEditText.setTextColor(Color.BLACK);

                    }else{
                        ll.setVisibility(View.VISIBLE);
                        Log.d(TAG, "setting delButton to VISIBLE");
                        mEditText.setTextColor(Color.WHITE);
                    }

                }
            });

        }



        @Override
        public void onClick(View v) {


            Log.d(TAG,"onClick called");
            mEditText.requestFocus();
            if(mMoreOpts.getVisibility()!=View.VISIBLE){
                mMoreOpts.setVisibility(View.VISIBLE);

            }
//            if (mRadioButton.getVisibility() == View.GONE) {
//                Toast.makeText(mContext, "ADDING SOMETHING", Toast.LENGTH_SHORT).show();
//
//                //mViewSwitcher.showNext(); //or switcher.showPrevious();
//                EditText editText = (EditText) v.findViewById(R.id.listItemEdit);
//                editText.setHint("ADD A NEW REMINDER HERE");
//
//            } else {
//
//                ImageButton imageButton = (ImageButton)v.findViewById(R.id.moreOptionsButton);
//                imageButton.setVisibility(View.VISIBLE);
                //mViewSwitcher.showNext(); //or switcher.showPrevious();
//                EditText editText = (EditText) mViewSwitcher.findViewById(R.id.listItemEdit);
//                CharSequence theText = mTextView.getText();
//                editText.setText(theText);
//                editText.setSelection(theText.length());
//            }

        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                Toast.makeText(mContext, "ADDING TO DATABASE", Toast.LENGTH_SHORT).show();
                mClickInterface.handleClickToCreateNewReminder(((EditText) v).getText().toString());
                ((ViewSwitcher) v.getParent()).showPrevious();

            }
            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(TAG, "onFocusChange called");
            if(!hasFocus){

                mMoreOpts.setVisibility(View.INVISIBLE);
                mEditText.setTextColor(Color.BLACK);
                //mClickInterface.handleClickToUpdateReminder(mReminderId,((EditText)v).getText().toString());
                //((ViewSwitcher)v.getParent()).showPrevious();

            }else{
                //mMoreOpts.setVisibility(View.VISIBLE);
            }
        }



        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.d(TAG,"Inside onEditorAction");
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId==KeyEvent.KEYCODE_ENTER)
            {
                Toast.makeText(mContext, "ADDING TO DATABASE", Toast.LENGTH_SHORT).show();
                mClickInterface.handleClickToCreateNewReminder(((EditText) v).getText().toString());
                //((ViewSwitcher) v.getParent()).showPrevious();
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        }



    }


    //=================================


}
