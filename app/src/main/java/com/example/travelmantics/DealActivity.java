package com.example.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText editTitle;
    EditText editDescription;
    EditText editPrice;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseUtil.openFbReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mDatabaseReference = mFirebaseDatabase.getReference().child("traveldeals");
        editTitle = (EditText) findViewById(R.id.editTitle);
        editDescription = (EditText) findViewById(R.id.editDescription);
        editPrice = (EditText) findViewById(R.id.editPrice);

        //show the deal activity class to user for editing purposes
        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;

        //setting up the title,price and description for editing, if travel deal is empty new will be created
        editTitle.setText(deal.getTitle());
        editDescription.setText(deal.getDescription());
        editPrice.setText(deal.getPrice());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;

            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal has been Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            //set default to return to the main class
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    //method to save new deal and edit deal if it already exists
    private void saveDeal() {

        //String title= editTitle.getText().toString();
        //String description= editDescription.getText().toString();
        //String price= editPrice.getText().toString();

        //creating new travel deal

        //TravelDeal deal = new TravelDeal(title,description,price, "" );

        //Instead of using strings we use the Travel deal object for the three values
        deal.setTitle(editTitle.getText().toString());
        deal.setDescription(editDescription.getText().toString());
        deal.setPrice(editPrice.getText().toString());

        //checking if its a new deal or existing
        //if its new use push to insert record to realtime else get id of the deal and edit using
        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

    //method to delete deal
    //if there is no deal the a msg will be displayed to user otherwise its deleted
    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();
    }

    //method to return to list activity after saving

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

        private void clean () {
            editTitle.setText("");
            editPrice.setText("");
            editDescription.setText("");
            editTitle.requestFocus();
        }
    private void enableEditTexts(boolean isEnabled) {
        editTitle.setEnabled(isEnabled);
        editDescription.setEnabled(isEnabled);
        editPrice.setEnabled(isEnabled);
    }
    }
