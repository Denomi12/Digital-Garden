package si.um.feri.mobilegarden.fileUtils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import si.um.feri.mobilegarden.models.ExtremeEvent
import java.io.File

object EventFileUtils {
    private val gson = Gson()

    fun loadEvents(
        context: Context,
        fileName: String
    ): MutableList<ExtremeEvent> {

        val file = File(context.filesDir, fileName)
        if (!file.exists()) return mutableListOf()

        return try {
            val content = file.readText()
            val listType = object : TypeToken<MutableList<ExtremeEvent>>() {}.type
            gson.fromJson(content, listType) ?: mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }
}
