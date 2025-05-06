package com.example.petfeeder.ui.kucing

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CatAdapter(
    private val context: Context,
    private var catList: MutableList<CatProfile>
) : RecyclerView.Adapter<CatAdapter.CatViewHolder>() {

    class CatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCatName: TextView = itemView.findViewById(R.id.tvCatName)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cat_profile, parent, false)
        return CatViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val cat = catList[position]
        holder.tvCatName.text = "${cat.name} - ${cat.breed}"

        holder.btnDelete.setOnClickListener {
            // Show confirmation dialog
            AlertDialog.Builder(context).apply {
                setTitle("Hapus Profil Kucing")
                setMessage("Yakin ingin menghapus profil \"${cat.name}\"?")
                setPositiveButton("Ya") { _, _ ->
                    catList.removeAt(position)
                    saveCatList()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, catList.size)

                    Toast.makeText(
                        context,
                        "Profil \"${cat.name}\" telah dihapus.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setNegativeButton("Batal", null)
                show()
            }
        }
    }

    override fun getItemCount(): Int = catList.size

    private fun saveCatList() {
        val prefs: SharedPreferences = context.getSharedPreferences("cats", Context.MODE_PRIVATE)
        val json = Gson().toJson(catList)
        prefs.edit().putString("cat_list", json).apply()
    }

    companion object {
        fun loadCatList(context: Context): MutableList<CatProfile> {
            val prefs = context.getSharedPreferences("cats", Context.MODE_PRIVATE)
            val json = prefs.getString("cat_list", null)
            return if (json != null) {
                val type = object : TypeToken<MutableList<CatProfile>>() {}.type
                Gson().fromJson(json, type)
            } else {
                mutableListOf()
            }
        }
    }
}