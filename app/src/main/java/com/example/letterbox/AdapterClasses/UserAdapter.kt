package com.example.letterbox.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.letterbox.MessageChatActivity
import com.example.letterbox.ModelClasses.Users
import com.example.letterbox.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.profilePic
import kotlinx.android.synthetic.main.user_search_item_layout.view.username

class UserAdapter(
    mContext:Context,
    mUsers:List<Users>,
    isChatCheck:Boolean
    ): RecyclerView.Adapter<UserAdapter.ViewHolder?>()                      //all this is equivalent to "extends" and "implements" used after class in Java in Kotlin   //We are receiving these values from other classes and then storing in our global variables

{
    private val mContext:Context
    private val mUsers:List<Users>
    private var isChatCheck:Boolean

    init
    {
        this.mContext=mContext
        this.mUsers=mUsers
        this.isChatCheck=isChatCheck
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        //Now we have to attach this "UserAdapter.kt" to "user_search_item_layout.xml"
        val view:View=LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)                                 //passing "view" as a parameter to "Class ViewHolder" which is a nested class inside "UserAdapter.kt"
    }

    override fun getItemCount(): Int
    {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        //Now we have to display that particular element stored inside the "List mUsers"
        val user: Users?=mUsers[position]
        holder.userNameText.text=user!!.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImageView)



        holder.itemView.setOnClickListener()
        {
            val options= arrayOf<CharSequence>("Send Message", "Visit Profile")

            val builder:AlertDialog.Builder=AlertDialog.Builder(mContext)
            builder.setTitle("What do you want to do?")
            builder.setItems(options, DialogInterface.OnClickListener(){dialog, position ->

                if (position==0)                //Direct the user to MessageChatActivity
                {
                    val intent= Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visitId69", user.getUid())
                    mContext.startActivity(intent)
                }

                if(position==1)                 //Direct the user to view profile
                {

                }

            })
            builder.show()


        }




    }









    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var userNameText: TextView
        var profileImageView: CircleImageView
        var onlineImageView: CircleImageView
        var offlineImageView: CircleImageView
        var lastMessageText: TextView

        init                //Constructor to initialise the values
        {
            userNameText=itemView.findViewById(R.id.username)
            profileImageView=itemView.findViewById(R.id.profilePic)
            onlineImageView=itemView.findViewById(R.id.image_online)
            offlineImageView=itemView.findViewById(R.id.image_offline)
            lastMessageText=itemView.findViewById(R.id.lastMessage)
        }

    }




}
