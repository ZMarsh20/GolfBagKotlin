package com.example.mygolfbagkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.lang.Exception

@SuppressLint("NotifyDataSetChanged")
class SignUpFragment : Fragment() {
    lateinit var editTextUsername: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextConfirm: EditText
    lateinit var editTextName: EditText
    var tablet = false
    var fault = false
    lateinit var progressBar: ProgressBar
    lateinit var username: String
    lateinit var password: String
    lateinit var mainActivity: MainActivity
    lateinit var detailActivity: DetailActivity
    lateinit var handler: Handler
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var myBagDatabaseHelper: MyBagDatabaseHelper
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
            if (isGood) {
                username = editTextUsername.text.toString()
                password = editTextPassword.text.toString()
                check()
            } else {
                if (fault) {
                    editTextUsername.setBackgroundColor(Color.RED)
                } else {
                    editTextUsername.setBackgroundColor(Color.WHITE)
                    editTextPassword.setBackgroundColor(Color.RED)
                    editTextConfirm.setBackgroundColor(Color.RED)
                }
            }
        } else if (item.itemId == R.id.delete) {
            if (tablet) clearHistory()
            else {
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
        val view: View = inflater.inflate(R.layout.fragment_sign_up, container, false)
        try {
            mainActivity = activity as MainActivity
            tablet = true
        } catch (e: Exception) {
            detailActivity = activity as DetailActivity
        }
        myBagDatabaseHelper = MyBagDatabaseHelper(context)
        sqLiteDatabase = myBagDatabaseHelper.readableDatabase
        editTextConfirm = view.findViewById(R.id.editTextConfirmPassword)
        editTextName = view.findViewById(R.id.editTextPersonName)
        editTextPassword = view.findViewById(R.id.editTextPasswordSignUp)
        editTextUsername = view.findViewById(R.id.editTextUsernameSignup)
        progressBar = view.findViewById(R.id.progressBarSignUp)
        return view
    }

    val isGood: Boolean
        get() = if (editTextUsername.text.toString() == "") {
            fault = true
            false
        } else if (editTextPassword.text.toString() == "" || editTextConfirm.text.toString() == ""
            || editTextPassword.text.toString() != editTextConfirm.text.toString()) {
            fault = false
            false
        } else true

    private fun check() {
        handler = object : Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    0 -> progressBar!!.visibility = View.VISIBLE
                    1 -> {
                        progressBar!!.visibility = View.GONE
                        val builder = AlertDialog.Builder(
                            context!!
                        )
                        builder.setTitle("Uh oh")
                        builder.setMessage("Looks like that username is taken :(")
                        builder.show()
                    }
                    2 -> {
                        progressBar.visibility = View.GONE
                        val contentValues = ContentValues()
                        contentValues.put(USER, username)
                        contentValues.put(PASS, password)
                        val name = editTextName.text.toString()
                        contentValues.put(NAME, name)
                        sqLiteDatabase.insert(USERTABLE, null, contentValues)
                        val user =
                            User(username, password, name, myBagDatabaseHelper.countUsers)
                        if (tablet) {
                            mainActivity.user = user
                            mainActivity.myBagAdapter.user = user
                            mainActivity.myBagAdapter.myBagDatabaseHelper.owner = user.id
                            mainActivity.myBagAdapter.notifyDataSetChanged()
                        } else detailActivity.user = user
                        if (tablet) clearHistory() else {
                            val intent = Intent()
                            intent.putExtra("user", user)
                            detailActivity.setResult(Activity.RESULT_OK, intent)
                            detailActivity.finish()
                        }
                    }
                }
            }
        }
        val thread = MyThread(username)
        thread.start()
    }

    inner class MyThread internal constructor(
        var username: String
    ) :
        Thread() {
        @SuppressLint("Range", "Recycle")
        override fun run() {
            super.run()
            val cursor = sqLiteDatabase.query(
                USERTABLE, arrayOf(TABLE_ID, PASS, USER, NAME),
                null, null, null, null, null, null
            )
            handler.sendEmptyMessage(0)
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                if (username == cursor.getString(cursor.getColumnIndex(USER))) {
                    handler.sendEmptyMessage(1)
                    break
                }
            }
            if (!cursor.moveToNext()) handler.sendEmptyMessage(2)
        }
    }

    fun clearHistory() {
        val fragmentManager = requireActivity().supportFragmentManager
        for (i in 0 until fragmentManager.backStackEntryCount) {
            fragmentManager.popBackStack()
        }
    }

    companion object {
        const val USERTABLE: String = MyBagDatabaseHelper.USERTABLE
        const val TABLE_ID: String = MyBagDatabaseHelper.TABLE_ID
        const val USER: String = MyBagDatabaseHelper.USER
        const val PASS: String = MyBagDatabaseHelper.PASS
        const val NAME: String = MyBagDatabaseHelper.NAME
    }
}