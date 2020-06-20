package com.paru.chatty.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.paru.chatty.Activity.MessageChatActivity
import com.paru.chatty.Activity.VisitUserProfileActivity
import com.paru.chatty.Activity.WelcomeActivity
import com.paru.chatty.ModelClasses.Chat
import com.paru.chatty.ModelClasses.Users
import com.paru.chatty.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main.view.profile_image
import kotlinx.android.synthetic.main.activity_main.view.user_name


class UserAdapter(
    mContext:Context,
    mUsers:List<Users>,
    isChatCheck:Boolean
):RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{
    private val mContext:Context
    private val mUsers:List<Users>
    private val isChatCheck:Boolean
    var lastMsg:String=""

    init {
        this.mUsers=mUsers
        this.mContext=mContext
        this.isChatCheck=isChatCheck
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View=LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user :Users=mUsers[position]

        holder.userNameTxt.text=user!!.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImageView)

        if(isChatCheck)
        {
            retrieveLastMessage(user.getUID(),holder.lastMessageTxt)
        }
        else
        {
            holder.lastMessageTxt.visibility=View.GONE
        }

        if (isChatCheck)
        {
            if (user.getStatus()== "online")
            {
                holder.onlineImageView.visibility=View.VISIBLE
                holder.offlineImageView.visibility=View.GONE
            }
            else
            {
                holder.onlineImageView.visibility=View.GONE
                holder.offlineImageView.visibility=View.VISIBLE
            }
        }
        else
        {
            holder.onlineImageView.visibility=View.GONE
            holder.offlineImageView.visibility=View.GONE
        }

        holder.profileImageView.setOnClickListener{
            val intent= Intent(mContext,
                VisitUserProfileActivity::class.java)
            intent.putExtra("visit_id",user.getUID())
            mContext.startActivity(intent)
        }

        holder.itemView.setOnClickListener{
            val intent= Intent(mContext,
                MessageChatActivity::class.java)
            intent.putExtra("visit_id",user.getUID())
            mContext.startActivity(intent)
        }

       /* holder.itemView.setOnClickListener{
            val options= arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder:AlertDialog.Builder=AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options,DialogInterface.OnClickListener{dialog, which ->  
                if(position == 0)
                {
                    val intent= Intent(mContext,
                        MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    mContext.startActivity(intent)
                }
                if(position == 1)
                {
                    val intent= Intent(mContext,
                        VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    mContext.startActivity(intent)
                }
            })
            builder.show()
        }
        */

    }


    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt:TextView
        var profileImageView:ImageView
        var onlineImageView:ImageView
        var offlineImageView:ImageView
        var lastMessageTxt:TextView

        init {
            userNameTxt=itemView.findViewById(R.id.username)
            profileImageView=itemView.findViewById(R.id.profile_image)
            onlineImageView=itemView.findViewById(R.id.image_online)
            offlineImageView=itemView.findViewById(R.id.image_offline)
            lastMessageTxt=itemView.findViewById(R.id.message_last)
        }
    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView) {

        lastMsg="defaultMsg"

        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children)
                {
                    val chat:Chat?=dataSnapshot.getValue(Chat::class.java)

                    if (firebaseUser!=null && chat!=null)
                    {
                        if (chat.getReceiver()==firebaseUser!!.uid && chat.getSender()==chatUserId||
                                chat.getReceiver()==chatUserId && chat.getSender()==firebaseUser!!.uid)
                        {
                            lastMsg=chat.getMessage()!!
                        }
                    }
                }
                when(lastMsg)
                {
                    "defaultMsg" -> lastMessageTxt.text="No Message"
                    "sent you an image." -> lastMessageTxt.text="image sent"
                    else -> lastMessageTxt.text=lastMsg
                }
                lastMsg="defaultMsg"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

}