package com.manshal_khatri.authmefirebase.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.manshal_khatri.authmefirebase.R
import com.manshal_khatri.authmefirebase.database.UserDatabase
import com.manshal_khatri.authmefirebase.databinding.FragmentProfileBinding
import com.manshal_khatri.authmefirebase.util.DataStore.preferenceDataStoreAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    lateinit var binding : FragmentProfileBinding
    lateinit var btnEdit : Button
    lateinit var btnLogout : Button
    lateinit var txtUserName : TextView
    lateinit var txtEmail : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        with(view){
            txtUserName = findViewById(R.id.tvDisplayName)
            txtEmail = findViewById(R.id.tvUserName)
            btnLogout = findViewById(R.id.btnLogout)
            btnEdit = findViewById(R.id.btnEditProfile)
        }

        setupCards()

        val sharedPreferences = activity?.getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val e = sharedPreferences?.getString("email","no email")

        val db = UserDatabase.getDatabase(requireContext()).userDao()

        val user = db.getUser(e!!)

        user.observe(viewLifecycleOwner, Observer{
            txtUserName.text = it.firstName
            txtEmail.text = it.emailId
        })

        btnEdit.setOnClickListener {
            // todo navigate to edit profile fragment
        }

        btnLogout.setOnClickListener {
            try{
                CoroutineScope(Dispatchers.IO).launch{
                    activity?.preferenceDataStoreAuth?.edit {
                        it.clear()
                    }
                }
                // Todo Navigate to Auth Activity
            }catch (e : Exception){
                Toast.makeText(activity,e.message, Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }
    private fun setupCards() {
        with(binding) {
            // Title
            cvOrders.tvUserName.text = "Orders"
            cvNotifications.tvUserName.text = "Notifications"
            cvShippings.tvUserName.text = "Shipping"
            // Navigators
            cvOrders.root.setOnClickListener {
                // TODO : Handled while Integration
                Toast.makeText(activity, "Opens Orders", Toast.LENGTH_SHORT).show()
            }
            cvNotifications.root.setOnClickListener {
                // TODO : Handled while Integration
                Toast.makeText(activity, "Opens Notification", Toast.LENGTH_SHORT).show()
            }
            cvShippings.root.setOnClickListener {
                // TODO : Handled while Integration
                Toast.makeText(activity, "Opens Shipping", Toast.LENGTH_SHORT).show()
            }
        }
    }

}