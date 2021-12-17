package com.example.mygolfbagkotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("NotifyDataSetChanged")
class MainActivity : AppCompatActivity(), MyBagAdapter.Listener {
    var frameLayout: FrameLayout? = null
    lateinit var recyclerView: RecyclerView
    lateinit var textView: TextView
    lateinit var myBagAdapter: MyBagAdapter
    var setOn = true
    var pos = -1
    lateinit var user: User
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Settings")
            builder.setMessage("By clicking yes you will turn off the notification for having more than 14 clubs in your bag which can lead to unwanted strokes.")
            val yes: String
            val no: String
            if (setOn) {
                yes = "Off please"
                no = "It can stay"
            } else {
                yes = "Keep off"
                no = "Back on"
            }
            builder.setPositiveButton(
                yes
            ) { _, _ ->
                setOn = false
                textView.visibility = View.GONE
                val mp: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.golfball)
                mp.start()
            }
            builder.setNegativeButton(
                no
            ) { _, _ ->
                setOn = true
                if (myBagAdapter.myBagDatabaseHelper.count > 14) textView.visibility = View.VISIBLE
                val mp: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.swing)
                mp.start()
            }
            builder.show()
        } else {
            if (frameLayout == null) {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("id", item.itemId)
                intent.putExtra("pos", -1)
                intent.putExtra("user", user)
                startActivityForResult(intent, 1)
            } else {
                var fragment: Fragment? = null
                if (item.itemId == R.id.addClub) fragment =
                    AddClubFragment() else if (item.itemId == R.id.login) fragment = LoginFragment()
                if (fragment != null) {
                    val fragmentManager = this.supportFragmentManager
                    for (i in 0 until fragmentManager.backStackEntryCount) {
                        fragmentManager.popBackStack()
                    }
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayout, fragment)
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                user = intent!!.getSerializableExtra("user") as User
                myBagAdapter.user = user
                myBagAdapter.myBagDatabaseHelper.owner = user.id
                myBagAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onResume() {
        if (setOn && myBagAdapter.myBagDatabaseHelper.count > 14) textView.visibility = View.VISIBLE
        else textView.visibility = View.GONE
        myBagAdapter.notifyDataSetChanged()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        user = User(null, null, null, -1)
        frameLayout = findViewById(R.id.frameLayout)
        textView = findViewById(R.id.textViewLimit)
        recyclerView = findViewById(R.id.recyclerView)
        myBagAdapter = MyBagAdapter(applicationContext, user)
        recyclerView.adapter = myBagAdapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        myBagAdapter.setListener(this)
        myBagAdapter.notifyDataSetChanged()
    }

    override fun onClick(position: Int) {
        pos = position
        if (frameLayout == null) {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("pos", position)
            intent.putExtra("id", -1)
            intent.putExtra("user", user)
            startActivityForResult(intent, 1)
        } else {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, ViewFragment())
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}