package br.com.liercesantos.infkotlin.manager

import android.app.Application
import android.content.Context
import br.com.liercesantos.infkotlin.models.ProfileModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileManager : Application() {
  companion object {
    fun fromStorage(context: Context): ProfileModel? {
      val sharedPref = context.getSharedPreferences("profile", Context.MODE_PRIVATE)
      val uid = sharedPref.getString("uid", null)
      val name = sharedPref.getString("name", null)
      val email = sharedPref.getString("email", null)

      return if(uid != null && name != null && email != null){
        ProfileModel(uid, name, email)
      } else null
    }

    fun toStorage(uid: String, name: String, email: String, context: Context) {
      val sharedPref = context.getSharedPreferences("profile", Context.MODE_PRIVATE)
      with (sharedPref.edit()) {
        putString("uid", uid)
        putString("name", name)
        putString("email", email)
        apply()
      }
    }

    fun deleteFromStorage(context: Context) {
      val sharedPref = context.getSharedPreferences("profile", Context.MODE_PRIVATE)
      val editor = sharedPref.edit()
      editor.clear()
      editor.apply()
    }

    suspend fun syncToStorage(uid: String, context: Context){
      val profile = fromFirestore(uid)
      if(profile != null){
        toStorage(profile.uid, profile.name, profile.email, context)
      }
    }

    suspend fun fromFirestore(uid: String): ProfileModel? {
      val db = FirebaseFirestore.getInstance()
      val collection = db.collection("profiles")
      val document = collection.document(uid)
      val snapshot = document.get().await()

      return if (snapshot.exists()) {
        ProfileModel.fromSnapshot(snapshot)
      } else null
    }

    suspend fun toFirestore(profile: ProfileModel){
      val db = FirebaseFirestore.getInstance()
      val collection = db.collection("profiles")
      val document = collection.document(profile.uid)
      document.set(profile.toMap()).await()
    }

    suspend fun deleteFromFirestore(uid: String) {
      val db = FirebaseFirestore.getInstance()
      val collection = db.collection("profiles")
      val docRef = collection.document(uid)
      docRef.delete().await()
    }

    suspend fun syncToFirestore(context: Context){
      val profile = fromStorage(context)
      if(profile != null){
        toFirestore(profile)
      }
    }
  }
}