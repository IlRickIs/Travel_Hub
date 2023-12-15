package it.unimib.travelhub.crypto_util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class DataEncryptionUtil {
    private final Context context;

    public DataEncryptionUtil(Application application) {
        this.context = application.getApplicationContext();
    }

    /**
     * Writes a value using EncryptedSharedPreferences (both key and value will be encrypted).
     * @param sharedPreferencesFileName the name of the SharedPreferences file where to write the data
     * @param key The key associated with the value
     * @param value The value to be written
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void writeSecretDataWithEncryptedSharedPreferences(String sharedPreferencesFileName,
                                                              String key, String value)
            throws GeneralSecurityException, IOException {

        MasterKey mainKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        // Creates a file with this name, or replaces an existing file that has the same name.
        // Note that the file name cannot contain path separators.
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                context,
                sharedPreferencesFileName,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Reads a value encrypted using EncryptedSharedPreferences class.
     * @param sharedPreferencesFileName the name of the SharedPreferences file where data are saved
     * @param key The key associated with the value to be read
     * @return The decrypted value
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public String readSecretDataWithEncryptedSharedPreferences(String sharedPreferencesFileName,
                                                               String key)
            throws GeneralSecurityException, IOException {

        MasterKey mainKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                context,
                sharedPreferencesFileName,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        return sharedPreferences.getString(key, null);
    }
}
