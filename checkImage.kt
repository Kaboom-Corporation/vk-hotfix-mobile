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
    //Проверка категорий картиночек (море, океан, пляж и т.д.)
    @Test
    fun categories_check() {
        for (i in 1..6) {
            var linkS = ""
            linkS = when (i) {
                1 -> "https://telegra.ph/file/9bec243391bb3d1d97c2b.jpg" // Море
                2 -> "https://mir-tourista.ru/wp-content/uploads/2018/09/tihiy_ocean-_2-1024x684.jpg" // Океан
                3 -> "https://img2.goodfon.ru/original/3000x2003/3/43/more-plyazh-pesok-palmy.jpg" // Пляж
                4 -> "https://moscow-oblast.sm-news.ru/wp-content/uploads/2020/06/16/h-9770.jpg" // Гора
                5 -> "http://s3.fotokto.ru/photo/full/460/4601628.jpg" // Снег
                else -> {
                    "https://img2.goodfon.ru/original/3694x2463/6/9a/evropeyskaya-koshka-dikiy-kot.jpg" // Кот
                }
            }
            val uri =
                Uri.parse(linkS) // Парсим ссылки
            try {
                val bitmap = resizeBitmap(
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        uri
                    )
                )
                Constants.PICKED_BITMAP = bitmap
                VisionApi.findLocation(
                    bitmap,
                    AppPreferences.getToken(applicationContext),
                    object : OnVisionApiListener {
                        override fun onSuccess(latLng: LatLng) {
                            throw Exception("this test is not for places")
                        }

                        override fun onErrorPlace(category: String) {
                            when (category) {
                                "sea" -> {
                                    assertEquals(i, 1)
                                }
                                "ocean" -> {
                                    assertEquals(i, 2)
                                }
                                "beach" -> {
                                    assertEquals(i, 3)
                                }
                                "mountain" -> {
                                    assertEquals(i, 4)
                                }
                                "snow" -> {
                                    assertEquals(i, 5)
                                }
                                else -> {
                                    assertEquals(i, 1)
                                }
                            }
                        }

                        override fun onError() {
                            throw Exception("error")
                        }
                    })
            } catch (e: Exception) {
                assertEquals(0, 1)
            }
        }
    }
}
