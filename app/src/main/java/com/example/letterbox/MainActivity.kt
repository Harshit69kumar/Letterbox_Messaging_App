package com.example.letterbox

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.letterbox.Fragments.ChatsFragment
import com.example.letterbox.Fragments.SearchFragment
import com.example.letterbox.Fragments.SettingsFragment
import com.example.letterbox.ModelClasses.Chat
import com.example.letterbox.ModelClasses.Users
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity()
{
    var refUsers: DatabaseReference?=null
    var firebaseUser: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        //Now getting information about that particular user from the database
        firebaseUser=FirebaseAuth.getInstance().currentUser             //Storing the cuurent user into the "firebaseUser"
        refUsers=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)                  //"users" is a table inside database and "uid" is like a unique primary key in "users" table. We are getting all the information of that particular user with that particular "uid" and storing it in "refusers"


        val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""     //Set the title with null


        val tabLayout:TabLayout=findViewById(R.id.tabLayout)
        val viewPager:ViewPager=findViewById(R.id.viewPager)
//        val viewPageAdapter=ViewPageAdapter(supportFragmentManager)
//
//        viewPageAdapter.addFragment(ChatsFragment(), "Chats")
//        viewPageAdapter.addFragment(SearchFragment(), "Search")
//        viewPageAdapter.addFragment(SettingsFragment(), "Settings")
//
//        viewPager.adapter=viewPageAdapter
//        tabLayout.setupWithViewPager(viewPager)

        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                val viewPageAdapter=ViewPageAdapter(supportFragmentManager)
                var countUnreadMessages=0

                for(dataSnapshot in p0.children)
                {
                    val chat=dataSnapshot.getValue(Chat::class.java)

                    //Traverse through all the chats
                    //And if the chats were meant for us (ie if we were the receiver),
                    //Then among these chats, how many are unread
                    if(chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.getIsMessageSeen().equals("True"))
                    {
                        countUnreadMessages+=1
                    }
                }

                if(countUnreadMessages==0)              //If no unread message exists, then display as it is
                {
                    viewPageAdapter.addFragment(ChatsFragment(), "Chats")
                }
                else                                    //If unread messages exist
                {
                    viewPageAdapter.addFragment(ChatsFragment(), "Chats ($countUnreadMessages)")
                }



                viewPageAdapter.addFragment(SearchFragment(), "Search")
                viewPageAdapter.addFragment(SettingsFragment(), "Settings")

                viewPager.adapter=viewPageAdapter
                tabLayout.setupWithViewPager(viewPager)

            }

            override fun onCancelled(p0: DatabaseError)
            {
                TODO("Not yet implemented")
            }
        })


        //Now we will display username and profile picture
        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                //before updating profile pic and username, we check if the user actually exists
                //data stored in "refUsers" is passed to "p0"
                //now we can extract "username" and "profile" from "p0"
                //The best way to do so is via Model Class (Remember MVVC?)
                //Our Model Class name is "Users" and it's stored under package "ModelClasses"
                if(p0.exists())
                {
                    val user: Users?=p0.getValue(Users::class.java)

                    username.text=user!!.getUsername()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(profilePic)      //You remember Glide Library...similar to that is Picasso to load image faster....while its loading we can use a "placeholder" as we did in "Glide Library" i.e. until the image is loading, placeholder image will be shown in its place
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId)
        {
            R.id.action_logout ->
            {
                FirebaseAuth.getInstance().signOut()

                val intent= Intent(this, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)       //Now we don't want the user to go to WelcomeActivity by clicking Back button. He can only go back there when LogOut option is pressed. To stop these intents, we use Intent.FLAG
                startActivity(intent)
                finish()

                return true
            }
        }

        return false
    }

    internal class ViewPageAdapter (fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
    {
        private val fragments:ArrayList<Fragment>
        private val titles:ArrayList<String>

        init                    //init is basically a constructor to initialise the global variables xD
        {
            fragments= ArrayList<Fragment>()
            titles=ArrayList<String>()
        }


        override fun getItem(position: Int): Fragment
        {
            return fragments[position]
        }

        override fun getCount(): Int
        {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title:String)
        {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence?
        {
            return titles[position]
        }

    }

}
