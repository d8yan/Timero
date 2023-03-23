package com.conestoga.timero;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "db_timero";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String tableTasks = "CREATE TABLE tbl_tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT, isComplete INTEGER);";
        sqLiteDatabase.execSQL(tableTasks);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }

    public Todo getTodo(int id) {
        String query = "SELECT * FROM tbl_tasks WHERE id = ?";

        Cursor cursor = this.getReadableDatabase()
                .rawQuery(query, new String[] { String.valueOf(id) });

        if (cursor != null && Objects.requireNonNull(cursor).getCount() > 0) {
            cursor.moveToFirst();
            int todoId = cursor.getInt(0);
            String task = cursor.getString(1);
            int isComplete = 0;

            Todo todo = new Todo(todoId, task, isComplete);

            cursor.close();

            return todo;
        }

        return new Todo();
    }

    public ArrayList<Todo> getAllTodos() {
        ArrayList<Todo> todos = new ArrayList<Todo>();

        String query = "SELECT * FROM tbl_tasks";

        Cursor cursor = this.getReadableDatabase().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Todo todo = new Todo();
                todo.setId(cursor.getInt(0));
                todo.setTask(cursor.getString(1));
                todo.setIsCompleted(0);

                todos.add(todo);
            }

            cursor.close();
        }

        return todos;
    }

    public void insertTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("task", todo.getTask());
        values.put("isComplete", todo.getIsCompleted());
        db.insert("tbl_tasks", null, values);
        db.close();
    }

    public void updateTodo(Todo todo) {
        System.out.println(todo.getId());
        System.out.println(todo.getTask());
        System.out.println(todo.getIsCompleted());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("task", todo.getTask());
        values.put("isComplete", todo.getIsCompleted());

        db.update("tbl_tasks", values, "id=?", new String[] { String.valueOf(todo.getId()) });
        db.close();
    }

    public void removeTodo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tbl_tasks", "id=?", new String[] { String.valueOf(id) });
        db.close();
    }

    public void clearTasks() {
        String query = "DELETE FROM tbl_tasks WHERE ID IS NOT NULL";

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query);
        db.close();
    }
}

