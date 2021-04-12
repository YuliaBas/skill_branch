package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

object UserHolder {
    private val map = mutableMapOf<String, User> ()

    fun checkExistingUser(Key: String) = map.contains(Key)

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User{
        if (!checkExistingUser(email))
            return User.makeUser(fullName, email=email, password=password)
                .also { user -> map[user.login] = user }
        else throw IllegalArgumentException ("A user with this email already exists")
    }

    fun loginUser (login: String, password: String): String? =
        map[login.trim()]?.let {
        if (it.checkPassword(password)) it.userInfo
        else null
        }
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

}