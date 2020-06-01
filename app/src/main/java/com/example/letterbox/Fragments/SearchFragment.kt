package com.example.letterbox.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letterbox.AdapterClasses.UserAdapter
import com.example.letterbox.ModelClasses.Users
import com.example.letterbox.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 * A simple [Fragment] subclass.
 */

class SearchFragment : Fragment()
{
    private var userAdapter:UserAdapter?=null
    private var mUsers:List<Users>?=null
    private var recyclerView:RecyclerView?=null
    private var searchUsersEditText:EditText?=null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(R.layout.fragment_search, container, false)


        searchUsersEditText=view.findViewById(R.id.searchUsersEditText)
        recyclerView=view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager=LinearLayoutManager(context)



        mUsers=ArrayList()
        retrieveAllUsers()


        searchUsersEditText!!.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {
                //Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                searchForUsers(s.toString().toLowerCase())
            }


            override fun afterTextChanged(s: Editable?)
            {
                //Do nothing
            }

        })


        return view
    }

    private fun retrieveAllUsers()
    {
        val firebaseUserId=FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers= FirebaseDatabase.getInstance().reference.child("users").child(firebaseUserId)                  //"users" is a table inside database and "uid" is like a unique primary key in "users" table. We are getting all the information of that particular user with that particular "uid" and storing it in "refUsers"

        refUsers.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                (mUsers as ArrayList<Users>).clear()

                //function "retrieveAllUsers" should display all users only when there is no entry in Search Bar
                //ie if there is no entry in EditText, it should display all users
                if(searchUsersEditText!!.text.toString() == "")
                {
                    val users: Users? = dataSnapshot.getValue(Users::class.java)
                    Log.i("users", users.toString())

                    //Now we don't want our name to appear in search when we are searching name
                    //firebaseUserId is our userId
                    //user!!.getUid() is a general userId for all the users in the list/table
                    //These shouldn't match

                    if(!(users!!.getUid()).equals(firebaseUserId))
                    {
                        (mUsers as ArrayList<Users>).add(users)
                    }

                }
                userAdapter=UserAdapter(context!!, mUsers!!, false)     //Passing the arguments to "UserAdapter" class
                recyclerView!!.adapter=userAdapter
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }

        })


    }




    private fun searchForUsers(str:String)
    {
        val firebaseUserId=FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers= FirebaseDatabase.getInstance().reference
            .child("users").orderByChild("search")
            .startAt(str)
            .endAt(str+"\uf8ff")

        queryUsers.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot)
            {
                (mUsers as ArrayList<Users>).clear()
                for(snapshot in p0.children)
                {
                    val user:Users?= snapshot.getValue(Users::class.java)

                    //Now we don't want our name to appear in search when we are searching name
                    //firebaseUserId is our userId
                    //user!!.getUid() is a general userId for all the users in the list/table
                    //These shouldn't match
                    if(!(user!!.getUid()).equals(firebaseUserId))
                    {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter=UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter=userAdapter
            }


            override fun onCancelled(p0: DatabaseError)
            {

            }



        })

    }



}
