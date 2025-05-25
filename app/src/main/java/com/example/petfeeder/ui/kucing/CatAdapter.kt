package com.example.petfeeder.ui.kucing

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R

class CatAdapter(
    private val context: Context,
    private var catList: MutableList<CatProfile>,
    private val onDeleteClicked: (CatProfile) -> Unit
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
            AlertDialog.Builder(context).apply {
                setTitle("Hapus Profil Kucing")
                setMessage("Yakin ingin menghapus profil \"${cat.name}\"?")
                setPositiveButton("Ya") { _, _ ->
                    onDeleteClicked(cat)
                }
                setNegativeButton("Batal", null)
                show()
            }
        }
    }

    override fun getItemCount(): Int = catList.size
}
