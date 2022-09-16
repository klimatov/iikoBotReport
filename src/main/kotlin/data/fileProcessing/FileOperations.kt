package fileProcessing

import java.io.File
import utils.Logging

class FileOperations {
    private val tag = this::class.java.simpleName
    fun write(fileName: String, data: String) {
        val writer = File(fileName).bufferedWriter()
        try {
            writer.write(data)
            Logging.i(tag,"Write to $fileName")
        } finally {
            writer.close()
        }
    }

    fun read(fileName: String): String {
        val reader = File(fileName).bufferedReader()
        var text = ""
        try {
            text = reader.readText()
            Logging.i(tag,"Read from $fileName")
        } finally {
            reader.close()
        }
        return text
    }
}