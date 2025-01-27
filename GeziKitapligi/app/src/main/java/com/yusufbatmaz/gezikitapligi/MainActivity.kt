package com.yusufbatmaz.gezikitapligi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yusufbatmaz.gezikitapligi.adapter.GeziListAdapter
import com.yusufbatmaz.gezikitapligi.models.GeziModel

class MainActivity : AppCompatActivity() {
    lateinit var listNotes: ListView
    lateinit var db: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Veritabanı bağlantısı
        db = DB(this)

        // ListView'i bulalım
        listNotes = findViewById(R.id.listNotes)
        val arama_kutusu = findViewById<SearchView>(R.id.arama)



        // Fab butonuna tıklama olayını ekleyelim
        val fabAddNote = findViewById<FloatingActionButton>(R.id.fabAddNote)
        fabAddNote.setOnClickListener {
            try {
                // Yeni ekrani aç
                val intent = Intent(this, GeziEklemeActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()  // Hata mesajını logcat'e yazdırır
                Toast.makeText(this, "Bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Veritabanından notları çekelim ve ListView'e gösterelim
        val notesList = db.showNotes()  // Veritabanındaki notları al

        // Adapter ile notları ListView'e ekleyelim
        var customAdapter = GeziListAdapter(this, notesList)
        listNotes.adapter = customAdapter

        // ListView item'ına tıklama işlemi
        listNotes.setOnItemClickListener { _, _, position, _ ->
            val selectedNote = listNotes.getItemAtPosition(position) as GeziModel
            val intent = Intent(this, GeziDetayActivity::class.java)
            intent.putExtra("Title", selectedNote.Title)
            intent.putExtra("Detail", selectedNote.Detail)
            intent.putExtra("Date", selectedNote.Date)
            intent.putExtra("NID", selectedNote.NID)
            intent.putExtra("ImageUri", selectedNote.ImageUri)
            startActivity(intent)
        }
        fun filterList(query: String?) {
            val filteredList = notesList.filter {
                it.Title.contains(query ?: "", ignoreCase = true)
            }
            customAdapter = GeziListAdapter(this, filteredList)
            listNotes.adapter = customAdapter
        }
        arama_kutusu.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Arama işlemi yapılınca tetikleniyor
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Arama metni değiştikçe, listeleri filtrele
                newText?.let {
                    val filteredNotes = db.searchNotes(it)  // Veritabanında arama yap
                    customAdapter = GeziListAdapter(this@MainActivity, filteredNotes)
                    listNotes.adapter = customAdapter
                }
                return true
            }

        })

    }


    // Eğer activity sonucu dönerse, ListView'i tekrar güncellemek isteyebiliriz
    override fun onResume() {
        super.onResume()

        // Veritabanından güncellenmiş notları al
        val notesList = db.showNotes()

        // Adapter ile notları ListView'e ekleyelim
        val customAdapter = GeziListAdapter(this, notesList)
        listNotes.adapter = customAdapter

        // Listeyi güncellerken adapter'ı notifyDataSetChanged() ile yenileyebilirsiniz
        customAdapter.notifyDataSetChanged()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // Listeyi güncelle
            onResume()  // veya listeyi tekrar yükleyin
        }
    }


}
