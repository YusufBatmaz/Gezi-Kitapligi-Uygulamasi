package com.yusufbatmaz.gezikitapligi

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.Calendar

class GeziEklemeActivity : AppCompatActivity() {


    lateinit var txtTitle: EditText
    lateinit var txtDetail: EditText
    lateinit var btnDate: Button
    lateinit var btnSave: Button
    lateinit var btnPickImage: Button
    lateinit var imageView: ImageView
    lateinit var db: DB


    var selectedDate = ""
    var selectedImageUri: String? = null  // Seçilen resmin URI'si

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gezi_ekle)

        txtTitle = findViewById(R.id.txtTitle)
        txtDetail = findViewById(R.id.txtDetail)
        btnDate = findViewById(R.id.btnDate)
        btnSave = findViewById(R.id.btnSave)
        btnPickImage = findViewById(R.id.btnPickImage)
        imageView = findViewById(R.id.imageView)
        db = DB(this)
        val toolbar: Toolbar = findViewById(R.id.toolbarEkle)
        setSupportActionBar(toolbar)

        // Geri butonunu etkinleştiriyoruz
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        // Tarih seçimiF
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Butona tıklama olayını ekle
        btnDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    // Seçilen tarihi formatla
                    selectedDate = "${selectedDayOfMonth}-${selectedMonth + 1}-${selectedYear}"
                    // Butonun text'ini güncelle
                    btnDate.text ="Gezi Tarihi:"+ selectedDate
                },
                year, month, dayOfMonth
            )
            datePickerDialog.show()
        }

        // Resim seçme işlemi
        val PICK_IMAGE_REQUEST = 1
        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSave.setOnClickListener {
            val title = txtTitle.text.toString()
            val detail = txtDetail.text.toString()

            if (selectedDate != "" && title != "" && detail != "") {
                // Yeni notu veritabanına ekle
                val status = db.addNote(title, detail, selectedDate, selectedImageUri.toString())

                // Başarı mesajı
                Toast.makeText(this, "Başarıyla kaydedildi!", Toast.LENGTH_LONG).show()

                // MainActivity'ye geri dönüp, listeyi güncelle
                setResult(Activity.RESULT_OK)
                finish() // AddNoteActivity'yi kapat

            } else {
                Toast.makeText(this, "Lütfen alanları eksiksiz doldurduğunuzdan emin olun", Toast.LENGTH_LONG).show()
            }
        }



    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImage = data.data
            selectedImageUri = selectedImage.toString()
            imageView.setImageURI(selectedImage)  // Resmi ImageView'de göster
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()  // Geri butonuna basıldığında aktif edilen işlem
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
