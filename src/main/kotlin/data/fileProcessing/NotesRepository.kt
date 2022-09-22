package data.fileProcessing

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotesRepository {
    private val tag = this::class.java.simpleName
    fun get(): String {
        val serializedData = FileOperations().read("notes.txt")
        val type = object : TypeToken<String>() {}.type
        var notes =
            Gson().fromJson<String>(serializedData, type)
        if (notes == null) notes = ""
        return notes
    }

    fun set(notes: String?) {
        if (notes != null) {
            val serializedData = Gson().toJson(notes)
            FileOperations().write("notes.txt", serializedData)
        }
    }

}