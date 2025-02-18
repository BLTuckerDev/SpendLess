package dev.bltucker.spendless.login

import android.util.Base64
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinConverter @Inject constructor() {
    companion object {
        private const val ITERATIONS = 10000
        private const val KEY_LENGTH = 256
        private const val ALGORITHM = "PBKDF2WithHmacSHA1"
        private const val SALT_SIZE = 16
    }

    data class HashResult(
        val hash: String,
        val salt: String
    )

    fun hashPin(pin: String): HashResult {
        return try {
            val salt = generateSalt()
            val hash = generateHash(pin, salt)

            HashResult(
                hash = encodeToString(hash),
                salt = encodeToString(salt)
            )
        } catch (e: NoSuchAlgorithmException) {
            throw SecurityException("Failed to hash PIN: Algorithm not available", e)
        } catch (e: InvalidKeySpecException) {
            throw SecurityException("Failed to hash PIN: Invalid key specification", e)
        }
    }

    fun verifyPin(enteredPin: String?, storedHash: String?, storedSalt: String?): Boolean {
        if (enteredPin == null || storedHash == null || storedSalt == null) return false

        return try {
            val salt = decodeFromString(storedSalt)
            val hash = decodeFromString(storedHash)

            val enteredHash = generateHash(enteredPin, salt)
            hash.contentEquals(enteredHash)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is InvalidKeySpecException,
                is IllegalArgumentException -> {
                    // Log error in production
                    false
                }
                else -> throw e
            }
        }
    }

    private fun generateSalt(): ByteArray {
        return ByteArray(SALT_SIZE).also {
            SecureRandom().nextBytes(it)
        }
    }

    private fun generateHash(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(
            pin.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        return SecretKeyFactory.getInstance(ALGORITHM)
            .generateSecret(spec)
            .encoded
    }

    private fun encodeToString(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun decodeFromString(str: String): ByteArray {
        return Base64.decode(str, Base64.NO_WRAP)
    }
}