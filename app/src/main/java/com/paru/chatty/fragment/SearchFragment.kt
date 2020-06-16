package com.paru.chatty.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.paru.chatty.AdapterClasses.UserAdapter
import com.paru.chatty.ModelClasses.Users

import com.paru.chatty.R
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private var userAdapter:UserAdapter?=null
    private var mUsers:List<Users>?=null
    private var recyclerView:RecyclerView?=null
    private var searchEditText:EditText?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView=view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager=LinearLayoutManager(context)
        searchEditText=view.findViewById(R.id.searchUsersET)

        mUsers=ArrayList()
        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

    private fun retrieveAllUsers() {
        var firebaseUserID=FirebaseAuth.getInstance().currentUser!!.uid
        var refUsers= FirebaseDatabase.getInstance().reference.child("users")

        refUsers.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
               if(searchEditText!!.text.toString()=="")
               {
                   for (snapshot in p0.children)
                   {
                       val user =snapshot.getValue(Users::class.java)

                       //my account should not be shown
                       if (!(user!!.getUID()).equals(firebaseUserID))
                       {
                           (mUsers as ArrayList<Users>).add(user)
                       }
                   }
               }
                userAdapter= UserAdapter(context!!,mUsers!!,false)
                recyclerView!!.adapter=userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private  fun searchForUsers(str:String)
    {
        var firebaseUserID=FirebaseAuth.getInstance().currentUser!!.uid
        var queryUsers= FirebaseDatabase.getInstance().reference
            .child("users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()

                for (snapshot in p0.children)
                {
                    val user =snapshot.getValue(Users::class.java)

                    //my account should not be shown
                    if (!(user!!.getUID()).equals(firebaseUserID))
                    {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter= UserAdapter(context!!,mUsers!!,false)
                recyclerView!!.adapter=userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
}
