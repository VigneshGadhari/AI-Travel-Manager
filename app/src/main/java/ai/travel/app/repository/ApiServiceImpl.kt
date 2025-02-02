package ai.travel.app.repository

import ai.travel.app.BuildConfig
import ai.travel.app.dto.ApiPrompt
import ai.travel.app.dto.Candidate
import ai.travel.app.dto.PalmApi
import ai.travel.app.dto.geocoding.GeoCodes
import ai.travel.app.dto.getPhotoId.PhotoIdResponse
import ai.travel.app.dto.getPlaceId.PlaceIdBody
import ai.travel.app.dto.getPlaceId.PlaceIdResponse
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.util.InternalAPI
import java.net.URLEncoder

class ApiServiceImpl(
    private val client: HttpClient,
) : ApiService {

    override suspend fun getApiData(apiPrompt: ApiPrompt): PalmApi {
        return try {
            client.post {
                url("${ApiRoutes.BASE_URL}?key=${BuildConfig.API_KEY}")
                setBody(apiPrompt)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }.body<PalmApi>()
        } catch (e: Exception) {
            Log.i("ApiException", e.message.toString())
            return PalmApi(
                candidates = listOf(
                    Candidate(
                        output = e.message.toString(),
                        safetyRatings = null
                    )
                )
            )
        }

    }

    override suspend fun getGeocodingData(query: String): GeoCodes {
        return try {
            client.get {
            val encodedLocation = URLEncoder.encode(query, "UTF-8")
            url("${ApiRoutes.Geocoding_URL}?q=$encodedLocation&apiKey=${BuildConfig.Here_API_KEY}")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            headers {
                append("Accept", "*/*")
                append("Content-Type", "application/json")
            }
        }.body()
        } catch (e: Exception) {
            Log.i("ApiException", e.message.toString())
            return GeoCodes(
                items = null
            )
        }
    }

    override suspend fun getPlaceIdData(text: PlaceIdBody): PlaceIdResponse {
        return try {
            client.post {
                url(ApiRoutes.getPlaceIdApi)
                setBody(text)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                headers {
                    append("Content-Type", "application/json")
                    append("X-Goog-Api-Key", BuildConfig.Places_API_KEY)
                    append("X-Goog-FieldMask", "places.id,places.displayName,places.formattedAddress")
                }
            }.body()
        } catch (e: Exception) {
            Log.i("ApiException", e.message.toString())
            return PlaceIdResponse(
               places = null
            )
        }
    }

    override suspend fun getPhotoId(photoId: String): PhotoIdResponse {
        return try {
           client.get {
                println("photoIdooooo: $photoId")
                val encodedLocation = URLEncoder.encode(photoId, "UTF-8")
                url("${ApiRoutes.getPhotoIdApi}?placeid=$encodedLocation&key=${BuildConfig.Places_API_KEY}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                headers {
                    append("Accept", "*/*")
                    append("Content-Type", "application/json")
                }
            }.body()
        } catch (e: Exception) {
            Log.i("ApiException", e.message.toString())
            return PhotoIdResponse(
                result = null,
                status = null,
            )
        }
    }

    override suspend fun getPhoto(
        photoReference: String,
        maxWidth: Int,
        maxHeight: Int,
    ): ByteArray {
        return try {
            println("photooooo 111: $photoReference")
            val a = client.get {
                val encodedLocation = URLEncoder.encode(photoReference, "UTF-8")
                url("${ApiRoutes.getPhoto}?maxwidth=$maxWidth&photo_reference=$encodedLocation&key=${BuildConfig.Places_API_KEY}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                headers {
                    append("Accept", "*/*")
                    append("Content-Type", "application/json")
                }
            }.body<ByteArray>()
            println("photooooo: $a")
            return a
        } catch (e: Exception) {
            Log.i("ApiException", e.message.toString())
            return ByteArray(0)
        }
    }
}

