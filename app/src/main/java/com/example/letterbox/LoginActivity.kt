package com.example.letterbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity()
{
    private lateinit var mAuth : FirebaseAuth               //Reference to our Firebase


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener()
        {
            val intent= Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth=FirebaseAuth.getInstance()


        login_button.setOnClickListener()
        {
            loginUser()
        }

    }

    private fun loginUser()
    {
        val email:String = email_login.text.toString()
        val password:String = password_login.text.toString()

        if(email=="")
        {
            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show()
        }
        else if(password=="")
        {
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show()
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful)               //if login is successful then send the user to MainActivityPage
                    {
                        val intent=Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)       //Now we don't want the user to go to WelcomeActivity by clicking Back button. He can only go back there when LogOut option is pressed. To stop these intents, we use Intent.FLAG
                        startActivity(intent)
                        finish()
                    }
                    else                                //if the login is unsuccessful, display the respective error message
                    {
                        Toast.makeText(this, "Error Message: "+task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }
}
