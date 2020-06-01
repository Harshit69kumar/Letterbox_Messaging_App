package com.example.letterbox.ModelClasses

import android.util.Log

class Chat
{
    private  var sender:String=""
    private  var receiver:String=""
    private  var url:String=""
    private  var message:String=""
    private  var isMessageSeen:String=""
    private  var currentTime:String=""
    private  var messageId:String=""


    constructor()


    constructor(
        sender: String,
        receiver: String,
        url: String,
        message: String,
        isMessageSeen: String,
        currentTime: String,
        messageId: String
    ) {
        this.sender = sender
        this.receiver = receiver
        this.url = url
        this.message = message
        this.isMessageSeen = isMessageSeen
        this.currentTime = currentTime
        this.messageId = messageId
    }


    fun getSender():String?
    {
        return sender
    }
    fun setSender(sender: String)
    {
        this.sender=sender
    }


    fun getReceiver():String?
    {
        return receiver
    }
    fun setReceiver(receiver: String)
    {
        this.receiver=receiver
    }



    fun getUrl():String?
    {
        return url
    }
    fun setUrl(url: String)
    {
        this.url=url
    }



    fun getMessage():String?
    {
        return message
    }
    fun setMessage(message: String)
    {
        this.message=message
    }


    fun getIsMessageSeen():String?
    {
        return isMessageSeen
    }
    fun setIsMessageSeen(isMessageSeen:String)
    {
        this.isMessageSeen=isMessageSeen
    }



    fun getCurrentTime():String?
    {
        return currentTime
    }
    fun setCurrentTime(currentTime: String)
    {
        this.currentTime=currentTime
    }


    fun getMessageId():String?
    {
        return messageId
    }
    fun setMessageId(messageId: String)
    {
        this.messageId=messageId
    }






}