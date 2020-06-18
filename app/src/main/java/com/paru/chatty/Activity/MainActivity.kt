package com.paru.chatty.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val progressBar= ProgressDialog(this)
        progressBar.setMessage("Loading....")
        progressBar.show()

        firebaseUser=FirebaseAuth.getInstance().currentUser
        refUsers=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        val toolbar:Toolbar=findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar_main)
        supportActionBar!!.title=""

        val tabLayout:TabLayout=findViewById(R.id.tab_layout)
        val viewPager:ViewPager=findViewById(R.id.view_pager)
        val viewPageAdapter=
            ViewPageAdapter(
                supportFragmentManager
            )

        viewPageAdapter.addFragment(ChatsFragment(),"Chats")
        viewPageAdapter.addFragment(SearchFragment(),"Search")
        viewPageAdapter.addFragment(SettingsFragment(),"Settings")

        viewPager.adapter=viewPageAdapter
        tab_layout.setupWithViewPager(viewPager)

        //display username and profile picture
        refUsers!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user =p0.getValue(Users::class.java)

                    user_name.text=user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(profile_image)
                    progressBar.dismiss()
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
                    WelcomeActivity::class.java)
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
}
