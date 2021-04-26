package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

object UserHolder {
    private val map = mutableMapOf<String, User> ()

    fun checkExistingUser(Key: String) = map.contains(Key)

    fun checkValidPhone(phoneAdj: String):Boolean {
        return phoneAdj.length == 12
    }

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        println("!checkExistingUser, $email = ${!checkExistingUser(email)}")
        if (!checkExistingUser(email))
            return User.makeUser(fullName, email=email, password=password)
                .also { user -> map[email] = user }
        else throw IllegalArgumentException ("A user with this email already exists")
    }

    fun loginUser (login: String, password: String): String? =
        map[login.trim()]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        val phoneAdj = "+" + rawPhone.replace("[^0123456789]".toRegex(),"")
        println("checkValidPhone, $phoneAdj = ${checkValidPhone(phoneAdj)}")
        println("!checkExistingUser, $phoneAdj = ${!checkExistingUser(phoneAdj)}")
        if (checkValidPhone(phoneAdj)) {
            if (!checkExistingUser(phoneAdj)) {
                return User.makeUser(fullName, phone = phoneAdj)
                    .also { user -> map[phoneAdj] = user }
                    .also { println ("user was made") }
            } else throw IllegalArgumentException("A user with this phone already exists")
        } else throw IllegalArgumentException ("Enter a valid phone number starting with a + and containing 11 digits, $phoneAdj")
    }

    fun requestAccessCode(login: String) : Unit {
        val phoneAdj = "+" + login.replace("[^0123456789]".toRegex(),"")
        val user: User? = map[phoneAdj]
        user?.changeAccessCode(phoneAdj)

        // map[phoneAdj]?.apply  {
        //     accessCode = this.generateAccessCode()
        //     println("login is $phoneAdj, new accessCode is $accessCode")
        //     passwordHash = encrypt(accessCode)
        //     }
    }



    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

}
