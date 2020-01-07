package com.example.mert.multinote;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

class NotesAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<Notes> notesList;
    private MainActivity mainActivity;

    public NotesAdapter(List<Notes> notesList, MainActivity mainActivity){
        this.notesList = notesList;
        this.mainActivity = mainActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list, parent, false);
        view.setOnClickListener(mainActivity);
        view.setOnLongClickListener(mainActivity);

        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.notesList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notes note = notesList.get(position);
        holder.title.setText(note.getTitle());
        String desc = note.getDescription();
        holder.date.setText(note.getDate());

        if(note.getDescription().length() >= 80) {
            desc = note.getDescription().substring(0, 79)+"...";
        }
        holder.description.setText(desc);
    }
}

