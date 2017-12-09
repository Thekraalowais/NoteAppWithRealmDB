package com.example.thekra.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class EditActivity extends AppCompatActivity {

    private EditText title;
    private EditText desc;
    private Realm myRealm;
    private boolean idValue = false;
    private int id;
    private RealmResults<Model> result;
    private boolean NoteChange = false;
    Model model = new Model();

    private View.OnTouchListener Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            NoteChange = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        myRealm = Realm.getInstance(EditActivity.this);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        Intent intent = getIntent();

        if (intent.hasExtra("id")) {
            setTitle(getString(R.string.edit_note));
            Log.i("OLD NOTE" + intent, "SSSSSSSSs");

        } else {
            setTitle(getString(R.string.add_note));
            Log.i("NEW NOTE" + intent, "SSSSSSSSs");

        }

        if (intent.hasExtra("id")) {
            idValue = true;
            id = intent.getIntExtra("id", 0);
            Log.v("ID", "SSSSSSSSSS" + id);

            result = myRealm.where(Model.class).equalTo("id", id).findAll();
            Log.v("RESULT", "SSSSSSSSSS" + result);

            for (Model model : result) {
                title.setText(model.getTitle());
                Log.v("TITLE", "SSSSSSSSSS" + model.getTitle());
                desc.setText(model.getDes());
            }
        }
        title.setOnTouchListener(Touch);
        desc.setOnTouchListener(Touch);

    }


    public void addOrUpdate() {
        if (idValue) {
            //use new array because No outside changes to a Realm is allowed while iterating a RealmResults. Use iterators methods instead
            List<Model> list = new ArrayList<>();
            list.addAll(result);
            myRealm.beginTransaction();
            for (Model model : list) {
                model.setTitle(title.getText().toString());
                model.setDes(desc.getText().toString());

                myRealm.copyToRealmOrUpdate(model);
                myRealm.commitTransaction();
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            }
        } else {
            model.setId(getNextKey());
            model.setTitle(title.getText().toString());
            model.setDes(desc.getText().toString());

            myRealm.beginTransaction();
            myRealm.copyToRealmOrUpdate(model);
            myRealm.commitTransaction();
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        }
    }

    //تقوم بإنشاء id جديد في كل مرة نضيف عنصر جديد
    private int getNextKey() {
        try {
            return myRealm.where(Model.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                addOrUpdate();
                finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        //
        if (!NoteChange) {
            super.onBackPressed();
            return;
        }
        Log.i("change" + NoteChange, "SSS");

        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setMessage("Discard changes? ")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}

