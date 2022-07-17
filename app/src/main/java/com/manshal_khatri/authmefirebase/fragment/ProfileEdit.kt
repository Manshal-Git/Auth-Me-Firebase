package com.manshal_khatri.authmefirebase.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.manshal_khatri.authmefirebase.R
import com.manshal_khatri.authmefirebase.database.UserDatabase
import com.manshal_khatri.authmefirebase.databinding.FragmentProfileEditBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileEdit : Fragment() {


    lateinit var binding: FragmentProfileEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_edit,container,false)
        binding = FragmentProfileEditBinding.bind(view)

        val sharedPreferences = activity?.getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val e = sharedPreferences?.getString("email","no email")

        val db = UserDatabase.getDatabase(requireContext()).userDao()

        val user = db.getUser(e!!)
        user.observe(viewLifecycleOwner) {
            binding.editFirstName.text = it.firstName.toEditable()
            binding.editLastName.text = it.lastName.toEditable()
            binding.editAddress.text = it.address.toEditable()
            binding.editPhone.text = it.mobileNo.toEditable()
            binding.editEmailName.text = it.emailId.toEditable()
        }

        binding.btnUpdateProfile.setOnClickListener {
            // todo create user object
            try{
                CoroutineScope(Dispatchers.IO) .launch{
                    // todo send profile update req to server

                    if(true){
                        // todo save user to local database
                    }
                }
                Toast.makeText(activity,"Profile Updated Succesfully",Toast.LENGTH_SHORT).show()
            }catch (e : Exception){
                Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun String.toEditable() : Editable = Editable.Factory.getInstance().newEditable(this)
}