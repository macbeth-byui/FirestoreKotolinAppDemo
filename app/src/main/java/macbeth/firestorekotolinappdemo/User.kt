package macbeth.firestorekotolinappdemo

// All fields are public and there are no functions
// If the document from firestore is missing one of these fields (that would be a mistake)
// then we will allow null.
data class User (val name : String? = null,
                 val age : Int? = null,
                 val title : String? = null)