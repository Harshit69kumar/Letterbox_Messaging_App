package com.example.letterbox

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity()
{
    private lateinit var mAuth : FirebaseAuth               //Reference to our Firebase
    private lateinit var refUsers : DatabaseReference       //Reference to our database
    private var firebaseUserId:String=""                    //Once the authentication is done, each user will carry it's own Id from Firebase

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener()
        {
            val intent= Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }



        mAuth=FirebaseAuth.getInstance()


        register_button.setOnClickListener()
        {
            registerUser()
        }


    }



    private fun registerUser()
    {
        val username:String = username_register.text.toString()
        val email:String = email_register.text.toString()
        val password:String = password_register.text.toString()

        if(username=="")
        {
            Toast.makeText(this, "Please enter your Username", Toast.LENGTH_SHORT).show()
        }
        else if(email=="")
        {
            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show()
        }
        else if(password=="")
        {
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show()
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task ->
                if (task.isSuccessful)                      //if registration is successful, then store the data in HashMap and store it in Firebase and then send the user to MainActivityPage
                {
                    firebaseUserId=mAuth.currentUser!!.uid
                    refUsers=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUserId)          //we will accept all the "users" data and pass the "FirebaseUserId"

                    val userHashMap=HashMap<String, Any>()
                    userHashMap["uid"]=firebaseUserId
                    userHashMap["username"]=username
                    userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/letterbox-7dd3e.appspot.com/o/profile.jpg?alt=media&token=f6828a82-872d-465b-815e-1f2d0c56ff47"
                    userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/letterbox-7dd3e.appspot.com/o/coverphoto.jpg?alt=media&token=eb6fdb7b-b3ce-43d3-b7e7-0927a7eab499"
                    userHashMap["status"]="offline"
                    userHashMap["search"]=username.toLowerCase()
                    userHashMap["facebook"]="https://m.facebook.com"
                    userHashMap["instagram"]="https://m.instagram.com"
                    userHashMap["website"]="https://www.google.com"

                    //Now add the hashmap to our database (refUsers is our database)
                    //"username", "profile", "uid", "status", "search" etc are all "child" or "children"
                    refUsers.updateChildren(userHashMap)                            //if storing the data in HashMap and storing it in Firebase is successful, and then send the user to MainActivityPage
                        .addOnCompleteListener{task ->
                            if(task.isSuccessful)
                            {
                                val intent=Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)       //Now we don't want the user to go to WelcomeActivity by clicking Back button. He can only go back there when LogOut option is pressed. To stop these intents, we use Intent.FLAG
                                startActivity(intent)
                                finish()
                            }
                        }

                }
                else                                                                 //if the registration is unsuccessful, display the respective error message
                {
                    Toast.makeText(this, "Error Message: "+task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }


    }



}
