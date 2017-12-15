package com.example.thekra.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.util.Collections.swap;

public class MainActivity extends AppCompatActivity {
    private Realm myRealm;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private NoteAdapter adapter;
    private RealmResults<Model> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRealm = Realm.getInstance(MainActivity.this);
        //a utility class to add swipe to dismiss and drag & drop support to RecyclerView.
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);

        result = myRealm.where(Model.class).findAllAsync();
        Log.i("RESULT" + result, "LLL");

        recyclerView = findViewById(R.id.recycleView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(result, this);
        recyclerView.setAdapter(adapter);
        Log.i("ADAPTERSIZE"+adapter,"AAAA");
        itemTouchHelper.attachToRecyclerView(recyclerView);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    //the contract between ItemTouchHelper and your application
    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            List<Model> list = new LinkedList<>(result);
            swap(list, fromPosition, toPosition);

            // Notify the adapter of the move
            adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Delete note? ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
// All changes to data must happen in a transaction
                            myRealm.beginTransaction();

// remove a single object
                            Model deleteNote = result.get(position);
                            deleteNote.removeFromRealm();

                            myRealm.commitTransaction();
                            adapter.notifyItemRemoved(position);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            clearView(recyclerView, viewHolder);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }


}
