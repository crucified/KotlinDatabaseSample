package com.example.crucified.myfirstkotlin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.crucified.myfirstkotlin.database.Art
import com.example.crucified.myfirstkotlin.database.Database
import kotlinx.android.synthetic.main.activity_add_art.*

class AddArtActivity : AppCompatActivity() {

    private var selectedImage: Bitmap? = null

    companion object {
        private const val MEDIA_SELECTION_CODE = 1
        private const val PERMISSION_REQUEST_CODE = 2
        const val IS_MUTABLE = "isMutable"
        const val ART = "art"
        var selectedArt: Art? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art)

        val isMutable = intent.getBooleanExtra(IS_MUTABLE, true)
        if (!isMutable) {
            imageView.isEnabled =  false
            imageView.setImageBitmap(selectedArt!!.image)
            editText.isEnabled = false
            editText.setText(selectedArt!!.name)
            button.visibility = View.INVISIBLE
        }
    }

    fun pickImage(view: View) {
        if (accessGranted()) {
            openImageSelectionActivity()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MEDIA_SELECTION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            imageView.setImageBitmap(selectedImage)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            openImageSelectionActivity()
        }
    }

    private fun accessGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun openImageSelectionActivity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, MEDIA_SELECTION_CODE)
    }

    fun saveImage(view: View) {
        if (editText.text.isEmpty()) {
            showAlert("Art title not set")
            editText.requestFocus()
            return
        }

        if (selectedImage == null) {
            showAlert("Select image of the art")
            return
        }

        val artName = editText.text.toString()
        try {
            Database(applicationContext).addNewArt(artName, selectedImage!!)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            when (e) {
                is SQLiteConstraintException -> showAlert("Art with the given name exists: $artName")
                else -> showAlert("Unknown error while saving")
            }
        }

    }
}
