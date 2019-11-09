package com.example.dailyshoppinglist.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailyshoppinglist.R;
import com.example.dailyshoppinglist.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private RecyclerView recyclerView;

    private TextView totalAmount;

    //Global Var
    private String type;
    private int amount;
    private String note;
    private String post_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        totalAmount = findViewById(R.id.total_items_count);


        firebaseAuth = FirebaseAuth.getInstance();
        // get the Current User and it's Id
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uId = firebaseUser.getUid();
        // get location in database to read/write
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        databaseReference.keepSynced(true);

        createRecycler();
        countItems();


    }

    private void countItems() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalAmount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void customDialog() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        View view = layoutInflater.inflate(R.layout.input_field, null);
        final AlertDialog alertDialog = myDialog.create();
        alertDialog.setView(view);

        final EditText type = view.findViewById(R.id.edt_type);
        final EditText amount = view.findViewById(R.id.edt_amount);
        final EditText note = view.findViewById(R.id.edt_note);
        Button save = view.findViewById(R.id.btn_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mType = type.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();
                String mNote = note.getText().toString().trim();

                int IntAmount = Integer.parseInt(mAmount);

                if (TextUtils.isEmpty(mType)) {
                    type.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mAmount)) {
                    amount.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mNote)) {
                    note.setError("Required Field");
                    return;
                }

                String mId = databaseReference.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(mType, IntAmount, mNote, mDate, mId);

                databaseReference.child(mId).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        countItems();
                    }
                });


                Toast.makeText(getApplicationContext(), "Data Added", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    public void createRecycler() {

        FirebaseRecyclerAdapter<Data, myViewHolder> adapter = new FirebaseRecyclerAdapter<Data, myViewHolder>(Data.class, R.layout.data_item, myViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(myViewHolder myViewHolder, final Data data, final int i) {

                myViewHolder.setDate(data.getDate());
                myViewHolder.setType(data.getmType());
                myViewHolder.setAmount(data.getmAmount());
                myViewHolder.setNote("note: " + data.getmNote());

                myViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // save the current ref and inputs to view inside update dialog
                        post_key = getRef(i).getKey();
                        type = data.getmType();
                        amount = data.getmAmount();
                        note = data.getmNote();
                        updateData();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

    }

    public static class myViewHolder extends RecyclerView.ViewHolder {


        View view;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setDate(String date) {
            TextView mDate = view.findViewById(R.id.date);
            mDate.setText(date);


        }

        public void setType(String type) {
            TextView mType = view.findViewById(R.id.type);
            mType.setText(type);
        }

        public void setAmount(int amount) {
            TextView mAmount = view.findViewById(R.id.amount);
            String strAmount = String.valueOf(amount);
            mAmount.setText("amount: "+strAmount);

        }

        public void setNote(String note) {
            TextView mNote = view.findViewById(R.id.note);
            mNote.setText(note);

        }

    }

    public void updateData() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        View view = layoutInflater.inflate(R.layout.update_input_field, null);
        final AlertDialog dialog = mydialog.create();
        dialog.setView(view);


        final EditText updateType = view.findViewById(R.id.edt_update_type);
        final EditText updateAmount = view.findViewById(R.id.edt_update_amount);
        final EditText updateNote = view.findViewById(R.id.edt_update_note);

        // set the current state of inputs
        updateType.setText(type);
        updateType.setSelection(type.length());

        updateAmount.setText(String.valueOf(amount));
        updateAmount.setSelection(String.valueOf(amount).length());

        updateNote.setText(note);
        updateNote.setSelection(note.length());

        Button update = view.findViewById(R.id.btn_update);
        Button delete = view.findViewById(R.id.btn_delete);

        // update state
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mUpdateType = updateType.getText().toString().trim();
                String mUpdateAmount = updateAmount.getText().toString().trim();
                String mUpdateNote = updateNote.getText().toString().trim();
                int intUpdateAmount = Integer.parseInt(mUpdateAmount);

                String mUpdateDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(mUpdateType, intUpdateAmount, mUpdateNote, mUpdateDate, post_key);

                databaseReference.child(post_key).setValue(data);

                Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(post_key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        countItems();
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.log_out:
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}
