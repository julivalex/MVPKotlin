package mvp

import android.content.ContentValues
import android.database.Cursor
import database.DbHelper
import database.UserTable
import kotlinx.coroutines.experimental.*
import model.User
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext

class UsersModel(private val dbHelper: DbHelper) : CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun loadUsers(load: (List<User>) -> Unit) = launch {
        val users: LinkedList<User> = LinkedList()
        //I/O
        async(Dispatchers.IO) {
            val cursor: Cursor = dbHelper.readableDatabase
                    .query(UserTable.TABLE,
                            null, null, null,
                            null, null, null)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(UserTable.Column.ID))
                val name = cursor.getString(cursor.getColumnIndex(UserTable.Column.NAME))
                val email = cursor.getString(cursor.getColumnIndex(UserTable.Column.EMAIL))

                val user = User(id, name, email)
                users.add(user)
            }
            cursor.close()
        }.await()

        //main thread
        load(users)
    }

    fun addUser(contentValues: ContentValues, complete: () -> Unit) = launch {
        //I/O
        async(Dispatchers.IO) {
            dbHelper.writableDatabase.insert(UserTable.TABLE, null, contentValues)
            delay(1000L)
        }.await()

        //main thread
        complete()
    }

    fun clearUsers(complete: () -> Unit) = launch {
        //I/O
        async(Dispatchers.IO) {
            dbHelper.writableDatabase.delete(UserTable.TABLE, null, null)
            delay(1000L)
        }.await()

        //main thread
        complete()
    }
}