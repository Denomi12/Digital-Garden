package si.um.feri.mobilegarden.utils

import android.util.Log
import com.google.gson.Gson
import si.um.feri.mobilegarden.models.Garden
import java.net.URL

object FetchGardens {

    interface GardensCallback {
        fun onSuccess(gardens: List<Garden>)
        fun onError(e: Exception)
    }

    fun getAllGardens(backendUrl: String, callback: GardensCallback) {
        Thread {
            try {
                val url = "$backendUrl/garden/"
                Log.d("FetchGardens", "Calling URL: $url")

                val response = URL(url).readText()
                Log.d("FetchGardens", "Response: $response")

                val gson = Gson()
                val listType = object : com.google.gson.reflect.TypeToken<List<Garden>>() {}.type
                val gardens: List<Garden> = gson.fromJson(response, listType)

                callback.onSuccess(gardens)

            } catch (e: Exception) {
                Log.e("FetchGardens", "ERROR fetching gardens", e)
                callback.onError(e)
            }
        }.start()
    }

}
