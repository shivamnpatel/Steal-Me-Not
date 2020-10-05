package com.project.stealmenot;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Security extends Activity {
    @RequiresApi(api = Build.VERSION_CODES.M)

        /** Alias for our key in the Android Key Store. */
        private static final String KEY_NAME = "my_key";
        private static final byte[] SECRET_BYTE_ARRAY = new byte[] {1, 2, 3, 4, 5, 6};
        Boolean result;
        private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;
        public static MediaPlayer mPlayer;
        //int sino = 0;
        KeyEvent event;
        ProtectionModesActivity ps=new ProtectionModesActivity();
        /**
         * If the user has unlocked the device Within the last this number of seconds,
         * it can be considered as an authenticator.
         */
        private static final int AUTHENTICATION_DURATION_SECONDS = 5;

        private KeyguardManager mKeyguardManager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_security);
            //mPlayer = MediaPlayer.create(com.project.stealmenot.Security.this, R.raw.siren);
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (!mKeyguardManager.isKeyguardSecure()) {
                // Show a message that the user hasn't set up a lock screen.
                Toast.makeText(this,
                        "Secure lock screen hasn't set up.\n"
                                + "Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
                        Toast.LENGTH_LONG).show();
                return;
            }
            createKey();
       /* mPlayer = MediaPlayer.create(Security.this, R.raw.siren);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        mPlayer.start();
        mPlayer.setLooping(true);*/
            tryEncrypt();

        }
        //Disable Volume Key
        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    return true;
                default:
                    return super.dispatchKeyEvent(event);
            }
        }
        private boolean tryEncrypt() {
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
                Cipher cipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);

                // Try encrypting something, it will only work if the user authenticated within
                // the last AUTHENTICATION_DURATION_SECONDS seconds.
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                cipher.doFinal(SECRET_BYTE_ARRAY);

                // If the user has recently authenticated, you will reach here.
                // showAlreadyAuthenticated();
                //Toast.makeText(this,"DOne",Toast.LENGTH_LONG).show();
                return true;
            } catch (UserNotAuthenticatedException e) {
                // User is not authenticated, let's authenticate with device credentials.
                showAuthenticationScreen();
                return false;
            } catch (KeyPermanentlyInvalidatedException e) {
                // This happens if the lock screen has been disabled or reset after the key was
                // generated after the key was generated.
                Toast.makeText(this, "Keys are invalidated after created. Retry the purchase\n"
                                + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                return false;
            } catch (BadPaddingException | IllegalBlockSizeException | KeyStoreException |
                    CertificateException | UnrecoverableKeyException | IOException
                    | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }
        private void createKey() {
            // Generate a key to decrypt payment credentials, tokens, etc.
            // This will most likely be a registration step for the user when they are setting up your app.
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                // Set the alias of the entry in Android KeyStore where the key will appear
                // and the constrains (purposes) in the constructor of the Builder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            // Require that the user has unlocked in the last 30 seconds
                            .setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_DURATION_SECONDS)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
                }
                keyGenerator.generateKey();
            } catch (NoSuchAlgorithmException | NoSuchProviderException
                    | InvalidAlgorithmParameterException | KeyStoreException
                    | CertificateException | IOException e) {
                throw new RuntimeException("Failed to create a symmetric key", e);
            }
        }

        private void showAuthenticationScreen() {
            // Create the Confirm Credentials screen. You can customize the title and description. Or
            // we will provide a generic one for you if you leave it null
            playMedia();



            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //Toast.makeText(this,"DOne",Toast.LENGTH_LONG).show();
                intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
                // Toast.makeText(this,"DOne22222",Toast.LENGTH_LONG).show();
            }
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
                // Challenge completed, proceed with using cipher
                if (resultCode == RESULT_OK) {
                    if (tryEncrypt()) {
                        //showPurchaseConfirmation();

                        mPlayer.stop();
                        Intent intent = new Intent(this, ProtectionModesActivity.class);


                        if(ProtectionModesActivity.isMasterModeOn)
                        {
                            //ProtectionModesActivity.headphone.setChecked(false);
                            ProtectionModesActivity.masterSwitch.setChecked(true);
                            //ProtectionModesActivity.switchcharge.setChecked(false);

                        }

                        if(ProtectionModesActivity.isChargeModeOn)
                        {
                            ProtectionModesActivity.switchcharge.setChecked(true);
                        }
                        if( ProtectionModesActivity.isheadphoneModeOn) {
                            ProtectionModesActivity.headphone.setChecked(true);
                        }

                        startActivity(intent);
                        finish();
                    }
                } else {
                    // The user canceled or didn’t complete the lock screen
                    // operation. Go to error/cancellation flow.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        public void playMedia() {
            // mPlayer = MediaPlayer.create(Security.this, R.raw.siren);
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
            //mPlayer.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // mPlayer = MediaPlayer.create(Security.this, R.raw.siren);
                    mPlayer.setLooping(true);
                    mPlayer.start();
                }
            }, 5000);
            //mPlayer.setLooping(true);

        }

    }
