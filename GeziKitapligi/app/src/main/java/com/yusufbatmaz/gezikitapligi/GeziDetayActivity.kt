package com.yusufbatmaz.gezikitapligi

import android.content.DialogInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide


class GeziDetayActivity : AppCompatActivity() {
    lateinit var btnDelete : Button
    lateinit var btnGuncelle: Button // Güncelle butonu
    lateinit var txtDetailTitle : EditText
    lateinit var img : ImageView
    lateinit var txtDetailDetail : EditText
    lateinit var txtDetailDate : TextView
    lateinit var db : DB
    lateinit var radioGuncelle: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gezi_detay)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Geri butonunu etkinleştiriyoruz
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        btnDelete = findViewById(R.id.btnDelete)
        btnGuncelle = findViewById(R.id.btnGuncelle) // Güncelle butonu
        txtDetailTitle = findViewById(R.id.txtDetailTitle)
        txtDetailDetail = findViewById(R.id.txtDetailDetail)
        txtDetailDate = findViewById(R.id.txtDetailDate)
        img=findViewById(R.id.imageView)
        radioGuncelle = findViewById(R.id.radioGuncelle)


        db = DB(applicationContext)

        txtDetailTitle.setText(intent.getStringExtra("Title"))
        txtDetailDetail.setText(intent.getStringExtra("Detail"))

        val imageUriString = intent.getStringExtra("ImageUri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            Log.d("ImageUri", imageUri.toString())  // URI'yi kontrol edin
            Glide.with(this.applicationContext)
                .load(imageUriString)  // URI'den resmi yükler
                .into(img) // // ImageView'e URI'yi ayarlayın
        } else {
            Log.e("Error", "ImageUri is null or empty.")
        }

        txtDetailDate.text = "Tarih : " + intent.getStringExtra("Date") + "     "
        val NID = intent.getIntExtra("NID",0)


        // Başlangıçta EditText yalnızca görüntülenebilir olacak
        txtDetailDetail.isFocusable = false
        txtDetailDetail.isFocusableInTouchMode = false
        txtDetailTitle.isFocusable = false
        txtDetailTitle.isFocusableInTouchMode = false

        // RadioButton durumunu kontrol et
        radioGuncelle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // RadioButton seçildiğinde, EditText yazılabilir hale gelsin
                txtDetailDetail.isFocusable = true
                txtDetailDetail.isFocusableInTouchMode = true
                txtDetailTitle.isFocusable = true
                txtDetailTitle.isFocusableInTouchMode = true

            } else {
                // RadioButton seçilmediyse, EditText readonly (yalnızca görüntülenebilir) kalsın
                txtDetailDetail.isFocusable = false
                txtDetailDetail.isFocusableInTouchMode = false
                txtDetailTitle.isFocusable = false
                txtDetailTitle.isFocusableInTouchMode = false
            }
        }

        btnGuncelle.setOnClickListener {
            val yeni_title = txtDetailTitle.text.toString()
            val yeni_detay = txtDetailDetail.text.toString()

            if (yeni_title.isNotEmpty() && yeni_detay.isNotEmpty()) {
                val NID = intent.getIntExtra("NID", 0)

                // Notu güncellemek için veritabanı metodunu çağırıyoruz
                val status = db.updateNote(NID, yeni_title, yeni_detay)

                if (status > 0) {
                    // Güncelleme başarılı
                    Toast.makeText(this, "Not güncellendi!", Toast.LENGTH_SHORT).show()
                    finish() // Bu activity'yi kapatıp, önceki activity'ye dönüyoruz
                } else {
                    // Güncelleme başarısız
                    Toast.makeText(this, "Bir hata oluştu, tekrar deneyin.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Eğer başlık veya detay boşsa, kullanıcıyı uyarıyoruz
                Toast.makeText(this, "Başlık ve Detay boş olamaz!", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            var alert = AlertDialog.Builder(this)
            alert.setMessage("Are you sure you wanna delete this note?")
            alert.setCancelable(false) // Bir yere tıklanıldığında iptal edilmesini önledim
            alert.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                Log.d("Cancel","Silmekten vazgeçildi")
            })
            alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                val status = db.deleteNote(NID)
                Log.d("status",status.toString())
                finish()
            })
            alert.show()
            // Notu sildikten sonra bu activity'de işimiz olmadığından sonlandırıyorum onResume tetikleniyor MainActivityde
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