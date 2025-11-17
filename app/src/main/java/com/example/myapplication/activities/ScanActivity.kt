package com.example.myapplication.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.models.Livrable
import com.example.myapplication.services.FirebaseService
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var captureButton: MaterialButton
    private lateinit var usePhotoButton: MaterialButton
    private lateinit var retakeButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var resultTextView: TextView
    private lateinit var previewContainer: LinearLayout
    private lateinit var resultContainer: LinearLayout

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var capturedImage: Bitmap? = null
    private val firebaseService = FirebaseService()
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    companion object {
        private const val TAG = "ScanActivity"
        private const val REQUEST_CAMERA_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        initViews()
        setupButtons()
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    private fun initViews() {
        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.captureButton)
        usePhotoButton = findViewById(R.id.usePhotoButton)
        retakeButton = findViewById(R.id.retakeButton)
        progressBar = findViewById(R.id.progressBar)
        resultTextView = findViewById(R.id.resultTextView)
        previewContainer = findViewById(R.id.previewContainer)
        resultContainer = findViewById(R.id.resultContainer)

        // Cacher le conteneur de résultats au début
        resultContainer.visibility = android.view.View.GONE
    }

    private fun setupButtons() {
        captureButton.setOnClickListener {
            captureImage()
        }

        usePhotoButton.setOnClickListener {
            processCapturedImage()
        }

        retakeButton.setOnClickListener {
            showCameraPreview()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Les permissions de la caméra sont nécessaires",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Erreur liaison caméra", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        // Créer le fichier de sortie
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "SCAN_$name")
            put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val uri = output.savedUri
                    if (uri != null) {
                        // Convertir URI en Bitmap
                        try {
                            val inputStream = contentResolver.openInputStream(uri)
                            capturedImage = BitmapFactory.decodeStream(inputStream)
                            inputStream?.close()
                            showCapturedImage()
                        } catch (e: Exception) {
                            Log.e(TAG, "Erreur chargement image", e)
                            Toast.makeText(this@ScanActivity, "Erreur chargement image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Erreur capture: ${exception.message}", exception)
                    Toast.makeText(this@ScanActivity, "Erreur capture", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showCapturedImage() {
        previewContainer.visibility = android.view.View.GONE
        resultContainer.visibility = android.view.View.VISIBLE

        // Afficher l'image capturée (optionnel)
        // val imageView: ImageView = findViewById(R.id.capturedImageView)
        // imageView.setImageBitmap(capturedImage)
    }

    private fun showCameraPreview() {
        resultContainer.visibility = android.view.View.GONE
        previewContainer.visibility = android.view.View.VISIBLE
        resultTextView.text = ""
        capturedImage = null
    }

    private fun processCapturedImage() {
        val image = capturedImage ?: return

        progressBar.visibility = android.view.View.VISIBLE
        usePhotoButton.isEnabled = false

        val inputImage = InputImage.fromBitmap(image, 0)

        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                progressBar.visibility = android.view.View.GONE
                usePhotoButton.isEnabled = true

                val extractedText = visionText.text
                resultTextView.text = "📝 Texte extrait:\n\n$extractedText"

                // Analyser le texte pour créer un livrable
                val livrableData = analyzeText(extractedText)
                createLivrableFromScan(livrableData)
            }
            .addOnFailureListener { e ->
                progressBar.visibility = android.view.View.GONE
                usePhotoButton.isEnabled = true
                Log.e(TAG, "Erreur reconnaissance texte", e)
                Toast.makeText(this, "Erreur reconnaissance texte", Toast.LENGTH_SHORT).show()
            }
    }

    private fun analyzeText(text: String): Map<String, String> {
        val data = mutableMapOf<String, String>()

        // Extraction du nom (première ligne ou titre)
        val lines = text.lines().filter { it.isNotBlank() }
        if (lines.isNotEmpty()) {
            data["nom"] = lines.first().take(50) // Limiter la longueur
        }

        // Détection du département
        data["departement"] = detectDepartment(text)

        // Détection de la date
        data["date"] = detectDate(text)

        // Utiliser le texte comme description
        data["description"] = text.take(200) // Limiter la longueur

        return data
    }

    private fun detectDepartment(text: String): String {
        val textLower = text.toLowerCase(Locale.getDefault())
        return when {
            textLower.contains("dev") || textLower.contains("technique") || textLower.contains("code") -> "Développement"
            textLower.contains("market") || textLower.contains("vente") || textLower.contains("commercial") -> "Marketing"
            textLower.contains("design") || textLower.contains("ui") || textLower.contains("ux") -> "Design"
            textLower.contains("rh") || textLower.contains("ressource") || textLower.contains("personnel") -> "Ressources Humaines"
            textLower.contains("direction") || textLower.contains("manager") || textLower.contains("chef") -> "Direction"
            else -> "Général"
        }
    }

    private fun detectDate(text: String): String {
        val patterns = listOf(
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "yyyy-MM-dd",
            "dd MM yyyy"
        )

        for (pattern in patterns) {
            try {
                val regex = pattern.replace("dd", "\\d{2}").replace("MM", "\\d{2}").replace("yyyy", "\\d{4}")
                val match = Regex(regex).find(text)
                if (match != null) {
                    return match.value
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erreur détection date", e)
            }
        }

        // Date par défaut (7 jours)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun createLivrableFromScan(data: Map<String, String>) {
        val nom = data["nom"] ?: "Document scanné"
        val description = data["description"] ?: ""
        val departement = data["departement"] ?: "Général"
        val dateString = data["date"] ?: ""

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val deadline = try {
            dateFormat.parse(dateString) ?: Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)
        } catch (e: Exception) {
            Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)
        }

        val livrable = Livrable(
            nom = nom,
            description = "📸 Document scanné\n\n$description",
            departement = departement,
            deadline = deadline,
            priorite = "moyenne",
            statut = "a_faire"
        )

        // Sauvegarder dans Firebase
        progressBar.visibility = android.view.View.VISIBLE
        usePhotoButton.isEnabled = false

        firebaseService.addLivrable(
            livrable = livrable,
            onSuccess = {
                progressBar.visibility = android.view.View.GONE
                usePhotoButton.isEnabled = true
                showSuccessMessage()
            },
            onError = { error ->
                progressBar.visibility = android.view.View.GONE
                usePhotoButton.isEnabled = true
                Toast.makeText(this, "Erreur sauvegarde: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showSuccessMessage() {
        android.app.AlertDialog.Builder(this)
            .setTitle("✅ Succès")
            .setMessage("Le livrable a été créé à partir du scan !")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish() // Retour à l'écran précédent
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}