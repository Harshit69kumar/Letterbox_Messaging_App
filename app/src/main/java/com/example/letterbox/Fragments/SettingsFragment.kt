package com.example.letterbox.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.letterbox.ModelClasses.Users

import com.example.letterbox.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * A simple [Fragment] subclass.
 */



class SettingsFragment : Fragment()
{
    var usersReference:DatabaseReference?=null
    var firebaseUser: FirebaseUser?=null
    private val RequestCode=438

    private var imageUri: Uri?=null
    private var storageRef: StorageReference?=null
    private var coverChecker:String?=null

    private var socialMediaChecker:String?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser=FirebaseAuth.getInstance().currentUser         //Storing the current user in variable "firebaseUser"
        usersReference=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)                        //Extracting "uid" of currentUser ie "firebaseUser" from the table "users" and storing it in variable "usersReference"

        storageRef=FirebaseStorage.getInstance().reference.child("User Images")             //This will create a folder named "User Images" in our storage in Firebase which will store all our cover and profile images



        usersReference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    val users:Users?=p0.getValue(Users::class.java)

                    if(context!=null)
                    {
                        view.usernameSettings.text=users!!.getUsername()                                    //Setting username in the Settings fragment
                        Picasso.get().load(users!!.getProfile()).into(view.profileImageSettings)            //Setting default profile iimage for the time being
                        Picasso.get().load(users!!.getCover()).into(view.coverImageSettings)                //Setting default cover iimage for the time being
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })



        view.profileImageSettings.setOnClickListener()
        {
            coverChecker="profilePic"
            pickImage()
        }

        view.coverImageSettings.setOnClickListener()
        {
            coverChecker="coverPic"
            pickImage()
        }


        view.setFacebook.setOnClickListener()
        {
            socialMediaChecker="Facebook"
            setSocialLinks()
        }


        view.setInstagram.setOnClickListener()
        {
            socialMediaChecker="Instagram"
            setSocialLinks()
        }


        view.setWebsite.setOnClickListener()
        {
            socialMediaChecker="Website"
            setSocialLinks()
        }





        return view
    }








    private fun pickImage()
    {
        val intent= Intent()
        intent.type="image/*"                           //This will send the user to mobile phone gallery as all images are stored as "images/*" format
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==RequestCode && resultCode==Activity.RESULT_OK && data!!.data!=null)
        {
            imageUri=data.data
            Toast.makeText(context, "Uploading Image...", Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }

    }


    private fun uploadImageToDatabase()
    {
        //Telling the user what's happening
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait...")
        progressBar.show()

        //Since there are millions of users and their photos should not get mixed up xD
        //Therefore we need to assign each photo with a unique ID
        //To make it unique, we will get the time
        if(imageUri!=null)
        {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString()+".jpg")              //Store the image with the name for example "02:45:15-26-05-20.jpg"   //This way every image will have a unique name xD

            var uploadTask:StorageTask<*>
            uploadTask=fileRef.putFile(imageUri!!)              //Note we are uploading images into our newly created folder "User Images" in our "Storage" section of Firebase



            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {                                  //If uploading is unsuccessful, throw an exception
                            throw it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        val url= downloadUrl.toString()                        //Download the url that we have stored our uploaded image in
                        Log.i("url: ", url)

                        if(coverChecker=="coverPic")                            //If it is cover photo, then store it under "cover" tag in the "users" table in the Firebase database
                        {
                            val mapCoverImg=HashMap<String,Any>()
                            mapCoverImg["cover"]=url
                            usersReference!!.updateChildren(mapCoverImg)
                            coverChecker=""
                        }


                        else if(coverChecker=="profilePic")                   //If it is profile photo, then store it under "profile" tag in the "users" table in the Firebase database
                        {
                            val mapProfileImg=HashMap<String,Any>()
                            mapProfileImg["profile"]=url
                            usersReference!!.updateChildren(mapProfileImg)
                            coverChecker=""
                        }

                        progressBar.dismiss()

                    }


                }


        }

    }






    private fun setSocialLinks()
    {
        val builder:AlertDialog.Builder=AlertDialog.Builder(context!!, R.style.Theme_AppCompat_DayNight_Dialog_Alert)


        if(socialMediaChecker=="Website")
        {
            builder.setTitle("Enter Url:")
        }
        else
        {
            builder.setTitle("Enter username:")
        }



        val editText=EditText(context)


        if(socialMediaChecker=="Website")
        {
            editText.hint="e.g.: www.google.com"
        }
        else
        {
            editText.hint="e.g.: harshitkumar1234"
        }


        builder.setView(editText)


        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
            dialog, which ->
            val str=editText.text.toString()

            if(str=="")
            {
                Toast.makeText(context, "Please write something...", Toast.LENGTH_SHORT).show()
            }
            else
            {
                saveSocialLink(str)
            }
        })


        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                dialog, which ->
            dialog.cancel()
        })


        builder.show()


    }





    private fun saveSocialLink(str: String)
    {
        val mapSocial=HashMap<String,Any>()


        if(socialMediaChecker=="Facebook")
        {
            mapSocial["facebook"]="https://m.facebook.com/$str"
            usersReference!!.updateChildren(mapSocial)
            socialMediaChecker=""
        }

        else if(socialMediaChecker=="Instagram")
        {
            mapSocial["instagram"]="https://www.instagram.com/$str"
            usersReference!!.updateChildren(mapSocial)
            socialMediaChecker=""
        }

        else if(socialMediaChecker=="Website")
        {
            mapSocial["website"]="https://$str"
            usersReference!!.updateChildren(mapSocial)
            socialMediaChecker=""
        }


        usersReference!!.updateChildren(mapSocial).addOnCompleteListener{
            task ->
            if(task.isSuccessful)
            {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            }
        }


    }


}
