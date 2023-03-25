package br.com.liercesantos.infkotlin.models

import com.google.firebase.firestore.DocumentSnapshot

data class ProfileModel(
  var uid: String = "",
  var name: String = "",
  var email: String = ""
) {
  fun toMap(): Map<String, Any?> {
    return mapOf(
      "name" to name,
      "email" to email
    )
  }

  companion object {
    fun fromSnapshot(snapshot: DocumentSnapshot): ProfileModel {
      val data = snapshot.data!!
      return ProfileModel(
        uid = data["uid"] as String,
        name = data["name"] as String,
        email = data["email"] as String
      )
    }
  }
}
