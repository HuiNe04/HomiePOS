package com.example.homie;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HOMI.db"; // file DB trong thư mục assets
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private final String dbPath;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Không cần tạo bảng – vì DB có sẵn trong assets
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Có thể xử lý cập nhật version nếu sau này đổi DB
    }

    // 🟢 Tạo (copy) database từ assets khi lần đầu cài app
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        if (!dbExist) {
            Log.d("DB_STATUS", "Database does NOT exist. Creating...");

            // 🟡 Đảm bảo thư mục đích tồn tại
            File dbFile = new File(dbPath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                Log.d("DB_STATUS", "Created database folder: " + created);
            }

            // 🟡 Tạo file rỗng trước khi copy
            this.getReadableDatabase();
            close();

            // 🟢 Copy file từ assets
            copyDatabase();

            Log.d("DB_STATUS", "✅ Database copied successfully!");
        } else {
            Log.d("DB_STATUS", "📁 Database already exists, skip copying.");
        }
    }

    // 🟢 Kiểm tra DB đã tồn tại trong bộ nhớ máy hay chưa
    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            // Database chưa tồn tại
        }
        if (checkDB != null) {
            checkDB.close();
            return true;
        } else {
            return false;
        }
    }

    // 🟢 Sao chép file HOMI.db từ assets sang thư mục database của app
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

        Log.d("DB_STATUS", "Database copied to: " + dbPath);
    }

    // 🟢 Mở database để đọc/ghi
    public SQLiteDatabase openDatabase() throws SQLException {
        Log.d("DB_STATUS", "Opening database at path: " + dbPath);
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    // 🟢 Đếm số lượng user (phục vụ xác định Admin đầu tiên)
    public int getUserCount() {
        int count = 0;
        SQLiteDatabase db = openDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM USER", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Lỗi khi đếm user: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return count;
    }

    // 🟢 Thêm user mới
    public boolean insertUser(String idUser, String username, String password, String fullname, String role) {
        try {
            SQLiteDatabase db = openDatabase();
            db.execSQL("INSERT INTO USER (ID_USER, USER_NAME, PASSWORD, FULLNAME, ROLE) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{idUser, username, password, fullname, role});
            return true;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Lỗi khi thêm user: " + e.getMessage());
            return false;
        }
    }

    // 🟢 Kiểm tra đăng nhập
    public Cursor checkLogin(String username, String password) {
        SQLiteDatabase db = openDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM USER WHERE USER_NAME=? AND PASSWORD=?", new String[]{username, password});
        } catch (Exception e) {
            Log.e("DB_ERROR", "Lỗi khi đăng nhập: " + e.getMessage());
        }
        return cursor;
    }

    // 🟢 Debug – In danh sách bảng có trong DB
    public void logAllTables() {
        try {
            SQLiteDatabase db = openDatabase();
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            while (c.moveToNext()) {
                Log.d("DB_TABLE", "Table: " + c.getString(0));
            }
            c.close();
        } catch (Exception e) {
            Log.e("DB_ERROR", "Không thể liệt kê bảng: " + e.getMessage());
        }
    }
}
