package com.example.zertte.network.Firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.zertte.model.CartItem
import com.example.zertte.model.Place
import com.example.zertte.model.User
import com.example.zertte.ui.Fragments.FragmentMain
import com.example.zertte.ui.activities.PlaceDetailsActivity
import com.example.zertte.ui.activities.user.ActivityLogin
import com.example.zertte.ui.activities.user.ActivitySignIn
import com.example.zertte.ui.activities.user.CartListActivity
import com.example.zertte.ui.activities.user.SettingsActivity
import com.example.zertte.ui.activities.user.UserProfileActivity
import com.example.zertte.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: ActivitySignIn, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.userRegistrationSuccess()
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

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.ZERTTE_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is ActivityLogin -> {
                        activity.userLoggedInSuccess(user)
                    }

                    is SettingsActivity ->{
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is ActivityLogin -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
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

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {

                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
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

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                when (activity) {
                    is UserProfileActivity -> {
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

    fun removeItemFromCart(context: Context, cart_id: String){
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {
                when(context){
                    is CartListActivity->{
                        context.itemRemoveSuccess()
                    }
                }
            }
            .addOnFailureListener {e->
                when(context){
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from cart list",
                    e
                )

            }
    }

    fun getAllPlacesList(activity: CartListActivity){
        mFireStore.collection(Constants.PLACES)
            .get()
            .addOnSuccessListener { document ->
                activity.hideProgressDialog()
                Log.e("Place list", document.documents.toString())
                val placesList: ArrayList<Place> = ArrayList()

                for(i in document.documents){
                    val place = i.toObject(Place::class.java)
                    place!!.place_id = i.id

                    placesList.add(place)
                }

                activity.successPlacesListFromFireStore(placesList)
            }.addOnFailureListener {e->
                activity.hideProgressDialog()

                Log.e("Get Place List", "Error while getting all place list", e)
            }
    }

    fun getMainItemsList(fragment: FragmentMain){
        mFireStore.collection(Constants.PLACES)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val placesList: ArrayList<Place> = ArrayList()

                for(i in document.documents){

                    val place = i.toObject(Place::class.java)!!
                    place.place_id=i.id
                    placesList.add(place)
                }

                fragment.successMainItemsList(placesList)
            }
            .addOnFailureListener{
                e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting main items list.", e)
            }
    }

    fun getCartList(activity: Activity){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.GUIDE_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener {document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<CartItem> = ArrayList()

                for(i in document.documents){

                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when(activity){
                    is CartListActivity->{
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener {
                 e->
                when(activity){
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting the cart items", e)
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHaspMap: HashMap<String, Any>){
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHaspMap)
            .addOnSuccessListener {

                when(context){
                        is CartListActivity -> {
                            context.itemUpdateSuccess()
                        }
                }

            }
            .addOnFailureListener { e->
                when(context){
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(context.javaClass.simpleName, "Error while updating the cart item", e)
            }
    }

    fun checkIfItemExistInCart(activity: PlaceDetailsActivity, placeId: String){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.GUIDE_ID, getCurrentUserID())
            .whereEqualTo(Constants.PLACE_ID, placeId)
            .get()
            .addOnSuccessListener { document->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                if(document.documents.size>0){
                    activity.placeExistsInCart()
                }else{
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener{e->

                activity.hideProgressDialog()

            }
    }

    fun addCartItems(activity: PlaceDetailsActivity, addToCart: CartItem){
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener{e->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item",
                    e
                )
            }
    }
}