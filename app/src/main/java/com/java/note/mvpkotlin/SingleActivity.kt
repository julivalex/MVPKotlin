package com.java.note.mvpkotlin

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import common.UserAdapter
import database.DbHelper
import database.UserTable
import kotlinx.android.synthetic.main.activity_single.*
import kotlinx.coroutines.experimental.*
import model.User
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext

class SingleActivity : AppCompatActivity(), CoroutineScope {

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var userAdapter: UserAdapter
    private lateinit var dbHdbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        init()
        loadUsersTask()
    }

    private fun init() {
        addButton.setOnClickListener { addUser() }
        clearButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            clearUsersTask()
        }

        userAdapter = UserAdapter()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        recyclerViewList.layoutManager = layoutManager
        recyclerViewList.adapter = userAdapter

        dbHdbHelper = DbHelper(this)
    }

    private fun addUser() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, R.string.empty_values, Toast.LENGTH_LONG).show()
            return
        }

        val contentValues = ContentValues(2)
        contentValues.put(UserTable.Column.NAME, name)
        contentValues.put(UserTable.Column.EMAIL, email)

        progressBar.visibility = View.VISIBLE
        addUserTask(contentValues)
    }

    private fun loadUsersTask() = launch {

        val users: LinkedList<User> = LinkedList()

        //I/O
        async(Dispatchers.IO) {
            val cursor: Cursor = dbHdbHelper.readableDatabase.query(UserTable.TABLE,
                    null, null, null, null, null, null)

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
        userAdapter.setData(users = users)
    }

    private fun addUserTask(contentValues: ContentValues) = launch {

        //I/O
        async(Dispatchers.IO) {
            dbHdbHelper.writableDatabase.insert(UserTable.TABLE, null, contentValues)
            delay(1000L)
        }.await()

        //main thread
        progressBar.visibility = View.INVISIBLE
        loadUsersTask()
    }

    private fun clearUsersTask() = launch {

        //I/O
        async(Dispatchers.IO) {
            dbHdbHelper.writableDatabase.delete(UserTable.TABLE, null, null)
            delay(1000L)
        }.await()

        //main thread
        progressBar.visibility = View.INVISIBLE
        loadUsersTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}