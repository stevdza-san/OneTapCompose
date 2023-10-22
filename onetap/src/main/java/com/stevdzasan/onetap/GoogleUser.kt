package com.stevdzasan.onetap

import android.util.Log
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT

data class GoogleUser(
    val sub: String?,
    val email: String?,
    val emailVerified: Boolean?,
    val fullName: String?,
    val givenName: String?,
    val familyName: String?,
    val picture: String?,
    val issuedAt: Long?,
    val expirationTime: Long?,
    val locale: String?
)

/**
 * Use this function to extract [GoogleUser] information from a token id that you're
 * getting from One-Tap Sign in. The token id is usually valid for only one hour after
 * a successful authentication.
 * @return This function returns a [GoogleUser] object, or null if an exception occurred,
 * due to invalid token decoding, or because your token id has expired.
 * */
fun getUserFromTokenId(tokenId: String): GoogleUser? {
    try {
        val jwt = JWT(tokenId)
        return GoogleUser(
            sub = jwt.claims[Claims.SUB]?.asString(),
            email = jwt.claims[Claims.EMAIL]?.asString(),
            emailVerified = jwt.claims[Claims.EMAIL_VERIFIED]?.asBoolean(),
            fullName = jwt.claims[Claims.FUll_NAME]?.asString(),
            givenName = jwt.claims[Claims.GIVEN_NAME]?.asString(),
            familyName = jwt.claims[Claims.FAMILY_NAME]?.asString(),
            picture = jwt.claims[Claims.PICTURE]?.asString(),
            issuedAt = jwt.claims[Claims.ISSUED_AT]?.asLong(),
            expirationTime = jwt.claims[Claims.EXPIRATION_TIME]?.asLong(),
            locale = jwt.claims[Claims.LOCALE]?.asString()
        )
    } catch (e: Exception) {
        Log.e("OneTapCompose", e.toString())
        return null
    } catch (e: DecodeException) {
        Log.e("OneTapCompose", e.toString())
        return null
    }
}