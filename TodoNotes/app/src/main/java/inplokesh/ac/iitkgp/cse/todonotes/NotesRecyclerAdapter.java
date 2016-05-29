package inplokesh.ac.iitkgp.cse.todonotes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lokeshponnada on 5/28/16.
 */
public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private ArrayList<Notes> notesList;
    private Context context;

    public NotesRecyclerAdapter(ArrayList<Notes> notesList, Context context) {
        Log.d("MainActivity", "Notes list in adapter is " + notesList.size());
        this.notesList = notesList;
        this.context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        private String docId;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent noteDetailIntent = new Intent(context, NewnoteActivity.class);
                    noteDetailIntent.putExtra("docId", docId);
                    noteDetailIntent.putExtra("title", title.getText());
                    noteDetailIntent.putExtra("content", content.getText());
                    context.startActivity(noteDetailIntent);
                }
            });
        }


    }

    public void add(int position, Notes note) {
        notesList.add(position, note);
        notifyItemInserted(position);
    }

    public void remove(Notes note) {
        int position = notesList.indexOf(note);
        notesList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public NotesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Log.d("MainActivity", "Setting bholder for " + position);
        holder.title.setText(notesList.get(position).getTitle());
        holder.content.setText(notesList.get(position).getContent());
        holder.docId = notesList.get(position).getDocId();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return notesList.size();
    }

}
