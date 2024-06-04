package com.example.appproject1

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception

class WriteActivity : AppCompatActivity() {

    // 선언
    lateinit var backbtn : ImageButton
    lateinit var text1 : TextView
    lateinit var updatebtn : ImageButton

    // 11월 26일 변수 추가
    lateinit var datePicker : DatePicker
    lateinit var edtCategory : EditText
    lateinit var edtContent : EditText
    lateinit var edtPrice : EditText

    // 11월 26일 DB 추가
    lateinit var databaseWrite: AppDatabase
    lateinit var sqlDB : SQLiteDatabase

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        // 12월 1일
        val display =
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val width = (display.width * 0.7).toInt() //Display 사이즈의 70%
        val height = (display.height * 0.8).toInt() //Display 사이즈의 90%
        window.attributes.width = width
        window.attributes.height = height

        // 날짜 변수 추가
        var writeYear : Int = 0
        var writeMonth : Int = 0
        var writeDay : Int = 0

        // ID
        backbtn = findViewById(R.id.backbtn)
        text1 = findViewById(R.id.writetext1)
        updatebtn = findViewById(R.id.updatebtn)
        datePicker = findViewById(R.id.datepicker)
        edtCategory = findViewById(R.id.editCategory)
        edtContent = findViewById(R.id.editContent)
        edtPrice = findViewById(R.id.editPrice)

//      ------------------------------------------------------------
        // DB
        databaseWrite = AppDatabase(this)

//      ------------------------------------------------------------
        // Listener
        backbtn.setOnClickListener{
            // 11월 5일 : Back 버튼을 기존 안드로이드 뒤로가기 버튼과 똑같은 기능하도록 설정
            onBackPressed()
        }

        updatebtn.setOnClickListener {

            // 버튼을 2번 연속으로 빠르게 눌렀을때 방지
            // 11월 26일
            writeYear = datePicker.year
            writeMonth = datePicker.month + 1
            writeDay = datePicker.dayOfMonth
            //Log.i("myApp","$writeDay $writeMonth $writeYear")

            var category : String = ""
            var content : String = ""
            var price : Int = 0
            try{
                category = edtCategory.text.toString()
                if(category.length==0){
                    Toast.makeText(applicationContext,"내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                    edtCategory.requestFocus()
                    return@setOnClickListener
                }
                content = edtContent.text.toString()
                if(content.length==0){
                    Toast.makeText(applicationContext,"내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                    edtContent.requestFocus()
                    return@setOnClickListener
                }
                price = edtPrice.text.toString().toInt()
            }catch (e : Exception){
                //Log.i("myApp","Error : $e")
                Toast.makeText(applicationContext,"숫자를 입력해주세요", Toast.LENGTH_SHORT).show()
                edtPrice.requestFocus()
                return@setOnClickListener
            }
            writeData(databaseWrite,writeYear,writeMonth,writeDay,category,content,price)
            Log.i("myApp","WriteActivity : $writeYear : $writeMonth : $writeDay | $category , $content , $price")


            var intent = Intent(this, UpdateActivity::class.java)
            startActivity(intent)
//            // 12월 1일 intent 변경
//            var intent1 = Intent(this,MainActivity::class.java)
//            var intent2 = Intent(this, UpdateActivity::class.java)
//            startActivity(intent1)
//            startActivity(intent2)
        }

    }
    // 12월 1일 팝업 기능 구현
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        // 팝업 창 바깥 터치시 창이 안닫히게
        if(event?.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false
        }
        return true
    }


}

// -------------------------------------- Function ------------------------------------------

fun writeData(AppDB : AppDatabase, year : Int, month : Int, day : Int, category : String, content : String, price : Int){
    var sql : SQLiteDatabase
    sql = AppDB.writableDatabase
    sql.execSQL("INSERT INTO writeData (year,month,day,category,content,price) VALUES($year,$month,$day,'$category','$content',$price)")
    updateSum(AppDB)
}