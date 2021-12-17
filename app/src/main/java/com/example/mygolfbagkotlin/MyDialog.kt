package com.example.mygolfbagkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment

class MyDialog(private var position: Int, private var tablet: Boolean) : DialogFragment() {
    lateinit var user: User
    @SuppressLint("Range", "Recycle", "NotifyDataSetChanged")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle("ARE YOU SURE?")
            .setMessage("By clicking yes you will delete this club from your bag. This can NOT be undone")
            .setNegativeButton(
                "No, don't remove"
            ) { _, _ -> }
            .setPositiveButton(
                "Yes, remove club"
            ) { _, _ ->
                user = if (tablet) {
                    (activity as MainActivity).user
                } else {
                    (activity as DetailActivity).user
                }
                val myBagDatabaseHelper = MyBagDatabaseHelper(context)
                val sqLiteDatabase: SQLiteDatabase = myBagDatabaseHelper.readableDatabase
                val cursor = sqLiteDatabase.query(TABLE, arrayOf(TABLE_ID, TYPE, LOFT, BRAND, SHAFT, FLEX, YARDS, DESC, IMAGE, OWNER),
                    "$OWNER = ${user.id}", null, null, null, "$YARDS DESC", null)
                cursor.moveToPosition(position)
                sqLiteDatabase.delete(
                    TABLE,
                    "$TABLE_ID=?",
                    arrayOf(
                        cursor.getInt(cursor.getColumnIndex(TABLE_ID)).toString()
                    )
                )
                if (tablet) {
                    val mainActivity = activity as MainActivity
                    mainActivity.myBagAdapter.notifyDataSetChanged()
                    if (mainActivity.setOn && mainActivity.myBagAdapter.myBagDatabaseHelper.count > 14)
                        mainActivity.textView.visibility = View.VISIBLE
                    else mainActivity.textView.visibility = View.GONE
                    mainActivity.onBackPressed()
                } else {
                    val intent = Intent()
                    requireActivity().setResult(Activity.RESULT_CANCELED, intent)
                    requireActivity().finish()
                }
                val mp: MediaPlayer = MediaPlayer.create(context, R.raw.golfball)
                mp.start()
            }
            .create()
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