package com.example.cobafirebase

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    var DataProvinsi = ArrayList<daftarProvinsi>()
    lateinit var lvAdapter: ArrayAdapter<daftarProvinsi>
    lateinit var _etProvinsi: EditText
    lateinit var _etIbukota: EditText

    var data: MutableList<Map<String, String>> = ArrayList()
    lateinit var _lvAdapter: SimpleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _etProvinsi = findViewById<EditText>(R.id.etProvinsi)
        _etIbukota = findViewById<EditText>(R.id.etIbukota)
        val btSimpan = findViewById<Button>(R.id.btSimpan)
        val _lvData = findViewById<ListView>(R.id.lvData)
//        lvAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_list_item_1,
//            DataProvinsi
//        )
//        _lvData.adapter = lvAdapter

        _lvAdapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf<String>("Pro", "Ibu"),
            intArrayOf(
                android.R.id.text1,
                android.R.id.text2
            )
        )
        _lvData.setOnItemLongClickListener() { parent, view, position, id ->
            val namaPro = data[position].get("Pro")
            if (namaPro != null) {
                db.collection("tbProvinsi")
                    .document(namaPro)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Firebase", "Berhasil Dihapus")
                        readData(db)
                    }
                    .addOnFailureListener { e ->
                        Log.d("Firebase", e.message.toString())
                    }
            }
            true
        }
        _lvData.adapter = _lvAdapter

        btSimpan.setOnClickListener {
            val Provinsi = _etProvinsi.text.toString()
            val Ibukota = _etIbukota.text.toString()
            TambahData(db, Provinsi, Ibukota)
            readData(db)
        }

    }

    fun TambahData(db: FirebaseFirestore, Provinsi: String, Ibukota: String) {
        val dataBaru = daftarProvinsi(Provinsi, Ibukota)
        db.collection("tbProvinsi")
            .document(dataBaru.provinsi)
            .set(dataBaru)
            .addOnSuccessListener {
                _etProvinsi.setText("")
                _etIbukota.setText("")
                Log.d("Firebase", "Data Berhasil Disimpan")
                readData(db)
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    fun readData(db: FirebaseFirestore) {
        db.collection("tbProvinsi").get()
            .addOnSuccessListener {
                result ->
                DataProvinsi.clear()
                for (document in result){
                    val readData = daftarProvinsi(
                        document.data.get("provinsi").toString(),
                        document.data.get("ibukota").toString()
                    )
                    DataProvinsi.add(readData)
                    data.clear()
                    DataProvinsi.forEach {
                        val dt: MutableMap<String, String> = HashMap(2)
                        dt["Pro"] = it.provinsi
                        dt["Ibu"] = it.ibukota
                        data.add(dt)
                    }
                }
//                lvAdapter.notifyDataSetChanged()
                _lvAdapter.notifyDataSetChanged()

            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }


}