package com.heartcare.agni.utils.constants

object ErrorConstants {
    const val TOO_MANY_ATTEMPTS_ERROR = "Too many attempts. Please try after 5 mins"
    const val SESSION_EXPIRED = "Session expired."
    const val UNAUTHORIZED = "Unauthorized"
    const val SOCKET_TIMEOUT_EXCEPTION = "Socket Timeout Exception from server"
    const val IO_EXCEPTION = "IO Exception from server"
    const val SCHEDULE_EXISTS = "Schedule already exists"
    const val SERVER_ERROR = "Server error"
    const val USER_DOES_NOT_EXIST = "User does not exist"
    const val SOMETHING_WENT_WRONG = "Something went wrong"
    const val ERROR_FETCHING_USER_DETAILS = "Error fetching user details"
    const val FAILED_TO_SEND_EMAIL = "Failed to send Email"
    const val EMAIL_NOT_REGISTERED_BACKEND = "No user with provided email-id exists in the system"
    const val EMAIL_NOT_REGISTERED_ERROR_UI = "Email entered is not registered"
    const val INVALID_OTP = "Entered OTP is invalid"
    const val INCORRECT_CODE = "Incorrect authorization code"
}