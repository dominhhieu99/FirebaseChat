package com.dohieu19999.firebasechat.`interface`

import com.dohieu19999.firebasechat.Constants.Constants
import com.dohieu19999.firebasechat.Constants.Constants.Companion.CONTENT_TYPE
import com.dohieu19999.firebasechat.Constants.Constants.Companion.SERVER_KEY
import com.dohieu19999.firebasechat.model.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by hieudm.lvt on 3/23/2021.
 */
interface NotificationApi {
    @Headers("Authorization: key=$SERVER_KEY", "Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

}