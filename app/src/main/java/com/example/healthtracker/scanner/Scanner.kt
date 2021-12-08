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
import android.util.Log
import android.view.MenuItem
import android.view.View
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtracker.*
import com.example.healthtracker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.io.InputStream

class Scanner : AppCompatActivity(), RecycleViewFoodHistoryAdapter.OnItemClickListener {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var authentication: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var userID : String

    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private lateinit var image_uri: Uri
    private lateinit var btnAction: String      //S for scanner, C for customize (putExtra value), H for History

    //Recycle View
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<RecycleViewFoodHistory>
    private lateinit var adapter: RecycleViewFoodHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpFirebase()

        //Camera and storage permission
        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.mHome -> {
                    finish()
                }
                R.id.mProfile -> {
                    startActivity(Intent(this, AuthorisedUser::class.java))
                    finish()
                }
                R.id.mFAQ -> {
                    startActivity(Intent(this, FAQ::class.java))
                    finish()
                }
                R.id.mHelp -> {
                    startActivity(Intent(this, ChatBot::class.java))
                    finish()
                }
                R.id.mAboutUs -> {
                    startActivity(Intent(this, AboutUs::class.java))
                    finish()
                }
                R.id.mLogout -> {
                    authentication.signOut()
                    Toast.makeText(this,"Successfully Logout", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    finishAffinity()
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
        addFoodBtn.setOnClickListener {
            val intent =Intent(this, ScannerFoodDetail::class.java)
            btnAction = "C"
            intent.putExtra("Action", btnAction)
            startActivity(intent)
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
                    .setGuidelines(CropImageView.Guidelines.OFF)
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
        val resultUri: Uri = uri
        // ML kit (from firebase recognizer)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        //val recognizer1: TextRecognizer = TextRecognizer.Builder(applicationContext).build()
        val stream: InputStream? = contentResolver.openInputStream(resultUri)
        val drawable = Drawable.createFromStream(stream, resultUri.toString())
        val bitmap: Bitmap = drawable.toBitmap()


        val image = InputImage.fromBitmap(bitmap, 0)
        var wholeText: ArrayList<String> = ArrayList()
        val result = recognizer.process(image).addOnSuccessListener {
            if(it != null) {
                val resultText = it.text
                for (block in it.textBlocks) {
                    for (line in block.lines) {
                        for(element in line.elements){
                            wholeText.add(element.text)
                            wholeText.add(" ")
                        }
                    }
                }
                // after scanning go to scannerfooddetail activity
                val intent = Intent(this, ScannerFoodDetail::class.java)
                btnAction = "S"
                intent.putExtra("wholeText", wholeText)
                intent.putExtra("Action", btnAction)
                startActivity(intent)
                Toast.makeText(this, "Scanning..", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "The image has no text", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, " " + it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        val intent: Intent
        if ((result.resultCode == Activity.RESULT_OK) && (result.data != null)) {
            intent = result.data!!
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                cropActivityResultLauncher.launch(image_uri)
            }
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                cropActivityResultLauncher.launch(intent.data)
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
    }
    private fun pickGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getGalleryActivityResultLauncher.launch(intent)
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

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ScannerFoodDetail::class.java)
        intent.putExtra("foodID", list[position].getFoodID())
        intent.putExtra("Action", "H")
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        // Get current User id and email
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
            navView.getHeaderView(0).dhEmail.text = authentication.currentUser!!.email
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            finishAffinity()
        }
        val nameDocRef = firebase.collection("User").document(userID)
        nameDocRef.get().addOnSuccessListener{ name ->
            if(name != null){
                navView.getHeaderView(0).dhName.text = name.getString("userName")
            }
        }
        val caloriesDocRef = firebase.collection("User").document(userID)
        caloriesDocRef.get().addOnSuccessListener { kcal ->
            if(kcal != null){
                navView.getHeaderView(0).dhKcal.text = kcal.get("calories").toString()
            }
        }
        //Recycle View
        recyclerView = recyclerViewScannerHistory
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.setHasFixedSize(true)
        list = arrayListOf()

        adapter = RecycleViewFoodHistoryAdapter(list, this)

        recyclerView.adapter = adapter
        noDataTxt.visibility = View.GONE
        firebase.collection("Food History").whereEqualTo("userID", userID).addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("FireStore Error", error.message.toString())
            }else {
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        list.add(dc.document.toObject(RecycleViewFoodHistory::class.java))
                    }
                }
                if(list.isEmpty()){
                    noDataTxt.visibility = View.VISIBLE
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun setUpFirebase(){
        authentication = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        if(authentication.currentUser != null) {
            userID = authentication.currentUser!!.uid
        }
    }
}