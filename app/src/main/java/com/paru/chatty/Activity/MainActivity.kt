package com.paru.chatty.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.paru.chatty.ModelClasses.Chat
import com.paru.chatty.ModelClasses.Chatlist
import com.paru.chatty.ModelClasses.Users
import com.paru.chatty.R
import com.paru.chatty.fragment.ChatsFragment
import com.paru.chatty.fragment.SearchFragment
import com.paru.chatty.fragment.SettingsFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var refUsers:DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null
    lateinit var progressLayout:RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressLayout=findViewById(R.id.progressLayout)

        firebaseUser=FirebaseAuth.getInstance().currentUser
        refUsers=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        val toolbar:Toolbar=findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar_main)
        supportActionBar!!.title=""

        val tabLayout:TabLayout=findViewById(R.id.tab_layout)
        val viewPager:ViewPager=findViewById(R.id.view_pager)


        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val viewPageAdapter= ViewPageAdapter(supportFragmentManager)
                var countUnreadMessages=0

                for (dataSnapshot in p0.children)
                {
                    val chat=dataSnapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.isIsSeen())
                    {
                        countUnreadMessages += 1
                    }
                }
                if (countUnreadMessages==0)
                {
                    viewPageAdapter.addFragment(ChatsFragment(),"Chats")
                }
                else
                {
                    viewPageAdapter.addFragment(ChatsFragment(),"Chats ($countUnreadMessages)")
                }
                viewPageAdapter.addFragment(SearchFragment(),"Search")
                viewPageAdapter.addFragment(SettingsFragment(),"Settings")
                viewPager.adapter=viewPageAdapter
                tab_layout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //display username and profile picture
        refUsers!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user =p0.getValue(Users::class.java)

                    user_name.text=user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(profile_image)
                    progressLayout.visibility=View.GONE
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.action_logout ->
            {
                FirebaseAuth.getInstance().signOut()

                val intent= Intent(this@MainActivity,
                    Splash_Activity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                return true
            }
        }
        return false
    }


    internal  class ViewPageAdapter(fragmentManager: FragmentManager):FragmentPagerAdapter(fragmentManager)
    {
        private lateinit var fragments:ArrayList<Fragment>
        private lateinit var titles:ArrayList<String>

        init {
            fragments= ArrayList<Fragment>()
            titles= ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment,title:String)
        {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }

    private fun updateStatus(status:String)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        val hashmap= HashMap<String,Any>()
        hashmap["status"] = status
        ref!!.updateChildren(hashmap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }
}
