package gmo.demo.voidtask.data.preference

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * controll all communication with share preference
 */
class PreferenceHelper {
    companion object {
        private var Pref: SharedPreferences? = null
        private var PREF_NAME = "LockerPreference"

        /**
         * load at init of Application, need load before all of other logic
         */
        fun loadPreferences(ctx: Context) {
            val keyGenParameterSpec =
                KeyGenParameterSpec.Builder(
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE).build()

            val masterKeyAlias = MasterKey.Builder(ctx).setKeyGenParameterSpec(keyGenParameterSpec).build()


            Pref = EncryptedSharedPreferences.create(
                ctx,
                PREF_NAME,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        fun hasKey(key: String): Boolean {
            return Pref?.contains(key) == true
        }

        /**
         * save string value
         */
        fun writeString(key: String, value: String?) {
            Pref?.let {
                val editor = it.edit()
                editor.putString(key, value)
                editor.commit()
            }
        }

        /**
         * load string value
         */
        fun getString(key: String, default: String = "") = Pref?.let {
            return@let it.getString(key, default) ?: default
        } ?: default

        /**
         * save integer value
         */
        fun writeInt(key: String, value: Int) {
            Pref?.let {
                val editor = it.edit()
                editor.putInt(key, value)
                editor.commit()
            }
        }

        /**
         * save long value
         */
        fun writeLong(key: String, value: Long) {
            Pref?.let {
                val editor = it.edit()
                editor.putLong(key, value)
                editor.commit()
            }
        }

        /**
         * get integer value
         */
        fun getInt(key: String, default: Int = -1) = Pref?.let {
            return@let it.getInt(key, default)
        } ?: default

        /**
         * get long value
         */
        fun getLong(key: String, default: Long = -1) = Pref?.let {
            return@let it.getLong(key, default)
        } ?: default

        /**
         * load boolean value
         */
        fun getBoolean(key: String, default: Boolean = true) = Pref?.let {
            return@let it.getBoolean(key, default)
        } ?: default

        /**
         * save boolean value
         */
        fun writeBoolean(key: String, value: Boolean) {
            Pref?.let {
                val editor = it.edit()
                editor.putBoolean(key, value)
                editor.commit()
            }
        }

        /**
         * remove key
         */
        fun remove(key: String) {
            Pref?.let {
                val editor = it.edit()
                editor.remove(key)
                editor.commit()
            }
        }

        /**
         * save array of string
         */
        fun saveArrayList(list: ArrayList<String>, key: String) {
            Pref?.let {
                val editor = it.edit()
                val gson = Gson()
                val json = gson.toJson(list)
                editor.putString(key, json)
                editor.apply()
            }

        }

        /**
         * get array of string
         */
        fun getArrayList(key: String, default: ArrayList<String> = ArrayList()): ArrayList<String> =
            Pref?.let {
                val gson = Gson()
                val json = it.getString(key, "")
                val type = object : TypeToken<ArrayList<String>>() {
                }.type
                return gson.fromJson<ArrayList<String>>(json, type) ?: ArrayList()
            } ?: default

        /**
         * remove all key with not cont
         * @param arrKeyNotRV isEmpty is remove all key
         */
        fun removeAllKey(vararg arrKeyNotRV: String?) {
            if (arrKeyNotRV.isEmpty()) {
                Pref?.edit()?.clear()?.apply()
            } else {
                removeValueByKey(arrKeyNotRV)
            }
        }

        private fun removeValueByKey(arrKeyNotRV: Array<out String?>) {
            Pref?.let { pres ->
                val prefsMap: MutableSet<String> = pres.all.keys
                val tempPre = arrayListOf<String>()
                tempPre.addAll(prefsMap)
                for (key in prefsMap) {
                    for (keyNotRemove in arrKeyNotRV) {
                        if (key == keyNotRemove) {
                            tempPre.remove(key)
                        }
                    }
                }
                for (keyRemove in tempPre) {
                    remove(keyRemove)
                }
            }
        }
    }
}