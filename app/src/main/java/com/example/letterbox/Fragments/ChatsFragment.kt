package com.example.letterbox.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letterbox.AdapterClasses.UserAdapter
import com.example.letterbox.ModelClasses.Chatlist
import com.example.letterbox.ModelClasses.Users
import com.example.letterbox.Notifications.Token

import com.example.letterbox.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment()
{
    private var userAdapter:UserAdapter?=null
    private var mUsers:List<Users>?=null
    private var usersChatList:List<Chatlist>?=null
    lateinit var recycler_view_chatlist:RecyclerView
    private var firebaseUser:FirebaseUser?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chatlist=view.findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager= LinearLayoutManager(context)

        firebaseUser=FirebaseAuth.getInstance().currentUser

        usersChatList=ArrayList()

        val ref=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                //Now we are going to retrieve the "ChatList" one by one and going to add it to ArrayList "usersChatList"
                //Let's say I am currently logined as "Anonymous", ie "myCurrentAccount" or "fireBaseUser" is "Anonymous" and my UID is "tEQziC0sAwV7pBAV1RUYD88GTie2"
                //So first it will go "tEQziC0sAwV7pBAV1RUYD88GTie2" under "ChatList ie mY Current Account under whom I am logged in rn
                //It will see whom I have talked to
                //For instance, "Anonymous" has talked to four people namely
                // "3fwEFcYpmPNrbQtasVeurZcbtPP2" (Rahasya)
                //"E3VXBAH963evQXt8Pbr2jUeVRb02" (Himani)
                //"SAXnpJepqHSvdRuezLbsUa5SfWj1" (Rithwik)
                //JX7lixGwR5U0I5g9aayj5Bys8AU2 (Dwijesh)
                //So all these 4 users will get stored in my ArrayList "usersChatList"
                (usersChatList as ArrayList).clear()

                for(dataSnapshot in p0.children)
                {
                    val chatlist=dataSnapshot.getValue(Chatlist::class.java)

                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatLists()
            }

            override fun onCancelled(p0: DatabaseError)
            {
                TODO("Not yet implemented")
            }
        })


        //This is for notification part
        updateToken(FirebaseInstanceId.getInstance().token)


        return view
    }


    private fun retrieveChatLists()
    {
        mUsers=ArrayList()

        val ref=FirebaseDatabase.getInstance().reference.child("users")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()

                for (dataSnapshot in p0.children)
                {
                    val user=dataSnapshot.getValue(Users::class.java)

                    for(eachChatList in usersChatList!!)
                    {
                        //Now that we have got whom "we" (firebaseUser) have talked to stored inside "usersChatList"
                        //Its time we get info about their profile
                        //So we traverse through "users" section in Storage and see if any of the "user in users" matches that with the "user stored in usersChatList"
                        //If the match is found, then all the info about that user will be stored in ArrayList "mUsers"
                        //Here we are storing all the users whom the person has talked to in the ArrayList "mUsers"
                        //We are doing this because we have to display their profilePhoto and Username etc on "ChatFragment"
                        if((user!!.getUid()).equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter= UserAdapter(context!!, (mUsers as ArrayList), true)
                recycler_view_chatlist.adapter=userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }


    //This is for Notification part
    private fun updateToken(token: String?)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1= Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }


}
