package mvp

import android.content.ContentValues
import com.java.note.mvpkotlin.R
import database.UserTable

class UsersPresenter(private val model: UsersModel) {

    private var view: UsersContractView? = null

    fun attachView(view: UsersContractView) {
        this.view = view
    }

    fun detachView() {
        view = null
    }

    fun viewIsReady() {
        loadUsers()
    }

    private fun loadUsers() {
        model.loadUsers { it -> view?.showUsers(it) }
    }

    fun add() {
        val userData = view?.getUserData() ?: return
        if (userData.name.isEmpty() || userData.email.isEmpty()) {
            view?.showToast(R.string.empty_values)
            return
        }

        val contentValues = ContentValues(2)
        contentValues.put(UserTable.Column.NAME, userData.name)
        contentValues.put(UserTable.Column.EMAIL, userData.email)

        view?.showProgress()
        model.addUser(contentValues) {
            view?.hideProgress()
            loadUsers()
        }
    }

    fun clear() {
        view?.showProgress()
        model.clearUsers {
            view?.hideProgress()
            loadUsers()
        }
    }
}