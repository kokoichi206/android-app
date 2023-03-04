package jp.mydns.kokoichi0206.playground

import androidx.annotation.Keep
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import retrofit2.http.GET

data class User(
    val name: String,
    @AllowedDateRegex("\\d{4}-\\d{2}-\\d{2}") val birthDate: String,
) {
    init {
        val fields = this::class.java.declaredFields
        fields.forEach { field ->
            field.annotations.forEach { annotation ->
//                annotation.equals(AllowedDateRegex::class.java)
                if (field.isAnnotationPresent(AllowedDateRegex::class.java)) {
                    val regex = field.getAnnotation(AllowedDateRegex::class.java)?.regex
                    if (regex?.toRegex()?.matches(birthDate) == false) {
                        throw IllegalArgumentException("Birth date is not a valid format: $birthDate")
                    }
                }
            }
        }
    }
}

// 他にもよく使うもの
//@MustBeDocumented
//@Retention(AnnotationRetention.BINARY)
//@Keep
//@Repeatable

@Target(AnnotationTarget.FIELD)
annotation class AllowedDateRegex(val regex: String)


// ========== ↓ 実用例 ↓ ==========
interface AnnotationApi {

    @GET("/users/1")
    suspend fun getUser()

    @GET("/posts/1")
    @Authenticated
    suspend fun getPost()
}

@Target(AnnotationTarget.FUNCTION)
annotation class Authenticated

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val invocation = chain.request().tag(Invocation::class.java)
            ?: return chain.proceed(chain.request())

        val shouldAttachAuthHeader = invocation
            .method()
            .annotations
            .any { it.annotationClass == Authenticated::class }

        return if (shouldAttachAuthHeader) {
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "my token")
                    .build()
            )
        } else chain.proceed(chain.request())
    }
}
