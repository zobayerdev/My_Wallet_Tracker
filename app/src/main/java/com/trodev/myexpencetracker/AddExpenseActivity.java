package com.trodev.myexpencetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.trodev.myexpencetracker.databinding.ActivityAddExpenseBinding;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {

    ActivityAddExpenseBinding binding;
    private String type;
    private ExpenseModel expenseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra("type");
        expenseModel = (ExpenseModel) getIntent().getSerializableExtra("model");

        if (type == null) {
            type = expenseModel.getType();
            binding.ammount.setText(String.valueOf(expenseModel.getAmount()));
            binding.category.setText(expenseModel.getCategory());
            binding.note.setText(expenseModel.getNote());
        }

        if (type.equals("Income")) {
            binding.incomeRadio.setChecked(true);
        } else {
            binding.expenseRadio.setChecked(true);
        }

        binding.incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Income";
            }
        });


        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Expense";
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if (expenseModel == null) {
            menuInflater.inflate(R.menu.add_menu, menu);
        } else {
            menuInflater.inflate(R.menu.update_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.saveExpense) {
            if (type != null) {
                createExpense();
                finish();
            }
            if (type == null) {
                updateExpense();
                finish();
            }
            return true;
        }
        if (id == R.id.developer) {
            startActivity(new Intent(AddExpenseActivity.this, DeveloperActivity.class));
        }
        if (id == R.id.deleteExpense) {
            deleteExpense();
        }

        return false;
    }

    private void deleteExpense() {
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseModel.getExpenseId())
                .delete();
        finish();
    }

    private void createExpense() {

        String expenseId = UUID.randomUUID().toString();
        String amount = binding.ammount.getText().toString();
        String note = binding.note.getText().toString();
        String category = binding.category.getText().toString();
        boolean incomeChecked = binding.incomeRadio.isChecked();

        if (incomeChecked) {
            type = "Income";
        } else {
            type = "Expense";
        }

        if (amount.trim().length() == 0) {
            binding.ammount.setError("Empty Amount");
            binding.ammount.requestFocus();
            return;
        }

        ExpenseModel expenseModel = new ExpenseModel(expenseId, note, category,
                type, Long.parseLong(amount), Calendar.getInstance().getTimeInMillis()
                , FirebaseAuth.getInstance().getUid());
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(expenseModel);
        finish();

    }

    private void updateExpense() {

        String expenseId = expenseModel.getExpenseId();
        String amount = binding.ammount.getText().toString();
        String note = binding.note.getText().toString();
        String category = binding.category.getText().toString();

        boolean incomeChecked = binding.incomeRadio.isChecked();
        if (incomeChecked) {
            type = "Income";
        } else {
            type = "Expense";
        }

        if (amount.trim().length() == 0) {
            binding.ammount.setError("Empty Amount");
            return;
        }

        ExpenseModel model = new ExpenseModel(expenseId, note, category, type, Long.parseLong(amount),
                expenseModel.getTime(), FirebaseAuth.getInstance().getUid());


        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(model);
        finish();

    }

}