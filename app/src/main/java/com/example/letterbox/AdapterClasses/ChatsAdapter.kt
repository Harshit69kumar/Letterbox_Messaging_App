package com.example.letterbox.AdapterClasses

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.letterbox.ModelClasses.Chat
import com.example.letterbox.ModelClasses.Users
import com.example.letterbox.R
//import com.example.letterbox.databinding.MessageItemLeftBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*
import org.w3c.dom.Text

class ChatsAdapter(
    mContext:Context,
    mChatList: List<Chat>,
    imageUrl:String
):RecyclerView.Adapter<ChatsAdapter.ViewHolder>()
{
    private val mContext: Context
    private val mChatList: List<Chat>
    private  val imageUrl: String
    var firebaseUser:FirebaseUser=FirebaseAuth.getInstance().currentUser!!

    init
    {
        this.mContext=mContext
        this.mChatList=mChatList
        this.imageUrl=imageUrl
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder
    {
        //On Position 0, we have "message_item_right"
        //On Position 1, we have "message_item_left"
        return if(position==0)
        {
            Log.i("position", position.toString())
            val view:View= LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        }
        else
        {
            Log.i("position", position.toString())
            val view:View= LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }

    }

    override fun getItemCount(): Int
    {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ChatsAdapter.ViewHolder, position: Int)
    {
        val chat:Chat = mChatList[position]         //Let's say if mChat[position]="I love you baby" then "I love baby" will be stored in "chat" variable

        Log.i("imageUrl", imageUrl)

        //Setting the receiver's image
        //Picasso.get().load(imageUrl).into(holder.profileImageLeft)

        //If you go to "MessageChatActivity" or "Chats" section under Firebase
        //You will see a major difference between text messages and image mesages
        //For text message, messageHashMap["url"]="" && messageHashMap["message"]=message
        //For image files, messageHashMap["url"]=url && messageHashMap["message"]="Sent you an image."
        //We will utilise this difference in messageHashMap[url] && messageHashMap[message] to differentiate between a text message and an image file

        //For Image Messages
        if(chat.getMessage().equals("Sent you an image.") && !chat.getUrl().equals(""))
        {
            //Now if it's sender && we are displaying image and not text
            if(chat.getSender().equals(firebaseUser.uid))
            {
                holder.showTextMessage!!.visibility=View.GONE
                holder.rightImageView!!.visibility=View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.rightImageView)
            }

            //Now if it's receiver && we are displaying image and not text
            else if(!chat.getSender().equals(firebaseUser.uid))
            {
                holder.showTextMessage!!.visibility=View.GONE
                holder.leftImageView!!.visibility=View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.leftImageView)
            }

        }




        //For Text Messages
        else {
            holder.showTextMessage!!.text = chat.getMessage()
        }


        //holder.currentTimeLeft!!.text="Message sent at "+chat.getCurrentTime()
        holder.currentTime!!.text="Message sent at "+chat.getCurrentTime()

        //Now we have to display whether its "Sent" or "Seen"
        //We have to display "Sent" or "Seen" after the last message and not after every message
        //Therefore
        if(position==mChatList.size-1)
        {
            if(chat.getIsMessageSeen().equals("true"))              //If the chat is seen
            {
                holder.textSeen!!.text="Seen"

                if(chat.getMessage().equals("Sent you an image.") && !chat.getUrl().equals(""))
                {
                    val lp: RelativeLayout.LayoutParams?=holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)     //Incase we have an image, set the "Seen" as this
                    holder.textSeen!!.layoutParams=lp
                }
            }

            else if(chat.getIsMessageSeen().equals("false"))               //If the chat is yet not seen
            {
                holder.textSeen!!.text="Sent"

                if(chat.getMessage().equals("Sent you an image.") && !chat.getUrl().equals(""))
                {
                    val lp: RelativeLayout.LayoutParams?=holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)     //Incase we have an image, set the "Seen" as this
                    holder.textSeen!!.layoutParams=lp
                }
            }

        }
        else
        {
            //holder.textSeenRight!!.visibility=View.GONE
        }




    }



    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var profileImageLeft: CircleImageView?=null
        var profileImageRight: CircleImageView?=null
        var showTextMessage: TextView?=null
        //var showTextMessageRight: TextView?=null
        var leftImageView: ImageView?=null
        var rightImageView: ImageView?=null
        var currentTime: TextView?=null
        //var currentTimeRight: TextView?=null
        var textSeen: TextView?=null
        //var textSeenRight: TextView?=null



        init
        {
            profileImageLeft=itemView.findViewById(R.id.profileImageLeft)
            profileImageRight=itemView.findViewById(R.id.profileImageRight)
            showTextMessage=itemView.findViewById<TextView>(R.id.showTextMessage)
            //showTextMessageRight=itemView.findViewById(R.id.showTextMessageLeft)
            leftImageView=itemView.findViewById(R.id.leftImageView)
            rightImageView=itemView.findViewById(R.id.rightImageView)
            currentTime=itemView.findViewById(R.id.currentTime)
            //currentTimeRight=itemView.findViewById(R.id.currentTimeRight)
            textSeen=itemView.findViewById(R.id.textSeen)
            //textSeenRight=itemView.findViewById(R.id.textSeenRight)
        }

    }



    /*inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        fun bind(item: )
        {
            with(itemView)
            {
                show
            }
        }
    }*/




    override fun getItemViewType(position: Int): Int
    {
        //firebaseUser=FirebaseAuth.getInstance().currentUser         //Getting the cuurent user aka "sender"

        return if(mChatList[position].getSender().equals(firebaseUser.uid))
        {
            //Lets'say mChat[position]="I love you baby" and we don't know whether its being sent or received
            //If that particular chat "I love you baby", when invoked getSender() returns the same UID as the current Firebase User
            //That means "I love you baby" is sent by the sender
            //That means it has to be on right side
            //Therefore return 0 to "override fun onCreateViewHolder()" so that message_chat_right is activated
            0
        }
        else
        {
            //Lets'say mChat[position]="I love you baby" and we don't know whether its being sent or received
            //If that particular chat "I love you baby", when invoked getSender() returns a different UID as the current Firebase User
            //That means "I love you baby" is not sent by the sender
            //It is being received by the receiver
            //That means it has to be on left side
            //Therefore return 1  to "override fun onCreateViewHolder()" so that message_chat_left is activated
            1
        }

    }





}