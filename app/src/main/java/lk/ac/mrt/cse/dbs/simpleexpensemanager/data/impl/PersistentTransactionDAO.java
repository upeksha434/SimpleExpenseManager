package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteDatabase db;

    public PersistentTransactionDAO(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        //Log.d("TransactionDAO", "logTransaction() called with: date = [" + date + "], accountNo = [" + accountNo + "], expenseType = [" + expenseType + "], amount = [" + amount + "]");
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        ContentValues values = new ContentValues();
        values.put("date", dateFormat.format(date));
        values.put("account_no", accountNo);
        values.put("expense_type", (expenseType == ExpenseType.INCOME) ? "INCOME" : "EXPENSE");
        values.put("amount", amount);
        db.insert("transaction_log", null, values);


    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List <Transaction>transaction = new ArrayList();
        Cursor cursor = db.query("transaction_log", null, null, null, null, null, null);
        while(cursor.moveToNext()) {
            Date date = new Date(cursor.getLong(2));
            String acc_no= cursor.getString(1);
            ExpenseType expenseType;
            if(cursor.getString(3).equals("INCOME")){
               expenseType = ExpenseType.INCOME;
            }
            else{
               expenseType = ExpenseType.EXPENSE;
            }

            double amount = cursor.getDouble(4);
            Transaction tran = new Transaction(date, acc_no, expenseType, amount);
            transaction.add(tran);
        }
        cursor.close();
        return transaction;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List <Transaction>transaction = new ArrayList();
        Cursor cursor = db.query("transaction_log", null, null, null, null, null, null, String.valueOf(limit));
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date date = format.parse(cursor.getString(2));
            Log.d("ddddddddd",date.toString());
            String acc_no= cursor.getString(1);
            ExpenseType expenseType;
            if(cursor.getString(3).equalsIgnoreCase("INCOME")){
                expenseType = ExpenseType.INCOME;
            }
            else{
                expenseType = ExpenseType.EXPENSE;
            }

            double amount = cursor.getDouble(4);
            Transaction tran = new Transaction(date, acc_no, expenseType, amount);
            transaction.add(tran);
        }
        cursor.close();
        return transaction;


    }
}
