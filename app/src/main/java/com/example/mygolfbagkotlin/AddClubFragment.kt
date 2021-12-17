package com.example.mygolfbagkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AddClubFragment : Fragment() {
    lateinit var buttonCamera: Button
    lateinit var buttonPhoto: Button
    lateinit var editTextBrand: EditText
    lateinit var editTextLoft: EditText
    lateinit var editTextAddInfo: EditText
    lateinit var editTextYards: EditText
    lateinit var adapterType: ArrayAdapter<CharSequence>
    lateinit var adapterFlex: ArrayAdapter<CharSequence>
    lateinit var textView: TextView
    lateinit var textView2: TextView
    lateinit var textView3: TextView
    lateinit var textViewImg: TextView
    lateinit var spinnerType: Spinner
    lateinit var spinnerFlex: Spinner
    lateinit var toggleButton: ToggleButton
    lateinit var imageView: ImageView
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var detailActivity: DetailActivity
    lateinit var mainActivity: MainActivity
    lateinit var cursor: Cursor
    lateinit var selectedImage: Uri
    var fileName: String? = null
    lateinit var user: User
    var tablet = false
    var update = false
    var skip = false
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
            if (check()) {
                val myAsyncTask = MyAsyncTask()
                myAsyncTask.execute()
                val mp: MediaPlayer = MediaPlayer.create(context, R.raw.golfball)
                mp.start()
            }
        } else if (item.itemId == R.id.delete) {
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.golfball)
            mp.start()
            if (tablet) {
                mainActivity.onBackPressed()
            } else {
                val intent = Intent()
                detailActivity.setResult(Activity.RESULT_CANCELED, intent)
                detailActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_add_club, container, false)
        try {
            mainActivity = activity as MainActivity
            sqLiteDatabase = mainActivity.myBagAdapter.sqLiteDatabase
            tablet = true
            pos = mainActivity.pos
            user = mainActivity.myBagAdapter.user
        } catch (e: Exception) {
            detailActivity = activity as DetailActivity
            sqLiteDatabase = detailActivity.sqLiteDatabase
            pos = detailActivity.pos
            user = detailActivity.user
        }
        update = pos >= 0
        val mp: MediaPlayer = MediaPlayer.create(context, R.raw.swing)
        mp.start()
        buttonCamera = view.findViewById(R.id.buttonCamera)
        buttonPhoto = view.findViewById(R.id.buttonPhoto)
        imageView = view.findViewById(R.id.imageViewPreview)
        textViewImg = view.findViewById(R.id.textViewImg)
        editTextBrand = view.findViewById(R.id.editTextBrand)
        editTextLoft = view.findViewById(R.id.editTextLoft)
        textView = view.findViewById(R.id.textView3)
        editTextAddInfo = view.findViewById(R.id.editTextAddInfo)
        editTextYards = view.findViewById(R.id.editTextYards)
        textView2 = view.findViewById(R.id.textView6)
        spinnerType = view.findViewById(R.id.spinnerType)
        spinnerFlex = view.findViewById(R.id.spinnerFlex)
        textView3 = view.findViewById(R.id.textView5)
        toggleButton = view.findViewById(R.id.toggleButtonShaft)
        adapterFlex = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.flexs, android.R.layout.simple_spinner_item
        )
        adapterFlex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFlex.adapter = adapterFlex
        adapterType = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.types, android.R.layout.simple_spinner_item
        )
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapterType
        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                editTextYards.visibility = View.VISIBLE
                editTextLoft.visibility = View.VISIBLE
                spinnerFlex.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                textView2.visibility = View.VISIBLE
                textView3.visibility = View.VISIBLE
                skip = false
                when (position) {
                    1 -> editTextLoft.setText("10.5")
                    3 -> editTextLoft.setText("15")
                    4 -> editTextLoft.setText("19")
                    5, 6 -> editTextLoft.setText("21")
                    7 -> editTextLoft.setText("27")
                    8, 9 -> editTextLoft.setText("18")
                    10 -> editTextLoft.setText("21")
                    11 -> editTextLoft.setText("24")
                    12 -> editTextLoft.setText("27")
                    13 -> editTextLoft.setText("31")
                    14 -> editTextLoft.setText("35")
                    15 -> editTextLoft.setText("38")
                    16 -> editTextLoft.setText("42")
                    17, 18 -> editTextLoft.setText("46")
                    19 -> editTextLoft.setText("52")
                    20 -> editTextLoft.setText("56")
                    21 -> editTextLoft.setText("60")
                    22 -> {
                        editTextYards.visibility = View.GONE
                        editTextLoft.visibility = View.GONE
                        spinnerFlex.visibility = View.GONE
                        textView.visibility = View.GONE
                        textView2.visibility = View.GONE
                        textView3.visibility = View.GONE
                        skip = true
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        buttonCamera.setOnClickListener(View.OnClickListener {
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, )
            startActivityForResult(takePicture, 0)
        })
        buttonPhoto.setOnClickListener(View.OnClickListener {
            val action: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent.ACTION_OPEN_DOCUMENT
            } else {
                Intent.ACTION_PICK
            }
            val pickPhoto = Intent(
                action,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, 1)
        })
        if (update) setUp()
        return view
    }

    @SuppressLint("Range", "Recycle")
    private fun setUp() {
        try {
            cursor = sqLiteDatabase.query(TABLE, arrayOf(TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER),
                "$OWNER = ${user.id}", null, null, null, "$YARDS DESC", null)
            cursor.moveToPosition(pos)
            editTextBrand.setText(cursor.getString(cursor.getColumnIndex(BRAND)))
            editTextLoft.setText(cursor.getString(cursor.getColumnIndex(LOFT)))
            editTextAddInfo.setText(cursor.getString(cursor.getColumnIndex(DESC)))
            editTextYards.setText(cursor.getString(cursor.getColumnIndex(YARDS)))
            var i: Int
            i = 0
            while (i < adapterType.count) {
                if (cursor.getString(cursor.getColumnIndex(TYPE)) == adapterType.getItem(i)) break
                i++
            }
            spinnerType.setSelection(i)
            val shaft = cursor.getString(cursor.getColumnIndex(SHAFT)) == "Steel"
            toggleButton.isSelected = shaft
            i = 0
            while (i < adapterFlex.count) {
                if (cursor.getString(cursor.getColumnIndex(FLEX)) == adapterFlex.getItem(i)) break
                i++
            }
            if (i == adapterFlex.count) i = 0
            spinnerFlex.setSelection(i)
            try {
                fileName = cursor.getString(cursor.getColumnIndex(IMAGE))
                val bitmap = BitmapFactory.decodeFile(fileName)
                imageView.setImageBitmap(bitmap)
                textViewImg.visibility = View.GONE
                imageView.visibility = View.VISIBLE
            } finally { }
        } catch (e: CursorIndexOutOfBoundsException) {
            update = false
        }
    }

    private inner class MyAsyncTask : AsyncTask<Int?, Double?, String?>() {

        @SuppressLint("NotifyDataSetChanged")
        override fun onPostExecute(s: String?) {
            val mp: MediaPlayer = MediaPlayer.create(context, R.raw.golfball)
            mp.start()
            if (tablet) {
                mainActivity.myBagAdapter.notifyDataSetChanged()
                if (mainActivity.setOn && mainActivity.myBagAdapter.myBagDatabaseHelper.count > 14
                ) mainActivity.textView.visibility = View.VISIBLE
                mainActivity.onBackPressed()
            } else {
                val intent = Intent()
                detailActivity.setResult(Activity.RESULT_CANCELED, intent)
                detailActivity.finish()
            }
            super.onPostExecute(s)
        }

        @SuppressLint("Range", "Recycle")
        override fun doInBackground(vararg p0: Int?): String? {
            val contentValues = ContentValues()
            contentValues.put(TYPE, spinnerType.selectedItem.toString())
            contentValues.put(BRAND, editTextBrand.text.toString())
            contentValues.put(SHAFT, toggleButton.text.toString())
            contentValues.put(DESC, editTextAddInfo.text.toString())
            if (skip) {
                contentValues.put(LOFT, 0)
                contentValues.put(YARDS, 0)
                contentValues.put(FLEX, "Putter")
            } else {
                contentValues.put(FLEX, spinnerFlex.selectedItem.toString())
                contentValues.put(YARDS, Integer.valueOf(editTextYards.text.toString()))
                contentValues.put(LOFT, java.lang.Float.valueOf(editTextLoft.text.toString()))
            }
            contentValues.put(OWNER, user.id)
            contentValues.put(IMAGE, fileName)
            if (update) {
                cursor = sqLiteDatabase.query(TABLE, arrayOf(TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER),
                    OWNER + " = " + user.id, null, null, null, "$YARDS DESC", null)
                cursor.moveToPosition(pos)
                sqLiteDatabase.update(
                    TABLE, contentValues, "$TABLE_ID= ?",
                    arrayOf(cursor.getString(cursor.getColumnIndex(TABLE_ID))))
            } else {
                sqLiteDatabase.insert(TABLE, null, contentValues)
            }
            return null
        }
    }

    @SuppressLint("SetTextI18n")
    private fun check(): Boolean {
        if (editTextBrand.text.toString() == "") editTextBrand.setText("Not Specified")
        if (editTextYards.text.toString().trim().isEmpty()) editTextYards.setText("0")
        if (editTextLoft.text.toString().trim().isEmpty()) editTextLoft.setText("0")
        return true
    }

    @SuppressLint("SimpleDateFormat")
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        try {
            fileName = "${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_club.png"
            var bitmap: Bitmap? = null
            when (requestCode) {
                0 -> if (resultCode == Activity.RESULT_OK) {
                    Log.d("here", "onActivityResult: ${imageReturnedIntent!!.data}")
                    selectedImage = imageReturnedIntent?.data!!
                    bitmap = imageReturnedIntent?.extras?.get("data") as Bitmap?
                }
                1 -> if (resultCode == Activity.RESULT_OK) {
                    selectedImage = imageReturnedIntent!!.data!!
                    try {
                        val `is` = requireActivity().contentResolver.openInputStream(selectedImage)
                        if (`is` != null) {
                            bitmap = BitmapFactory.decodeStream(`is`)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val fileOutputStream = requireActivity().openFileOutput(fileName, Context.MODE_PRIVATE)
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            textViewImg.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            fileName = requireContext().getFileStreamPath(fileName).absolutePath
            val bm = BitmapFactory.decodeFile(fileName)
            imageView.setImageBitmap(bm)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        const val IMAGE = MyBagDatabaseHelper.IMAGE
        const val OWNER: String = MyBagDatabaseHelper.OWNER
    }
}