package com.avalith.seminariokotlin.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.avalith.seminariokotlin.R
import com.avalith.seminariokotlin.databinding.ActivityLoginBinding
import com.avalith.seminariokotlin.extensions.getEmailPref
import com.avalith.seminariokotlin.extensions.savePref
import com.avalith.seminariokotlin.extensions.showAlert
import com.avalith.seminariokotlin.extensions.signInWithCredential
import com.avalith.seminariokotlin.model.UserData
import com.avalith.seminariokotlin.ui.dialog.LoadingDialog
import com.avalith.seminariokotlin.ui.home.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

const val GOOGLE_SIGN_IN = 100
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private val loadingDialog = LoadingDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setButton()
        session()
        subscribeToLiveData()
    }

    private fun session() {
        getEmailPref()?.let { MainActivity.start(this) }
    }

    private fun subscribeToLiveData() {
        viewModel.succesLiveData.observe(this) { continueToNextActivity(it) }
        viewModel.errorLiveData.observe(this) { dismissAndAlert() }
    }

    private fun dismissAndAlert(){
        loadingDialog.dismiss()
        showAlert()
    }

    private fun continueToNextActivity(user: UserData){
        loadingDialog.dismiss()
        savePref(user)
        MainActivity.start(this)
    }

    private fun setButton() {
        binding.googleButton.setOnClickListener {
            loadingDialog.show(supportFragmentManager)
            setGoogleWidget()
        }
    }

    private fun setGoogleWidget() {
        val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleConfig)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let { account ->
                    signInWithCredential(GoogleAuthProvider.getCredential(account.idToken, null))
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = UserData().map(it.result.user!!)
                                viewModel.saveUserOnDB(user, account)
                            } else {
                                dismissAndAlert()
                            }
                        }
                }
            } catch (exp: ApiException) {
                dismissAndAlert()
            }
        }
    }
}