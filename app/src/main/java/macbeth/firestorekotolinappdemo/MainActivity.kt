package macbeth.firestorekotolinappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var etName : EditText
    private lateinit var etAge : EditText
    private lateinit var etTitle : EditText
    private lateinit var etUserName : EditText
    private lateinit var lvUsers : ListView
    private lateinit var lvUsersAdapter : ArrayAdapter<User>
    private lateinit var db : FirebaseFirestore

    private val users = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize firebase
        // Requires google-services.json in the app folder
        // Requires gms classpath in project gradle file
        // Requires gms plugin and firestore libraries in module gradle file
        // See https://firebase.google.com/docs/firestore/quickstart for more details
        FirebaseApp.initializeApp(this)
        db = Firebase.firestore


        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etTitle = findViewById(R.id.etTitle)
        etUserName = findViewById(R.id.etUserName)
        lvUsers = findViewById(R.id.lvUsers)

        val bAdd = findViewById<Button>(R.id.bAdd)
        bAdd.setOnClickListener { addUser() }

        // Connect the listview to the "users" list (not yet populated)
        lvUsersAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
        lvUsers.adapter = lvUsersAdapter

        registerFirestoreUpdates()
    }

    private fun addUser() {
        // To add to database, create a User object first
        val user = User(
            etName.text.toString(),
            etAge.text.toString().toInt(),
            etTitle.text.toString())

        // Need to specify the document name.  The fields for the document
        // will all ome from the User object we just created.
        db.collection("users").document(etUserName.text.toString())
            .set(user)
            .addOnSuccessListener { Log.d("FirestoreKotlinAppDemo", "New user sent to firestore: $user") }
            .addOnFailureListener { error -> Log.d("FirestoreKotlinAppDemo", "Failed to send to firestore: $error") }
    }

    private fun registerFirestoreUpdates() {
        val ref = db.collection("users")
        // Register at the collection level which means I will receive everything when
        // something changes in the collection.  A little wasteful, but a good place
        // to start learning about how this works.
        ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("FirestoreKotlinAppDemo", "Error reading Firestore: $error")
                return@addSnapshotListener
            }
            if (snapshot == null) {
                Log.w("FirestoreKotlinAppDemo", "Snapshot was unexpectedly null.")
                return@addSnapshotListener
            }
            // Clear out old users list so we can replace it
            users.clear()
            for (document in snapshot) {
                val user = document.toObject<User>()
                Log.d("FirestoreKotlinAppDemo", "Document in snapshot: $user")
                users.add(user)
            }
            // Update the User Interface
            lvUsersAdapter.notifyDataSetChanged()
        }
    }
}