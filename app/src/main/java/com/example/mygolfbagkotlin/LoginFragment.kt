package com.example.mygolfbagkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.CursorIndexOutOfBoundsException
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.lang.Exception

@SuppressLint("NotifyDataSetChanged")
class LoginFragment : Fragment() {
    lateinit var editTextUsername: EditText
    lateinit var editTextPassword: EditText
    lateinit var progressBar: ProgressBar
    lateinit var button: Button
    lateinit var username: String
    lateinit var password: String
    var tablet = false
    lateinit var mainActivity: MainActivity
    lateinit var detailActivity: DetailActivity
    lateinit var handler: Handler
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var myBagDatabaseHelper: MyBagDatabaseHelper
    lateinit var user: User
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
            if (tablet) fragmentTransaction.replace(R.id.frameLayout, SignUpFragment())
            else fragmentTransaction.replace(R.id.frameLayoutDetail, SignUpFragment())
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else if (item.itemId == R.id.delete) {
            if (tablet) {
                mainActivity.onBackPressed()
            } else {
                val intent = Intent()
                intent.putExtra("user", user)
                detailActivity.setResult(Activity.RESULT_OK, intent)
                detailActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)
        try {
            mainActivity = activity as MainActivity
            tablet = true
            user = mainActivity.user
            if (user.id != -1) userSignedIn()
        } catch (e: Exception) {
            detailActivity = activity as DetailActivity
            user = detailActivity.user
            if (user.id != -1) userSignedIn()
        }
        myBagDatabaseHelper = MyBagDatabaseHelper(context)
        sqLiteDatabase = myBagDatabaseHelper.readableDatabase
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPassword = view.findViewById(R.id.editTextPassword)
        progressBar = view.findViewById(R.id.progressBar)
        button = view.findViewById(R.id.buttonSubmit)
        button.setOnClickListener(View.OnClickListener {
            username = editTextUsername.text.toString()
            password = editTextPassword.text.toString()
            check()
        })
        return view
    }

    fun check() {
        handler = object : Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    0 -> progressBar.visibility = View.VISIBLE
                    1 -> {
                        progressBar.visibility = View.GONE
                        editTextPassword.setBackgroundColor(Color.RED)
                        editTextUsername.setBackgroundColor(Color.RED)
                    }
                    2 -> {
                        if (tablet) {
                            mainActivity.user = user
                            mainActivity.myBagAdapter.user = user
                            mainActivity.myBagAdapter.notifyDataSetChanged()
                        }
                        progressBar.visibility = View.GONE
                        if (tablet) {
                            val fragmentManager = activity!!.supportFragmentManager
                            var i = 0
                            while (i < fragmentManager.backStackEntryCount) {
                                fragmentManager.popBackStack()
                                i++
                            }
                            mainActivity.myBagAdapter.myBagDatabaseHelper.owner = user.id
                            mainActivity.myBagAdapter.notifyDataSetChanged()
                        } else {
                            val intent = Intent()
                            intent.putExtra("user", user)
                            detailActivity.setResult(Activity.RESULT_OK, intent)
                            detailActivity.finish()
                        }
                    }
                }
            }
        }
        val thread = MyThread(username, password)
        thread.start()
    }

    inner class MyThread internal constructor(
        var username: String,
        var password: String
    ) :
        Thread() {
        @SuppressLint("Range", "Recycle")
        override fun run() {
            super.run()
            val cursor = sqLiteDatabase.query(USERTABLE, arrayOf(TABLE_ID, PASS, USER, NAME),
                null, null, null, null, null, null)
            handler.sendEmptyMessage(0)
            cursor.moveToFirst()
            try {
                do {
                    if (username == cursor.getString(cursor.getColumnIndex(USER))) {
                        if (password == cursor.getString(cursor.getColumnIndex(PASS))) {
                            handler.sendEmptyMessage(2)
                            user.username = cursor.getString(cursor.getColumnIndex(USER))
                            user.password = cursor.getString(cursor.getColumnIndex(PASS))
                            user.name = cursor.getString(cursor.getColumnIndex(NAME))
                            user.id = cursor.getInt(cursor.getColumnIndex(TABLE_ID))
                        } else handler.sendEmptyMessage(1)
                        break
                    }
                } while (cursor.moveToNext())
                if (!cursor.moveToNext() && user.id == -1) handler.sendEmptyMessage(1)
            } catch (e: CursorIndexOutOfBoundsException) {
                handler.sendEmptyMessage(1)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun userSignedIn() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        builder.setTitle("Log Out?")
        builder.setMessage(user.name.toString() + " is currently logged in. By clicking yes you will log out. Continue?")
        builder.setPositiveButton(
            "Log out"
        ) { _, _ ->
            val noCurrent = User(null, null, null, -1)
            if (tablet) {
                mainActivity.user = noCurrent
                mainActivity.myBagAdapter.user = mainActivity.user
                mainActivity.myBagAdapter.myBagDatabaseHelper.owner = mainActivity.user.id
                mainActivity.myBagAdapter.notifyDataSetChanged()
                val fragmentManager = requireActivity().supportFragmentManager
                for (j in 0 until fragmentManager.backStackEntryCount) {
                    fragmentManager.popBackStack()
                }
            } else {
                user = noCurrent
                val intent = Intent()
                intent.putExtra("user", user)
                detailActivity.setResult(Activity.RESULT_OK, intent)
                detailActivity.finish()
            }
        }
        builder.setNegativeButton(
            "Stay Logged in"
        ) { _, _ ->
            if (tablet) {
                mainActivity.onBackPressed()
            } else {
                val intent = Intent()
                detailActivity.setResult(Activity.RESULT_CANCELED, intent)
                detailActivity.finish()
            }
        }
        builder.show()
    }

    companion object {
        const val USERTABLE: String = MyBagDatabaseHelper.USERTABLE
        const val TABLE_ID: String = MyBagDatabaseHelper.TABLE_ID
        const val USER: String = MyBagDatabaseHelper.USER
        const val PASS: String = MyBagDatabaseHelper.PASS
        const val NAME: String = MyBagDatabaseHelper.NAME
    }
}