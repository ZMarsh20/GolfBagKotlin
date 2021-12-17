package com.example.mygolfbagkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MyBagAdapter(var context: Context, var user: User) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var myBagDatabaseHelper: MyBagDatabaseHelper = MyBagDatabaseHelper(context)
    var sqLiteDatabase: SQLiteDatabase = myBagDatabaseHelper.readableDatabase
    private lateinit var listener: Listener

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    inner class MyViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(
        cardView
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.my_bag_card_view, parent, false)
        val cardView: CardView = view.findViewById(R.id.cardView)
        return MyViewHolder(cardView)
    }

    @SuppressLint("Range", "SetTextI18n", "Recycle")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val myViewHolder = holder as MyViewHolder
        val cardView = myViewHolder.cardView
        cursor = sqLiteDatabase.query(
            TABLE, arrayOf(TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER),
            "$OWNER = ${user.id}", null, null, null, "$YARDS DESC", null
        )
        cursor.moveToPosition(position)
        val imageView = cardView.findViewById<ImageView>(R.id.imageView)
        val textViewType = cardView.findViewById<TextView>(R.id.textViewCardType)
        val textViewYards = cardView.findViewById<TextView>(R.id.textViewCardYards)
        val textViewBrand = cardView.findViewById<TextView>(R.id.textViewCardBrand)
        val textViewLoft = cardView.findViewById<TextView>(R.id.textViewCardLoft)
        val textViewShaft = cardView.findViewById<TextView>(R.id.textViewCardShaft)
        val textViewFlex = cardView.findViewById<TextView>(R.id.textViewCardFlex)
        val yards = cursor.getInt(cursor.getColumnIndex(YARDS))
        val loft = cursor.getFloat(cursor.getColumnIndex(LOFT))
        val type = cursor.getString(cursor.getColumnIndex(TYPE))
        val flex = cursor.getString(cursor.getColumnIndex(FLEX))
        val shaft = cursor.getString(cursor.getColumnIndex(SHAFT))
        val brand = cursor.getString(cursor.getColumnIndex(BRAND))
        val fileName = cursor.getString(cursor.getColumnIndex(IMAGE))
        val bitmap = BitmapFactory.decodeFile(fileName)
        imageView.setImageBitmap(bitmap)
        textViewBrand.text = brand
        textViewFlex.text = flex
        textViewLoft.text = "$loft°"
        textViewShaft.text = shaft
        textViewType.text = type
        textViewYards.text = "$yards Yds"
        cardView.setOnClickListener { listener.onClick(position) }
    }

    override fun getItemCount(): Int {
        return myBagDatabaseHelper.count
    }

    companion object {
        lateinit var cursor: Cursor
        const val TABLE_ID: String = MyBagDatabaseHelper.TABLE_ID
        const val TABLE: String = MyBagDatabaseHelper.TABLE
        const val TYPE: String = MyBagDatabaseHelper.TYPE
        const val LOFT: String = MyBagDatabaseHelper.LOFT
        const val BRAND: String = MyBagDatabaseHelper.BRAND
        const val SHAFT: String = MyBagDatabaseHelper.SHAFT
        const val FLEX: String = MyBagDatabaseHelper.FLEX
        const val YARDS: String = MyBagDatabaseHelper.YARDS
        const val DESC: String = MyBagDatabaseHelper.DESC
        const val IMAGE: String = MyBagDatabaseHelper.IMAGE
        const val OWNER: String = MyBagDatabaseHelper.OWNER
    }

}