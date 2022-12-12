package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO /*extends SQLiteOpenHelper*/ implements AccountDAO {

    private static final int VERSION=1;
    private static final String TABLE_NAME="account";

    //private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public PersistentAccountDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /*@Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS account(" +
                "account_no VARCHAR PRIMARY KEY," +
                "bank VARCHAR," +
                "acc_holder VARCHAR," +
                "balance REAL" +
                " );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS account");
        onCreate(sqLiteDatabase);
    }*/




    @Override
    public List<String> getAccountNumbersList() {
        //Log.d("DB", "db: "+db);
        String[] projection=new String[]{"account_no"};
        Cursor cursor = db.query(TABLE_NAME,projection,null,null,null,null,null);
        List<String> accountNumbers = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                String grupo=cursor.getString(0);
                accountNumbers.add(grupo);
            }while(cursor.moveToNext());

        }
        cursor.close();


        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {

        String[] projection=new String[]{"account_no","bank","acc_holder","balance"};
        Cursor cursor = db.query(TABLE_NAME,projection,null,null,null,null,null);
        List<Account> AccountsList= new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                Account grupo=new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
                AccountsList.add(grupo);
            }while(cursor.moveToNext());

        }
        cursor.close();

        return AccountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        String[] projection=new String[]{"account_no"};
        String selection="account_no=?";
        String[] selectionArgs=new String[]{accountNo};
        Cursor cursor= db.query(TABLE_NAME,projection, selection,selectionArgs,null,null,null);
        /*returns only one row
        cursor to that row*/
        if(cursor !=null){
            cursor.moveToFirst();
        }
        Account AccountsList=new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));

        cursor.close();
        return AccountsList;
    }

    @Override
    public void addAccount(Account account) {

        ContentValues values = new ContentValues();
        values.put("account_no", account.getAccountNo());
        values.put("bank", account.getBankName());
        values.put("acc_holder", account.getAccountHolderName());
        values.put("balance", account.getBalance());
        db.insert(TABLE_NAME, null, values);


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String selection="account_no=?";
        String[] selectionArgs=new String[]{accountNo};
        db.delete(TABLE_NAME,selection,selectionArgs);

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        /*first need to return the row containing the data of certain account holder*/
        String[] projection=new String[]{"account_no"};
        String selection="account_no=?";
        String[] selectionArgs=new String[]{accountNo};
        Cursor cursor= db.query(TABLE_NAME,projection, selection,selectionArgs,null,null,null);
        /*cursor is pointing to the row containing the data of the account holder
        * if it is not null*/
        if(cursor !=null){
            cursor.moveToFirst();
        }
        ContentValues values=new ContentValues();
        /*check whether the transaction is an expense or income*/
        if(expenseType==ExpenseType.INCOME) {
            values.put("balance", cursor.getDouble(3) + amount);
        }
        else{
            values.put("balance", cursor.getDouble(3) - amount);
        }
        db.update(TABLE_NAME,values,"account_no =?",new String[]{accountNo});

    }


}
