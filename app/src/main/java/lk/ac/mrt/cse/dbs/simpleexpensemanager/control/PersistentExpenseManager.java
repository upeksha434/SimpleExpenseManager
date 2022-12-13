package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager {
    private Context context;
    @SuppressLint("SdCardPath")
    private static final String DB_PATH = "/data/data/lk.ac.mrt.cse.dbs.simpleexpensemanager/databases/200672M.db";

    public PersistentExpenseManager(Context context) {
        this.context=context;
        setup();
    }

    @Override
    public void setup() {
        SQLiteDatabase db = openOrCreateDatabase(DB_PATH, null, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS account(" +
                "account_no VARCHAR PRIMARY KEY," +
                "bank VARCHAR," +
                "acc_holder VARCHAR," +
                "balance FLOAT" +
                " );");

        db.execSQL("CREATE TABLE IF NOT EXISTS transaction_log(" +
                "transaction_id INTEGER PRIMARY KEY," +
                "account_no VARCHAR," +
                "date DATE," +
                "expense_type VARCHAR," +
                "amount FLOAT," +
                "FOREIGN KEY (account_no) REFERENCES account(account_no)" +
                " );");


        PersistentAccountDAO accountDAO = new PersistentAccountDAO(db);

        setAccountsDAO(accountDAO);

        setTransactionsDAO(new PersistentTransactionDAO(db));

    }
}
