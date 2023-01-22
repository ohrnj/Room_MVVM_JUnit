package com.example.room_mvvm_junit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.room_mvvm_junit.databinding.ActivityMainBinding
import com.example.room_mvvm_junit.db.ImageDataBase
import com.example.room_mvvm_junit.db.ImageDataModel
import com.example.room_mvvm_junit.repositories.DefaultRepository
import com.example.room_mvvm_junit.ui.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val dao = ImageDataBase.getInstance(this).imageDao()
        val repository = DefaultRepository(dao)

        val factory = viewModelFactory { initializer { MyViewModel(repository) } }
        viewModel = ViewModelProvider(this, factory)[MyViewModel::class.java]

        val itemList = viewModel.getSavedImages()
        val rv = binding.RVActivitymain
        val observer = Observer<List<ImageDataModel>> { list ->

            rv.adapter = RVadapter(list) { selectedItem: ImageDataModel ->
                viewModel.onSelectedItemClick(selectedItem)
            }
        }
        itemList.observe(this, observer)
        rv.layoutManager = LinearLayoutManager(this)

        binding.myViewModel = viewModel
        binding.lifecycleOwner = this

        //Show event message from ViewModel class
        viewModel.stmessage.observe(this, Observer {
            it.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                if (it == "loadImage") loadImage() //clickOnImage function loadImage in ViewModel
            }
        })
    } //End of OnCreate

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val imageUri =
                        result.data!!.data  // imageUri of the file we selected from gallery
                    val returnCursor =
                        contentResolver.query(imageUri!!, null, null, null, null)
                    val nameIndex =
                        returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    returnCursor.moveToFirst()
                    val fileName = returnCursor.getString(nameIndex)
                    // Create new file in cache folder
                    val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val savedPhotoFile = File.createTempFile(fileName, ".jpg", storageDirectory)
                    val cacheFileUri = savedPhotoFile.toURI()
                    copyFilefromGallery(imageUri, savedPhotoFile)
                    val uri = Uri.parse(cacheFileUri.toString())
                    Glide.with(this).load(uri).into(binding.ivActivitymain)
                    viewModel.imageURL.value = cacheFileUri.toString()
                } catch (ex: java.lang.Exception) {
                    Log.d("RVLV", "exception: ${ex.toString()}")
                }
            }
        }

    // Create new file in cache folder and in that folder copy selected image from gallery
    fun copyFilefromGallery(imageUri: Uri, outputFile: File): File {

        val inputStream: InputStream =
            contentResolver.openInputStream(imageUri)!!

        inputStream.use { input ->
            FileOutputStream(outputFile).use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
        inputStream.close()
        return outputFile
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about ->
                Toast.makeText(this, "About selected", Toast.LENGTH_SHORT).show()
            R.id.menu_settings ->
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
            R.id.manu_exit ->
                Toast.makeText(this, "Exit selected", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }


}