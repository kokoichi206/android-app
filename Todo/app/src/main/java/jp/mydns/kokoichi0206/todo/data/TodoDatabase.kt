package jp.mydns.kokoichi0206.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Todo::class],
    version = 1,
)
abstract class TodoDatabase: RoomDatabase() {

    abstract val dao: TodoDao
}