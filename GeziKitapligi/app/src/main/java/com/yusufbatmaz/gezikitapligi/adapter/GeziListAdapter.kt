package com.yusufbatmaz.gezikitapligi.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide // Glide kullanarak resimleri yüklemek için
import com.yusufbatmaz.gezikitapligi.R
import com.yusufbatmaz.gezikitapligi.models.GeziModel

class GeziListAdapter(
    private val context: Activity,
    private val list: List<GeziModel>
) : ArrayAdapter<GeziModel>(context, R.layout.geziler_list, list) {

    // ViewHolder sınıfını kullanarak görünüm yeniden kullanılabilir
    private class ViewHolder(view: View) {
        val rTitle: TextView = view.findViewById(R.id.r_title)
        val rDate: TextView = view.findViewById(R.id.r_date)
        val rImage: ImageView = view.findViewById(R.id.r_image)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val rootView: View

        // ConvertView varsa, onu tekrar kullanıyoruz, yoksa yeni bir görünüm oluşturuyoruz
        if (convertView == null) {
            rootView = context.layoutInflater.inflate(R.layout.geziler_list, parent, false)
            holder = ViewHolder(rootView)
            rootView.tag = holder
        } else {
            rootView = convertView
            holder = rootView.tag as ViewHolder
        }

        // Liste elemanını alıyoruz
        val note = list[position]

        // Verileri görünümde gösteriyoruz
        holder.rTitle.text = note.Title
        holder.rDate.text = note.Date

        // Glide ile resim yüklüyoruz
        if (note.ImageUri.isNotEmpty()) {
            Glide.with(context)
                .load(note.ImageUri)  // URI'den resmi yükler
                .into(holder.rImage)
        } else {
            holder.rImage.setImageResource(R.drawable.ic_launcher_background)  // Eğer resim yoksa varsayılan bir resim kullan
        }

        return rootView
    }
}
