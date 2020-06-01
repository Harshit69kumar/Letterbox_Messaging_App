package com.example.letterbox

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letterbox.AdapterClasses.ChatsAdapter
import com.example.letterbox.Fragments.APIService
import com.example.letterbox.ModelClasses.Chat
import com.example.letterbox.ModelClasses.Users
import com.example.letterbox.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageChatActivity : AppCompatActivity()
{
    var userIdVisit:String=""
    var firebaseUser:FirebaseUser?=null

    private val RequestCode=438

    var chatsAdapter:ChatsAdapter?=null
    var mChatList:List<Chat>?=null              //We are going to retrieve and save all the chats from Firebase into "mChats" ArrayList

    lateinit var recyclerViewChats: RecyclerView

    var seenListener:ValueEventListener?=null
    var reference: DatabaseReference?=null

    var notify=false
    var apiService: APIService?=null






    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)


        intent=intent
        userIdVisit=intent.getStringExtra("visitId69")          //This will get the "uid" of the "recipient" ie to the person whom we are going to message
        firebaseUser=FirebaseAuth.getInstance().currentUser            //This will get the sender's "uid" ie our currentUserId


        recyclerViewChats=findViewById(R.id.recyclerViewChats)
        recyclerViewChats.setHasFixedSize(true)
        var linearLayoutManager=LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd
        recyclerViewChats.layoutManager=linearLayoutManager



        val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbarMessageChat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener()
        {
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



        //"Client" class has an object named "Client" thats why "Client.Client" xD
        apiService=Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)




        //Updating the Username and ProfilePic on Action Bar/Toolbar
        //val reference=FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit)
        reference=FirebaseDatabase.getInstance().reference
            .child("users")

        Log.i("ref", reference.toString())

        reference!!.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children)                        //Traversing through all the users stored in "users" in Firebase
                {
                    val users: Users? = childSnapshot.getValue(Users::class.java)
                    //Log.i("users", users!!.toString())

                    //Log.i("usersName", users.getUsername()+"4")
                    //Log.i("usersProfile", users.getProfile()+"5")

                    if(users!!.getUid() == userIdVisit)                         //While traversing "users" in Firebase, if we recieve a match with that of our "userIdVisit" ie the receiver, then set the receiver's username and profile on toolbar
                    {
                        usernameMessageChat.text= users.getUsername()                          //Now "Username" will be displayed on ActionBar/Toolbar
                        Picasso.get().load(users.getProfile()).into(profileImageMessageChat)    //Now Profile Pic will be displayed on ActionBar/Toolbar

                        retrieveMessages(firebaseUser!!.uid, userIdVisit, users.getProfile())       //Retrieve all the messages of "currentUser" aka "Sender" with "userIdVisit" aka "Receiver" and also "receiver's profile photo"
                    }


                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }


        })






        //If the user presses the "Send Message Button"
        sendMessageButton.setOnClickListener()
        {
            notify=true

            val message=textMessage.text.toString()

            if (message=="")
            {
                Toast.makeText(this, "Please enter something first...", Toast.LENGTH_SHORT).show()
            }
            else
            {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)             //Passing "sender's uid", "recipient's uid" and "the message to be sent" as arguments to function
            }
            textMessage.setText("")
        }





        //If the user presses the "Send Image Button"
        attachImageFile.setOnClickListener()
        {
            notify=true

            val intent= Intent()
            intent.type="image/*"                           //This will send the user to mobile phone gallery as all images are stored as "images/*" format
            intent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), RequestCode)                 //438 is the "RequestCode" xD
        }



        //Now we assume if the "other user" ie "userIdVisit" has opened that particular chat, he or she must have "Seen" the message
        //Therefore we will call the method "seenMessage" passing "userIdVisit" as the argument
        seenMessage(userIdVisit)


    }





    //This function is basically creating a HashMap for each and every message and then subsequently uploading all HashMap to Firebase
    //Each message you send has its own HashMap, so if you are sending your bae 27 messages, then 27 times this function will be invoked, implying that 27 different HashMaps will be created for each of the 27 messages and HashMap will be uploaded individually
    private fun sendMessageToUser(senderUid: String, receiverUid: String?, message: String)
    {
        val reference=FirebaseDatabase.getInstance().reference
        val messageKey=reference.push().key

        val messageHashMap=HashMap<String,Any?>()       //Creating a hASHmAP
        messageHashMap["sender"]=senderUid
        messageHashMap["receiver"]=receiverUid
        messageHashMap["url"]=""
        messageHashMap["message"]=message
        messageHashMap["isMessageSeen"]="false"       //Default value is obviously false xD
        messageHashMap["currentTime"]= SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Date()).toString()
        messageHashMap["messageId"]=messageKey

        reference.child("Chats")       //Uploading the HashMap
            .child(messageKey!!)
            .setValue(messageHashMap)           //Okay so basically, first create a table named "Chats"   //Then using "messageId" as unique primary key, push/upload the HashMap "messageHashMap"
            .addOnCompleteListener(){task ->
                if(task.isSuccessful)
                {
                    //Adding the reciever to the sender's chat list
                    val chatsListSenderReference=FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)              //Unless we store both "sender" and "receiver" ID, then only we can display
                        .child(userIdVisit)
                    chatsListSenderReference.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot)
                        {
                            if(!p0.exists())
                            {
                                //Adding the reciever to the sender's chat list
                                chatsListSenderReference.child("id").setValue(userIdVisit)
                            }


                            //Adding sender to the receiver's chat list
                            val chatsListReceiverReference=FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)              //Unless we store both "sender" and "receiver" ID, then only we can display
                                .child(firebaseUser!!.uid)
                            //Adding sender to the receiver's chat list
                            chatsListReceiverReference.child("id").setValue(firebaseUser!!.uid)

                        }
                        override fun onCancelled(p0: DatabaseError)
                        {

                        }
                    })

                }
            }

        //If the task of uploading to FirebaseDatabase is successful, then
        //You have to do Notification work also
        //Implement thr notification using FCM (Firebase Cloud Messaging)
        val userReference=FirebaseDatabase.getInstance().reference
            .child("users").child(firebaseUser!!.uid)

        userReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                val user=p0.getValue(Users::class.java)
                if(notify)
                {
                    sendNotification(receiverUid, user!!.getUsername(), message)
                }
                notify=false
            }

            override fun onCancelled(p0: DatabaseError)
            {
                TODO("Not yet implemented")
            }
        })


    }




    //This is for uploading/attaching image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==RequestCode && resultCode==RESULT_OK && data!=null && data.data!=null)
        {
            //Telling the user what's happening
            val progressBar=ProgressDialog(this)
            progressBar.setMessage("Image is uploading, please wait...")
            progressBar.show()

            //Now storing/uploading the sent image in Firebase
            val fileUrl=data.data
            val storageReference=FirebaseStorage.getInstance().reference.child("Chat Images")           //Create another folder in "Storage' with the name "Chat Images"
            val ref=FirebaseDatabase.getInstance().reference
            val messageId=ref.push().key
            val filePath=storageReference.child("$messageId.jpg")           //Storing the name of the image that is being uploaded with a unique name

            val uploadTask:StorageTask<*>
            uploadTask=filePath.putFile(fileUrl!!)

            uploadTask.continueWithTask(Continuation<com.google.firebase.storage.UploadTask.TaskSnapshot, com.google.android.gms.tasks.Task<android.net.Uri>> { task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {                                  //If uploading is unsuccessful, throw an exception
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener{task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url= downloadUrl.toString()                        //Download the url that we have stored our uploaded image in
                    Log.i("url: ", url)

                    //Now make a HashMap and upload it in "Chats" folder
                    //Note the information about image can also be stored in "Chats" folder
                    val messageHashMap=HashMap<String,Any?>()       //Creating a hASHmAP
                    messageHashMap["sender"]=firebaseUser!!.uid
                    messageHashMap["receiver"]=userIdVisit
                    messageHashMap["url"]=url
                    messageHashMap["message"]="Sent you an image."
                    messageHashMap["isMessageSeen"]="false"       //Default value is obviously false xD
                    messageHashMap["currentTime"]=SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Date()).toString()
                    messageHashMap["messageId"]=messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener{task ->
                            if (task.isSuccessful)
                            {
                                progressBar.dismiss()           //That "ProgressBar" which shows "Image is being uploaded" will no longer be shown


                                //If the task of uploading to FirebaseDatabase is successful, then
                                //You have to do Notification work also
                                //Implement thr notification using FCM (Firebase Cloud Messaging)
                                val reference=FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)

                                reference.addValueEventListener(object :ValueEventListener{
                                    override fun onDataChange(p0: DataSnapshot)
                                    {
                                        val user=p0.getValue(Users::class.java)
                                        if(notify)
                                        {
                                            sendNotification(userIdVisit, user!!.getUsername(), "Sent you an image.")
                                        }
                                        notify=false
                                    }

                                    override fun onCancelled(p0: DatabaseError)
                                    {
                                        TODO("Not yet implemented")
                                    }
                                })

                            }
                        }


                }
            }
        }

    }



    private fun retrieveMessages(senderId:String, receiverId:String?, receiverImageUrl:String?)
    {
        mChatList=ArrayList()
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")

        //Log.i("reference", reference.toString())

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                (mChatList as ArrayList<Chat>).clear()
                //Log.i("mChatList", (mChatList as ArrayList<Chat>).toString())
                //Log.i("mChatList", (mChatList as ArrayList<Chat>).size.toString())

                for (snapshot in p0.children)
                {
                    val chat:Chat?=snapshot.getValue(Chat::class.java)

                    //There may be two types of messages
                    //One in which we are sender and they are receiver
                    //Other in which we are receiver and they are sender
                    //Regardless of who is sender, who is receiver, we will store in the same ArrayList "mChat"
                    //Later we will figure out who is Sender, who is Receiver in "ChatsAdapter" class
                    if((chat!!.getSender().equals(senderId) && chat.getReceiver().equals(receiverId))  ||  (chat.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)))
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    Log.i("mChatList", (mChatList as ArrayList<Chat>).toString())
                    Log.i("mChatList", (mChatList as ArrayList<Chat>).size.toString())
                    chatsAdapter= ChatsAdapter(this@MessageChatActivity, (mChatList as ArrayList<Chat>),  receiverImageUrl!!)
                    recyclerViewChats.adapter=chatsAdapter
                    recyclerViewChats.smoothScrollToPosition((mChatList as ArrayList<Chat>).size-1);        //Automatically scrolls Recycler View to the bottommost or the recentmost chat
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }




    private fun seenMessage(userId:String)
    {
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener=reference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                for(dataSnapshot in p0.children)
                {
                    val chat=dataSnapshot.getValue(Chat::class.java)

                    if(chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId))
                    {
                        val hashMap=HashMap<String, Any>()
                        hashMap["isMessageSeen"]="True"
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {
                TODO("Not yet implemented")
            }
        })
    }


    override fun onPause()          //This function is written at the end of Tutorial 12
    {
        super.onPause()

        reference!!.removeEventListener(seenListener!!)
    }



    private fun sendNotification(receiverId: String?, userName:String?, message: String)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val query=ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                for(dataSnapshot in p0.children)
                {
                    val token:Token?=dataSnapshot.getValue(Token::class.java)
                    val data= Data(firebaseUser!!.uid, R.mipmap.ic_launcher, "$userName: $message", "New Message", userIdVisit)             //Passing parameters or arguments to "Data.kt"
                    val sender= Sender(data, token!!.getToken().toString())                                                                           //Passing parameters or arguments to "Sender.kt"

                    apiService!!.sendNotification(sender)
                        .enqueue(object : retrofit2.Callback<MyResponse>{
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            )
                            {
                                if(response.code()==200)
                                {
                                    if (response.body()!!.success!==1)
                                    {
                                        Toast.makeText(this@MessageChatActivity, "Failed, Nothing happened", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable)
                            {
                                TODO("Not yet implemented")
                            }

                        })

                }
            }

            override fun onCancelled(p0: DatabaseError)
            {
                TODO("Not yet implemented")
            }
        })

    }




}
