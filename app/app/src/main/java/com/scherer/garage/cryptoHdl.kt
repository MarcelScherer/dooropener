package com.scherer.garage

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec


class cryptoHdl() {

    private val TAG = "RSACryptor"
    private val CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"

    private var keyAlias: String = "testAlias"
    private var keyPasswd : String = "testPwd"
    private lateinit var keyPair : KeyPair
    private lateinit var privateKey :  PrivateKey
    private lateinit var publicKey :  PublicKey
    private lateinit var context : Context

    constructor(keyAlias : String, keyPasswd : String, context : Context) : this() {

        this.keyAlias = keyAlias
        this.keyPasswd = keyPasswd
        this.context = context
        val keyStore = KeyStore.getInstance("AndroidKeyStore");
        Log.d("cryptoHdl", "start crypto class")
        try {
            // generate new key if not exist
            if (keyStore != null) {
                keyStore.load(null)
                if(!keyStore.containsAlias(this.keyAlias)){
                    Log.d("cryptoHdl", "generate new rsa key")
                    keyPair = createKey()
                    this.privateKey = keyPair.private
                    this.publicKey = keyPair.public
                }else{
                    Log.d("cryptoHdl", "load rsa key")
                    val entry = keyStore.getEntry(this.keyAlias, null)
                    this.privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
                    this.publicKey = keyStore.getCertificate(this.keyAlias).publicKey
                }
                publicKeyToFile(this.publicKey)
                //Log.d("cryptoHdl", "public key: " + String(Base64.encode(this.publicKey.getEncoded(), 0)))

            }
        } catch (e:  ArithmeticException ){

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun createKey():KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
        )
        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder(
                this.keyAlias,
                KeyProperties.PURPOSE_SIGN
            )
                .setKeySize(2048)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .build()
        )
        return keyPairGenerator.generateKeyPair()
    }

     private fun publicKeyToFile(key:PublicKey){
        Log.d("cryptoHdl", "write public key to file")
        val publicKeyBytes: ByteArray = Base64.encode(key.getEncoded(), 0)
        val pubKey = String(publicKeyBytes)
        val codons = pubKey.chunked(65)
        val pem_folder  = context.getExternalFilesDir(null).toString() + File.separator + "garage" + File.separator
        var folder : File = File(pem_folder);
        val pem_file  = pem_folder + File.separator + "public.pem"
        var file : File = File(pem_file);
        Log.d("cryptoHdl", file.getAbsolutePath())
        Log.d("cryptoHdl", folder.mkdir().toString() + " ---");
        if(!folder.exists()) {
            folder.mkdirs()
        }
        if(!file.exists()) {
            file.createNewFile();
            val datei = FileOutputStream(file.getAbsolutePath().toString(), true)
            val writer = OutputStreamWriter(datei)
            writer.write("-----BEGIN PUBLIC KEY-----\n")
            val i: String = ""
            for (i in codons) {
                writer.write(i)
            }
            writer.write("-----END PUBLIC KEY-----")
            writer.close()
            datei.close()
        }
    }

    fun SignString(data : String) : ByteArray{
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(this.privateKey)
        signature.update(data.toByteArray())
        val signatureBytes = signature.sign()
        val signatureBase64 = Base64.encodeToString(signatureBytes, Base64.NO_WRAP)
        Log.d("cryptoHdl", "signatureBase64(" + signatureBase64.length.toString() + "): " + signatureBase64)
        return signatureBytes;
    }
}
