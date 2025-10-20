package com.example.homie;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    Context context;

    public DatabaseHelper(Context ctx) {
        this.context = ctx;
    }

    // Tạm thời giả lập database
    private static int mockUserCount = 0;

    public int getUserCount() {
        return mockUserCount;
    }

    public boolean insertUser(String username, String password, String fullname, String role) {
        mockUserCount++;
        return true; // Giả lập thêm user thành công
    }
}
