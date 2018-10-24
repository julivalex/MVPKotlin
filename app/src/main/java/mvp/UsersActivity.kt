package mvp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.java.note.mvpkotlin.R
import common.UserAdapter
import database.DbHelper
import kotlinx.android.synthetic.main.activity_single.*
import model.User

class UsersActivity : AppCompatActivity(), UsersContractView {

    private lateinit var userAdapter: UserAdapter
    private lateinit var presenter: UsersPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        init()
    }

    private fun init() {
        addButton.setOnClickListener { presenter.add() }
        clearButton.setOnClickListener { presenter.clear() }

        userAdapter = UserAdapter()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        recyclerViewList.layoutManager = layoutManager
        recyclerViewList.adapter = userAdapter

        val dbHelper = DbHelper(this)
        val userModel = UsersModel(dbHelper)

        presenter = UsersPresenter(userModel)
        presenter.attachView(this)
        presenter.viewIsReady()
    }

    override fun showUsers(users: List<User>) {
        userAdapter.setData(users)
    }

    override fun showToast(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun getUserData() = UserData(nameEditText.text.toString(), emailEditText.text.toString())

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}