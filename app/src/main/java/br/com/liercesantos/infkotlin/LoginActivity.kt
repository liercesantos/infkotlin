package br.com.liercesantos.infkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import br.com.liercesantos.infkotlin.manager.ProfileManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
  private val RC_SIGN_IN = 9001
  private val firebaseAuth = FirebaseAuth.getInstance()
  lateinit var inputEmail: TextView
  lateinit var inputPassword: TextView
  lateinit var linkCreateAccount: TextView
  lateinit var btnLogin: Button
  lateinit var btnGoogleLogin: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    supportActionBar?.hide()

    inputEmail = findViewById(R.id.input_email)
    inputPassword = findViewById(R.id.input_password)
    linkCreateAccount = findViewById(R.id.link_create_account)
    btnLogin = findViewById(R.id.button_login)
    btnGoogleLogin = findViewById(R.id.button_google_login)

    linkCreateAccount.setOnClickListener{
      val intent = Intent(this, RegisterActivity::class.java)
      startActivity(intent)
      finish()
    }

    setupAppLogin();
    setupGoogleLogin();
  }

  override fun onStart() {
    super.onStart()

    if(firebaseAuth.currentUser != null) {
      startActivity(Intent(this, MainActivity::class.java))
      finish()
    }
  }

  @Deprecated("Deprecated in Java")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if(requestCode == RC_SIGN_IN){
      val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
      try {
        val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
        if (account != null) {
          val credential = GoogleAuthProvider.getCredential(account.idToken, null)
          googleSignin(credential)
        }
      }
      catch (e: ApiException){
        makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun setupAppLogin(){
    btnLogin.setOnClickListener {
      val email = inputEmail.text.toString()
      val password = inputPassword.text.toString()

      firebaseAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
          if (task.isSuccessful) {
            val user = firebaseAuth.currentUser
            if (user != null) {
              ProfileManager.toStorage(
                user.uid,
                user.displayName.toString(),
                user.email.toString(),
                applicationContext
              )
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
          }
          else {
            val exception = task.exception

            if (exception is FirebaseAuthInvalidUserException) {
              Toast.makeText(this, "Usuário não existe", Toast.LENGTH_SHORT).show()
            } else {
              Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
            }
          }
        }
    }
  }

  private fun setupGoogleLogin() {
    val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso)

    btnGoogleLogin.setOnClickListener {
      val signInIntent: Intent = googleSignInClient.signInIntent
      startActivityForResult(signInIntent,RC_SIGN_IN)
    }
  }

  private fun googleSignin(credential: AuthCredential){
    firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task->
      if(task.isSuccessful) {
        val user = firebaseAuth.currentUser
        if (user != null) {
          ProfileManager.toStorage(
            user.uid,
            user.displayName.toString(),
            user.email.toString(),
            applicationContext
          )
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
      }
    }
  }
}