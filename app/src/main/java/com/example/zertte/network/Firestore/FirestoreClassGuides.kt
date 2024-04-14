package com.example.zertte.ui.activities.guide

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.zertte.model.Guide
import com.example.zertte.model.Place
import com.example.zertte.ui.Fragments.FragmentPlacesGuide
import com.example.zertte.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClassGuides {

    // Access a Cloud Firestore instance.
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerGuide(activity: ActivitySignInGuide, guideInfo: Guide) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.GUIDES)
            // Document ID for users fields. Here the document it is the User ID.
            .document(guideInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(guideInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.guideRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentGuideID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentGuide = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentGuideID = ""
        if (currentGuide != null) {
            currentGuideID = currentGuide.uid
        }

        return currentGuideID
    }

    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getGuideDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.GUIDES)
            // The document id to get the Fields of user.
            .document(getCurrentGuideID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val guide = document.toObject(Guide::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.ZERTTE_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // Create an instance of the editor which is help us to edit the SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_GUIDENAME,
                    "${guide.firstName} ${guide.lastName}"
                )
                editor.apply()

                when (activity) {
                    is ActivityLoginGuide -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.guideLoggedInSuccess(guide)
                    }

                    is SettingsActivityGuide ->{
                        // Call a function of base activity for transferring the result to it.
                        activity.guideDetailsSuccess(guide)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is ActivityLoginGuide -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivityGuide -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }


    /**
     * A function to update the user profile data into the database.
     *
     * @param activity The activity is used for identifying the Base activity to which the result is passed.
     * @param userHashMap HashMap of fields which are to be updated.
     */
    fun updateGuideProfileData(activity: Activity, guideHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.GUIDES)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentGuideID())
            // A HashMap of fields which are to be updated.
            .update(guideHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is GuideProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.guideProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is GuideProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    // A function to upload the image to the cloud storage.
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is GuideProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddPlaceActivity ->{
                                activity.imageUploadSuccess(uri.toString())

                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is GuideProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddPlaceActivity -> {
                        activity.hideProgressDialog()
                    }

                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadPlaceDetails(activity: AddPlaceActivity, placeInfo: Place){
        mFireStore.collection(Constants.PLACES)
            .document()
            .set(placeInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.placeUploadSuccess()
            }
            .addOnFailureListener{e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    fun getPlacesList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PLACES)
            .whereEqualTo(Constants.GUIDE_ID, getCurrentGuideID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val placesList: ArrayList<Place> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val place = i.toObject(Place::class.java)
                    place!!.place_id = i.id

                    placesList.add(place)
                }

                when (fragment) {
                    is FragmentPlacesGuide -> {
                        fragment.successPlacesListFromFireStore(placesList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is FragmentPlacesGuide -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e("Get Product List", "Error while getting product list.", e)
            }
    }
}