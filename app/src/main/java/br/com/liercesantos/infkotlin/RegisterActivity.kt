package br.com.liercesantos.infkotlin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.liercesantos.infkotlin.manager.ProfileManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.runBlocking


class RegisterActivity : AppCompatActivity() {
  private val firebaseAuth = FirebaseAuth.getInstance()
  lateinit var inputName: TextView
  lateinit var inputEmail: TextView
  lateinit var inputPassword: TextView
  lateinit var inputConfirmPassword: TextView
  lateinit var btnRegister: Button
  lateinit var linkBack: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_register)
    supportActionBar?.hide()

    inputName = findViewById(R.id.input_name)
    inputEmail = findViewById(R.id.input_email)
    inputPassword = findViewById(R.id.input_password)
    inputConfirmPassword = findViewById(R.id.input_confirm_password)

    goToLogin()
    signup()
  }

  private fun signup() {
    btnRegister = findViewById(R.id.button_register)
    btnRegister.setOnClickListener {
      if (validateForm()) {
        val name = inputName.text.toString()
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
              val user = firebaseAuth.currentUser
              if (user != null) {
                ProfileManager.toStorage(
                  user.uid,
                  name,
                  email,
                  applicationContext
                )

                runBlocking {
                  ProfileManager.syncToFirestore(applicationContext)
                }
              }

              finish()
              val intent = Intent(this, MainActivity::class.java)
              startActivity(intent)
            }
            else {
              val exception = task.exception
              if (exception is FirebaseAuthUserCollisionException) {
                Toast.makeText(this, "Usuário já cadastrado.", Toast.LENGTH_SHORT).show()
              } else {
                Toast.makeText(this, "Erro ao salvar os dados. Por favor, tente novamente mais tarde.", Toast.LENGTH_SHORT).show()
              }
            }
          }
      }
    }
  }

  private fun updateFirebaseProfileName(user: FirebaseUser, name: String) {
    val firebaseProfileUpdate = UserProfileChangeRequest.Builder()
      .setDisplayName(name)
      .build()
    user.updateProfile(firebaseProfileUpdate)
      .addOnCompleteListener(this){ task ->
        if (task.isSuccessful) {
          Log.d(TAG, "Nome do usuário alterado.")
        }
        else {
          Log.d(TAG, "Erro ao alterar o nome do usuário.")
        }
      }
  }

  private fun goToLogin() {
    linkBack = findViewById(R.id.link_back)
    linkBack.setOnClickListener{
      val intent = Intent(this, LoginActivity::class.java)
      startActivity(intent)
      finish()
    }
  }

  private fun validateForm(): Boolean {
    var valid = true

    val name = inputName.text.toString()
    if (name.length !in 5..50) {
      inputName.error = "O nome deve ter entre 5 e 50 caracteres"
      valid = false
    }

    val email = inputEmail.text.toString()
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      inputEmail.error = "Insira um email válido"
      valid = false
    }

    val password = inputPassword.text.toString()
    val confirmPassword = inputConfirmPassword.text.toString()
    if (password.length !in 8..25) {
      inputPassword.error = "A senha deve ter entre 8 e 25 caracteres"
      valid = false
    }
    else if (password != confirmPassword) {
      inputConfirmPassword.error = "As senhas não coincidem"
      valid = false
    }

    return valid
  }
}