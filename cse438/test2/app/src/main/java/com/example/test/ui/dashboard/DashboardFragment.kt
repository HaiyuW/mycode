
import org.junit.runner.Request.method
import org.omg.PortableInterceptor.Interceptor

import org.omg.PortableInterceptor.Interceptor

import org.junit.runner.Request.method

object ServiceGenerator {

    val API_BASE_URL = "https://your.api-base.url"

    private val httpClient = OkHttpClient.Builder()

    private val builder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null)
    }

    fun <S> createService(serviceClass: Class<S>, username: String, password: String): S {
        // we shortened this part, because it’s covered in
        // the previous post on basic authentication with Retrofit
    }

    fun <S> createService(serviceClass: Class<S>, token: AccessToken?): S {
        if (token != null) {
            httpClient.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()

                    val requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header(
                            "Authorization",
                            token!!.getTokenType() + " " + token!!.getAccessToken()
                        )
                        .method(original.method(), original.body())

                    val request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })
        }

        val client = httpClient.build()
        val retrofit = builder.client(client).build()
        return retrofit.create(serviceClass)
    }
}