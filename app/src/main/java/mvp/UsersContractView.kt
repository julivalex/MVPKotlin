package mvp

import model.User

interface UsersContractView {

    fun getUserData(): UserData
    fun showUsers(users: List<User>)
    fun showToast(resId: Int)
    fun showProgress()
    fun hideProgress()
}