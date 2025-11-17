package com.example.myapplication.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CameraService(private val context: Context) {

    companion object {
        private const val TAG = "CameraService"
        private const val IMAGE_QUALITY = 80
        const val REQUEST_CAMERA_PERMISSION = 1001
    }

    // Callbacks simulés
    var onCameraInitialized: (() -> Unit)? = null
    var onCameraError: ((String) -> Unit)? = null
    var onImageCaptured: ((Bitmap) -> Unit)? = null
    var onImageSaved: ((Uri) -> Unit)? = null

    // === INITIALISATION CAMÉRA ===
    fun initializeCamera(callback: (Boolean) -> Unit) {
        // Simulation : caméra toujours initialisée
        callback(true)
        onCameraInitialized?.invoke()
    }

    // === START CAMERA ===
    fun startCamera(previewView: Any) {
        // Rien à faire dans le mock
        onCameraInitialized?.invoke()
    }

    // === CAPTURE IMAGE ===
    fun captureImage() {
        // Bitmap factice 100x100 pixels
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        onImageCaptured?.invoke(bitmap)
    }

    fun captureImageToMemory() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        onImageCaptured?.invoke(bitmap)
    }

    // === TRAITEMENT IMAGE ===
    private fun processCapturedImage(uri: Uri) {
        // Juste simuler
        onImageCaptured?.invoke(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
    }

    // === NETTOYAGE RESSOURCES ===
    fun releaseCamera() {
        // Rien à libérer dans le mock
    }

    // === UTILITAIRES FICHIERS ===
    suspend fun saveBitmapToFile(bitmap: Bitmap, fileName: String): File? = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: Exception) {
            Log.e(TAG, "Erreur sauvegarde fichier", e)
            null
        }
    }

    suspend fun bitmapToByteArray(bitmap: Bitmap): ByteArray = withContext(Dispatchers.IO) {
        return@withContext try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            Log.e(TAG, "Erreur conversion byte array", e)
            ByteArray(0)
        }
    }

    // === PERMISSIONS ===
    fun arePermissionsGranted(): Boolean {
        // Toujours true dans le mock
        return true
    }
}
