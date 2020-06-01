package com.example.letterbox.ModelClasses

class Users
{
    //Now we have to initialise each and every child from our "users"
    //We have to use same name of variables as children in our database.
    //You can't use "id" for "uid". You HAVE to use "uid" itself (pretty much like JSON xD)

    private var uid:String=""
    private var username:String=""
    private var profile:String=""
    private var cover:String=""
    private var status:String=""
    private var search:String=""
    private var facebook:String=""
    private var instagram:String=""
    private var website:String=""

    constructor()               //null constructor


    constructor(               //secondary constructor to initialise
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }

    fun getUid():String?
    {
        return  uid
    }

    fun setUid(uid:String)
    {
        this.uid=uid
    }

    fun getUsername():String?
    {
        return  username
    }

    fun setUsername(username:String)
    {
        this.username=username
    }

    fun getProfile():String?
    {
        return  profile
    }

    fun setProfile(profile: String)
    {
        this.profile=profile
    }

    fun getCover():String?
    {
        return  cover
    }

    fun setCover(cover: String)
    {
        this.cover=cover
    }

    fun getStatus():String?
    {
        return status
    }

    fun setStatus(status:String)
    {
        this.status=status
    }

    fun getSearch():String?
    {
        return search
    }

    fun setSearch(search: String)
    {
        this.search=search
    }

    fun getFacebook():String?
    {
        return  facebook
    }

    fun setFacebook(facebook:String)
    {
        this.facebook=facebook
    }

    fun getInstagram():String?
    {
        return instagram
    }

    fun setInstagram(instagram:String)
    {
        this.instagram=instagram
    }

    fun getWebsite():String?
    {
        return  website
    }

    fun setWebsite(website: String)
    {
        this.website=website
    }



}