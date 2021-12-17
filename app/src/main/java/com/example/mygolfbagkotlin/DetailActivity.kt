package com.example.mygolfbagkotlin

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class DetailActivity : AppCompatActivity() {
    var pos = 0
    lateinit var user: User
    lateinit var sqLiteDatabase: SQLiteDatabase
    lateinit var myBagDatabaseHelper: MyBagDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val intent = intent
        myBagDatabaseHelper = MyBagDatabaseHelper(applicationContext)
        sqLiteDatabase = myBagDatabaseHelper.readableDatabase
        val id = intent.getIntExtra("id", 0)
        pos = intent.getIntExtra("pos", -1)
        user = intent.getSerializableExtra("user") as User
        val fragment: Fragment = when (id) {
            -1 -> ViewFragment()
            R.id.addClub -> AddClubFragment()
            else -> LoginFragment()
        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutDetail, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commit()
    }
}