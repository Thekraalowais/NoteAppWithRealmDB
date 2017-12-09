package com.example.thekra.notebook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>
{
    private Context context;
    private List<Model> note;


    public NoteAdapter(List<Model> note,Context context) {
        this.context = context;
        this.note = note;
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.ViewHolder holder, int position) {
        final Model currentItem = note.get(position);
        holder.title.setText(currentItem.getTitle());
        holder.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("id",currentItem.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return note.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private View click;

        public ViewHolder(View view) {
            super(view);
            click = view;
            title =  view.findViewById(R.id.title);
        }

}

}
