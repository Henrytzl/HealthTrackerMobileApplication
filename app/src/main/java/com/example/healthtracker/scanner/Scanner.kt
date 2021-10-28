package com.example.healthtracker.scanner

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.SparseArray
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.util.size
import com.example.healthtracker.R
import com.example.healthtracker.login.LoginActivity
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_scanner.*
import java.io.InputStream


class Scanner : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var cameraPermission: Array<String>
    lateinit var storagePermission: Array<String>
    lateinit var wholeText: String
    lateinit var image_uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Camera and storage permission
        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mHome -> {
                    finish()
                }
                R.id.mProfile -> {

                }
                R.id.mFAQ -> {

                }
                R.id.mFeedback -> {

                }
                R.id.mHelp -> {

                }
                R.id.mAboutUs -> {

                }
                R.id.mLogout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            true
        }

        imageHome.setOnClickListener{
            finish()
        }

        scannerImage.setOnClickListener {
            showImageImportDialog();
        }
    }

    private fun showImageImportDialog() {
        val items = arrayOf("Camera", "Gallery")
        val dialog = AlertDialog.Builder(this).setTitle("Select Image")
        dialog.setItems(items, DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                0 -> {
                    //Camera
                    if (!checkCameraPermission()) {
                        requestCameraPermission()
                    } else {
                        pickCamera()
                    }
                }
                1 -> {
                    //Gallery
                    if (!checkStoragePermission()) {
                        requestStoragePermission()
                    } else {
                        pickGallery()
                    }
                }
            }

        }
        )
        dialog.show()
    }

    //Crop imaged result
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?):Intent{
            return CropImage.activity(input as Uri?)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setBorderLineColor(R.color.purple_500)
                    .setBorderCornerColor(R.color.purple_700)
                    .setActivityTitle("Cropper")
                    .setActivityMenuIconColor(R.color.purple_500)
                    .getIntent(this@Scanner)
        }
        override fun parseResult(resultCode: Int, intent: Intent?): Uri?{
            return CropImage.getActivityResult(intent)?.uri
        }
    }
    //Crop image launcher
    private val cropActivityResultLauncher: ActivityResultLauncher<Any?> = registerForActivityResult(cropActivityResultContract){
        it?.let{ uri ->
            convertToText(uri)
        }
    }
    //Convert the cropped image to text
    private fun convertToText(uri: Uri) {
//        image_uri = uri
        val resultUri: Uri = uri

        val recognizer: TextRecognizer = TextRecognizer.Builder(applicationContext).build()
        val stream: InputStream? = contentResolver.openInputStream(resultUri)
        val drawable = Drawable.createFromStream(stream, resultUri.toString())
        val bitmap: Bitmap = drawable.toBitmap()

        if (!recognizer.isOperational) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        } else {
            val frame: Frame = Frame.Builder().setBitmap(bitmap).build()
            val items: SparseArray<TextBlock> = recognizer.detect(frame)
            var sb: StringBuilder = StringBuilder()
            var i = 0
            while (i < items.size) {
                var myItem: TextBlock = items.valueAt(i)
                sb.append(myItem.value)
                //sb.append("\n")
                i++
            }
            testTV.text = sb.toString()
        }
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        val intent: Intent
        if ((result.resultCode == Activity.RESULT_OK) && (result.data != null)) {
            intent = result.data!!
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                cropActivityResultLauncher.launch(image_uri)
                //CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this)
            }
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                cropActivityResultLauncher.launch(intent.data)
                //CropImage.activity(intent?.data).setGuidelines(CropImageView.Guidelines.ON).start(this)
            }
        }
    }

    private val getCameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(IMAGE_PICK_CAMERA_CODE, result)
    }
    private val getGalleryActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(IMAGE_PICK_GALLERY_CODE, result)
    }
    private fun pickCamera() {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "NewPic")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to text")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        getCameraActivityResultLauncher.launch(cameraIntent)
        //startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE)
    }
    private fun pickGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getGalleryActivityResultLauncher.launch(intent)
        //startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE)
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE)
    }
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        val result: Boolean = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED))
        val result1: Boolean = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED))

        return (result && result1)
    }
    private fun checkStoragePermission(): Boolean {
        val result: Boolean = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED))
        return result
    }

    //handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted: Boolean = (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    val writeStorageAccepted: Boolean = (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera()
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val writeStorageAccepted: Boolean = (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (writeStorageAccepted) {
                        pickGallery()
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        //return false
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val CAMERA_REQUEST_CODE = 200
        const val STORAGE_REQUEST_CODE = 400
        const val IMAGE_PICK_GALLERY_CODE = 1000
        const val IMAGE_PICK_CAMERA_CODE = 1001

    }
}