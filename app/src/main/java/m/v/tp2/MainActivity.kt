package m.v.tp2

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authenticationButton.setOnClickListener {
            thread {
                try {
                    val url = URL("https://httpbin.org/basic-auth/bob/sympa")
                    val urlConnection: HttpsURLConnection =
                        url.openConnection() as HttpsURLConnection
                    val basicAuth = "Basic " + Base64.encodeToString(getAuthInfo().encodeToByteArray(), Base64.NO_WRAP)
                    urlConnection.setRequestProperty ("Authorization", basicAuth)
                    try {
                        val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
                        val s: String? = readStream(`in`)
                        if (s != null) {
                            Log.i("JFL", s)
                            val jsonObject = JSONObject(s)
                            val result = jsonObject.get("authenticated") as Boolean
                            runOnUiThread {
                                resultText.text = result.toString()
                            }
                        }
                    } finally {
                        urlConnection.disconnect()
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getAuthInfo(): String {
        return String.format(
            resources.getString(R.string.auth),
            loginField.text.toString(),
            passwordField.text.toString()
        )
    }

    private fun readStream(`is`: InputStream): String? {
        return try {
            val bo = ByteArrayOutputStream()
            var i = `is`.read()
            while (i != -1) {
                bo.write(i)
                i = `is`.read()
            }
            bo.toString()
        } catch (e: IOException) {
            ""
        }
    }
}