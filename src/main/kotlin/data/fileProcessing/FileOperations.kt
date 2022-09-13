package fileProcessing

import java.io.File

class FileOperations {

    fun write(fileName: String, data: String) {
        val writer = File(fileName).bufferedWriter()
        try {
            writer.write(data)
            println("Write to $fileName")
        } finally {
            writer.close()
        }
    }

    fun read(fileName: String): String {
        val reader = File(fileName).bufferedReader()
        var text = ""
        try {
            text = reader.readText()
            println("Read from $fileName")
        } finally {
            reader.close()
        }
        return text
    }
}