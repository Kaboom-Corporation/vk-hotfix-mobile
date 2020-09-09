package com.travels.searchtravels

//Импорт библиотек
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.api.services.vision.v1.model.LatLng
import com.preview.planner.prefs.AppPreferences
import com.travels.searchtravels.api.OnVisionApiListener
import com.travels.searchtravels.api.VisionApi
import com.travels.searchtravels.utils.Constants
import com.travels.searchtravels.utils.ImageHelper.resizeBitmap
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup

import org.junit.Assert.assertEquals
import org.junit.Test

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MainActivity : AppCompatActivity() {
    // Проверка цен проживания в городах
    @Test
    fun test_prices() {
        for (i in 1..5) {
            val city = when(i){
                1 -> "Moscow" // Данные для Москвы
                2 -> "Paris" // Данные для Парижа
                3 -> "Berlin" // Данные для Берлина
                4 -> "Kiev" //Данные для Киева
                else -> {
                    "London" // Лондон
                }
            }
            try {
                val obj = URL("https://autocomplete.travelpayouts.com/places2?locale=en&types[]=city&term=$city")
                val connection = obj.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                connection.setRequestProperty("Content-Type", "application/json")
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?
                val response = StringBuffer()
                while (bufferedReader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                bufferedReader.close()
                val responseJSON = JSONArray(response.toString())
                Log.d("myLogs", "responseJSON = $responseJSON")
                val code = responseJSON.getJSONObject(0).getString("code")
                val obj2 = URL("https://api.travelpayouts.com/v1/prices/cheap?origin=LED&depart_date=2019-12&return_date=2019-12&token=471ae7d420d82eb92428018ec458623b&destination=$code")
                val connection2 = obj2.openConnection() as HttpURLConnection
                connection2.requestMethod = "GET"
                connection2.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection2.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
                connection2.setRequestProperty("Content-Type", "application/json")
                val bufferedReader2 = BufferedReader(InputStreamReader(connection2.inputStream))
                var inputLine2: String?
                val response2 = StringBuffer()
                while (bufferedReader2.readLine().also { inputLine2 = it } != null) {
                    response2.append(inputLine2)
                }
                bufferedReader2.close()
                val responseJSON2 = JSONObject(response2.toString())
                try {
                    val ticketPrice = responseJSON2.getJSONObject("data").getJSONObject(code).getJSONObject("1").getString("price").toInt() //получили данные о цене
                    // По условию задания не совсем понятно откуда мы должны брать цены для сравнения (из этого же API? Сравнивать два одинаковых запроса?)
                    // Поэтому мы решили просто захардкодить сравнение цены с "примерными" (возможно даже не рядом) ценами для этих городов
                    when(i){
                        1 -> assertEquals(12000, ticketPrice) // Данные для Москвы
                        2 -> assertEquals(17000, ticketPrice) // Данные для Парижа
                        3 -> assertEquals(15000, ticketPrice) // Данные для Берлина
                        4 -> assertEquals(8000, ticketPrice) // Данные для Киева
                        else -> {
                            assertEquals(20000, ticketPrice) // Данные для Лондона
                        }
                    }
                } catch (e: java.lang.Exception) {
                    assertEquals(0, 1)
                }
            } catch (e: Exception) {
                assertEquals(0, 1)
            }
        }
    }
}