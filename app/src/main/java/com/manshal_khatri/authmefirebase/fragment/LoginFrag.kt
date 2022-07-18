package com.manshal_khatri.authmefirebase.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.manshal_khatri.authmefirebase.ProfileActivity
import com.manshal_khatri.authmefirebase.R
import com.manshal_khatri.authmefirebase.database.UserDatabase
import com.manshal_khatri.authmefirebase.databinding.FragLoginBinding
import com.manshal_khatri.authmefirebase.model.User
import com.manshal_khatri.authmefirebase.util.Constants
import com.manshal_khatri.authmefirebase.util.DataStore
import com.manshal_khatri.authmefirebase.util.DataStore.preferenceDataStoreAuth
import com.manshal_khatri.authmefirebase.util.ExceptionHandler
import com.manshal_khatri.authmefirebase.util.LoadingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFrag : Fragment() {
    lateinit var binding : FragLoginBinding
    lateinit var d : LoadingScreen
    lateinit var dd : Dialog
    lateinit var googleSignInOptions : GoogleSignInOptions
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.frag_login, container, false)
        binding = FragLoginBinding.bind(view)
        d = LoadingScreen(activity as Context)
        dd = d.loadingScreen()
        val email = binding.ETEmail
        val password = binding.ETPassword
        auth = FirebaseAuth.getInstance()
        with(binding){
            TVRegister.setOnClickListener { findNavController().navigate(R.id.action_loginFrag_to_registerFrag) }
            TVForgotPassword.setOnClickListener { findNavController().navigate(R.id.action_loginFrag_to_forgotPasswordFragment) }
            BtnLogin.setOnClickListener {
                 if(!isDataFillled(email))else if(!isDataFillled(password))else{
                    loginUser(email.text.toString(),password.text.toString())
                }
            }
            signInWithGoogle.setOnClickListener {
                googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestProfile().build()
                googleSignInClient = GoogleSignIn.getClient(requireActivity(),googleSignInOptions)
                startActivityForResult(googleSignInClient.signInIntent,Constants.CODE_GOOGLE_SIGNIN)
            }
        }
        return view
    }
    private fun isDataFillled(view: TextView) : Boolean{
        if (TextUtils.isEmpty(view.text.toString().trim() { it <= ' ' })) {
            when(view){
                binding.ETEmail -> Snackbar.make(view, "Email is required", 1000).show()
                binding.ETPassword -> Snackbar.make(view, "Password is required", 1000).show()
            }
            return false
        }
        return true
    }
    fun loginUser(email : String,password : String){
        d.toggleDialog(dd) //show

       lifecycleScope.launch {
           try {
               // todo send login req to server with appropriate login method
               auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                   getUserFromCurrentUser()
               }.addOnFailureListener { e ->
                   activity?.let { ExceptionHandler.catchOnContext(it, e.toString()) }
                   d.toggleDialog(dd)
               }
           }catch (e:Exception){
               activity?.let { ExceptionHandler.catchOnContext(it, getString(R.string.generalErrorMsg)) }
               d.toggleDialog(dd)
           }
       }
    }
    fun onSimpleResponse(task : String, user: User){
        d.toggleDialog(dd)  // hide
        lifecycleScope.launch{
            // todo change it to user.uid or token etc
            storeStringPreferences(DataStore.JWT_TOKEN,user.emailId)
        }

        /* val sharedPreference =  requireActivity().getSharedPreferences("user_e",Context.MODE_PRIVATE)
      var editor = sharedPreference.edit()
      editor.putString("email",user.emailId)
      editor.commit()*/

        val intent = Intent(requireActivity(), ProfileActivity::class.java)

        val u = User(
            emailId = user.emailId,
            firstName = user.firstName,
            lastName = user.lastName,
            mobileNo = user.mobileNo,
            address = user.address,
            token = user.token,
            otp = "hi"
        )
        val db = UserDatabase.getDatabase(this.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            db.userDao().insertUser(u)
        }

        startActivity(intent)
        activity?.finish()

    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == Constants.CODE_GOOGLE_SIGNIN){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                task.getResult(ApiException::class.java)
//                task.result.idToken?.let {
//                    lifecycleScope.launch {
//                        storeStringPreferences(DataStore.JWT_TOKEN, it)
//                    }
//                        // Got an ID token from Google. Use it to authenticate
//                        // with Firebase.
//                    }
//                val firebaseCredential = GoogleAuthProvider.getCredential(task.result.idToken, null)
//                auth.signInWithCredential(firebaseCredential)
//                    .addOnCompleteListener(requireActivity()) { task ->
//                        if (task.isSuccessful) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success")
//                            getUserFromCurrentUser()
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.exception)
//
//                        }
//                    }
//               // Todo  Store user on firestore's collection
//            }catch (e : ApiException){
//                Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
//            }
//
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
    suspend fun storeStringPreferences(key: String ,value : String){
        activity?.let{ dsContext ->
            dsContext.preferenceDataStoreAuth.edit {
                it[stringPreferencesKey(key)] = value
            }
        }
    }
    fun getUserFromCurrentUser(){
        val fbUser = auth.currentUser
        if(fbUser!=null){
            val user = User(fbUser.email.toString(),
                fbUser.displayName.toString().substringBefore(" "),
                fbUser.displayName.toString().substringAfter(" "))
            onSimpleResponse("Login",user)
        }
    }
}