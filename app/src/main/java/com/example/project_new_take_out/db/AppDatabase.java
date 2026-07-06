package com.example.project_new_take_out.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.project_new_take_out.db.dao.CartDao;
import com.example.project_new_take_out.db.entity.CartItemEntity;

/**
 * Room 数据库类
 * 单例模式，确保只有一个数据库实例
 */
@Database(entities = {CartItemEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "online_food_order.db";
    private static volatile AppDatabase INSTANCE;

    public abstract CartDao cartDao();

    /**
     * 获取数据库实例（双重检查锁定单例）
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries() // 允许主线程查询（购物车数据量小，不会阻塞UI）
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 销毁数据库实例（一般不需要调用）
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
