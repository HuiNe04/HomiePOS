package com.example.homie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HOMI.db";  // tên file DataBase
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private String dbPath;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không tạo bảng vì đã có sẵn trong file HOMI.db
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu có thay đổi DB version, có thể copy lại file
    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        if (!dbExist) {
            this.getReadableDatabase();
            copyDatabase();
        }
    }

    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            // DB chưa tồn tại
        }
        if (checkDB != null) checkDB.close();
        return checkDB != null;
    }

    private void copyDatabase() throws IOException {
        InputStream input = context.getAssets().open(DATABASE_NAME);
        OutputStream output = new FileOutputStream(dbPath);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        output.flush();
        output.close();
        input.close();
    }

    public SQLiteDatabase openDatabase() throws SQLException {
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    // 🟢 Hàm thêm sản phẩm mới
    public boolean insertProduct(String idProduct, String name, double price, int stock) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.execSQL("INSERT INTO PRODUCT (ID_PRODUCT, NAME, PRICE, STOCK) VALUES (?, ?, ?, ?)",
                    new Object[]{idProduct, name, price, stock});
            Log.d("DB_PRODUCT", "✅ Thêm sản phẩm thành công: " + name);
            return true;
        } catch (Exception e) {
            Log.e("DB_PRODUCT", "❌ Lỗi thêm sản phẩm: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) db.close();
        }
    }


    // 🟢 Hàm lấy danh sách sản phẩm từ DB
    public ArrayList<String> getAllProducts() {
        ArrayList<String> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            cursor = db.rawQuery("SELECT NAME, PRICE FROM PRODUCT", null);

            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                double price = cursor.getDouble(1);
                products.add(name + " - " + price + "₫");
            }

        } catch (Exception e) {
            Log.e("DB_PRODUCT", "❌ Lỗi đọc sản phẩm: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return products;
    }
}


