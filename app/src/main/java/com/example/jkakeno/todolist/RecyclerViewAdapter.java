package com.example.jkakeno.todolist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//This is the Recycler View Adapter class
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    List<Entry> mEntry;
    Entry entry;
    private DbHelper dbHelper;
    private int lastPosition = -1;

    public RecyclerViewAdapter(Context context, ArrayList<Entry> entryList, DbHelper mDbHelper){
        mContext = context;
        mEntry = entryList;
        this.dbHelper = mDbHelper;
        setHasStableIds(true);
    }

//Creates the view holder that will be used as onBindViewHolder parameter
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Entry entry = mEntry.get(position);

        holder.task.setText(entry.getTask());
//Entry's date is long so format to display properly
        holder.date.setText(new SimpleDateFormat("MM/dd").format(new Date(entry.getDate())));
//Link delete button in the holder with the entry id
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//Get the entry id and pass it to the delete method
//NOTE: possible to get entry id as entry class has a id field. The entry is populated with its id in sortEntry().
                dbHelper.deleteTask(entry.id);
                ArrayList<Entry> entryList = dbHelper.sortEntry(MainActivity.selection);
                clear();
                addAll(entryList);
                notifyDataSetChanged();
            }
        });
        setAnimation(holder.itemView,position);
    }


    private void setAnimation(View itemView, int position) {
        if (position > lastPosition){
            Animation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1000);
            itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mEntry.size();
    }

    public void clear() {
        int size = this.mEntry.size();
        this.mEntry.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(ArrayList<Entry> entryList) {
        this.mEntry.addAll(entryList);
    }



//This is the Recycler View holder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView task;
        public TextView date;
        public Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            task = (TextView) itemView.findViewById(R.id.task_title);
            date = (TextView) itemView.findViewById(R.id.date);
            deleteButton = (Button) itemView.findViewById(R.id.btnDelete);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
