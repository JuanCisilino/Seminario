package com.avalith.seminariokotlin.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avalith.seminariokotlin.model.UserData
import com.avalith.seminariokotlin.repositories.FirebaseRepo
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LoginViewModel: ViewModel() {

    val errorLiveData = MutableLiveData<Unit>()
    val succesLiveData = MutableLiveData<UserData>()

    private val repo = FirebaseRepo()

    fun saveUserOnDB(user: UserData, account: GoogleSignInAccount){
        account.id
            ?.let {
                repo.saveUser(user)
                succesLiveData.value = user
            }
            ?:run {
                errorLiveData.value = Unit
            }
    }
}