package com.paru.chatty.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.storage.StorageReference
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.gson.internal.`$Gson$Preconditions`
import com.paru.chatty.ModelClasses.Users

import com.paru.chatty.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    var userReference:DatabaseReference?=null
    var firebaseUser: FirebaseUser?=null
    private var RequestCode=438
    private var imageUri: Uri?=null
    private var storageRef:StorageReference?=null
    var coverChecker:String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser= FirebaseAuth.getInstance().currentUser
        userReference=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        storageRef=FirebaseStorage.getInstance().reference.child("User Images")


        userReference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists())
                {
                    val user:Users?=p0.getValue(Users::class.java)

                   if (context!=null)
                   {
                       view.username_settings.text=user!!.getUserName()
                       Picasso.get().load(user.getProfile()).into(view.profile_image_settings)
                       Picasso.get().load(user.getCover()).into(view.cover_image_settings)
                   }
                }
            }

            override fun onCancelled(p0: DatabaseError) {


            }
        })

        view.profile_image_settings.setOnClickListener{
            pickImage()
        }

        view.cover_image_settings.setOnClickListener{
            coverChecker="cover"
            pickImage()
        }

        return view
    }

    private fun pickImage() {
        val intent=Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==RequestCode &&  resultCode==Activity.RESULT_OK && data!!.data!=null)
        {
            imageUri=data.data
            Toast.makeText(context,"uploading...",Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait...")
        progressBar.show()

        if (imageUri!=null)
        {
            val fileRef=storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask=fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                        }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    val downloadUrl= task.result
                    val url=downloadUrl.toString()

                    if(coverChecker=="cover")
                    {
                        val mapCoverImg=HashMap<String,Any>()
                        mapCoverImg["cover"] =url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker=""
                    }
                    else
                    {
                        val mapProfileImg=HashMap<String,Any>()
                        mapProfileImg["profile"] =url
                        userReference!!.updateChildren(mapProfileImg)
                    }
                    progressBar.dismiss()
                }

            }
        }
    }

}
