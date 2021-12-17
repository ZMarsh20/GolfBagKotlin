package com.example.mygolfbagkotlin

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.lang.Exception

class ViewFragment : Fragment() {
    lateinit var textViewViewBrand: TextView
    lateinit var textViewViewLoft: TextView
    lateinit var textViewViewYards: TextView
    lateinit var textViewViewDesc: TextView
    lateinit var textViewViewFlex: TextView
    lateinit var textViewViewType: TextView
    lateinit var textViewViewShaft: TextView
    lateinit var imageView: ImageView
    lateinit var mainActivity: MainActivity
    lateinit var detailActivity: DetailActivity
    lateinit var sqLiteDatabase: SQLiteDatabase
    var tablet = false
    lateinit var cursor: Cursor
    var pos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.frag_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add) {
            val fragmentTransaction = requireFragmentManager().beginTransaction()
            if (tablet) fragmentTransaction.replace(
                R.id.frameLayout,
                AddClubFragment()
            ) else fragmentTransaction.replace(R.id.frameLayoutDetail, AddClubFragment())
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else if (item.itemId == R.id.delete) {
            val myDialog = MyDialog(pos, tablet)
            myDialog.show(requireFragmentManager(), "does this matter?")
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_view, container, false)
        val mp: MediaPlayer = MediaPlayer.create(context, R.raw.swing)
        mp.start()
        textViewViewBrand = view.findViewById(R.id.textViewViewBrand)
        textViewViewLoft = view.findViewById(R.id.textViewViewLoft)
        textViewViewYards = view.findViewById(R.id.textViewViewYards)
        textViewViewDesc = view.findViewById(R.id.textViewViewDesc)
        textViewViewFlex = view.findViewById(R.id.textViewViewFlex)
        textViewViewType = view.findViewById(R.id.textViewViewType)
        textViewViewShaft = view.findViewById(R.id.textViewViewShaft)
        imageView = view.findViewById(R.id.imageViewView)
        try {
            mainActivity = activity as MainActivity
            sqLiteDatabase = mainActivity.myBagAdapter.sqLiteDatabase
            tablet = true
        } catch (e: Exception) {
            detailActivity = activity as DetailActivity
            sqLiteDatabase = detailActivity.sqLiteDatabase
        }
        val user: User
        user = if (tablet) {
            mainActivity.user
        } else {
            detailActivity.user
        }
        cursor = sqLiteDatabase!!.query(TABLE, arrayOf(TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER),
            "$OWNER = ${user.id}", null, null, null, "$YARDS DESC", null)
        pos = if (tablet) {
            mainActivity.pos
        } else {
            detailActivity.pos
        }
        cursor.moveToPosition(pos)
        textViewViewBrand.text = cursor.getString(cursor.getColumnIndex(BRAND))
        textViewViewLoft.text = cursor.getString(cursor.getColumnIndex(LOFT))
        textViewViewYards.text = cursor.getString(cursor.getColumnIndex(YARDS))
        textViewViewDesc.text = cursor.getString(cursor.getColumnIndex(DESC))
        textViewViewFlex.text = cursor.getString(cursor.getColumnIndex(FLEX))
        textViewViewType.text = cursor.getString(cursor.getColumnIndex(TYPE))
        textViewViewShaft.text = cursor.getString(cursor.getColumnIndex(SHAFT))
        val fileName = cursor.getString(cursor.getColumnIndex(IMAGE))
        val bitmap = BitmapFactory.decodeFile(fileName)
        imageView.setImageBitmap(bitmap)
        return view
    }

    companion object {
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