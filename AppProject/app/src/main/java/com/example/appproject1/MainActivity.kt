package com.example.appproject1

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.util.*

// 11월 25일 DB 데이터 저장 변수 추가
var dbUserLimit : Int = 0
var dbUserSum : Int = 0
var thisMonth = 0


class MainActivity : AppCompatActivity() {

    lateinit var optionButton : ImageButton
    lateinit var mainImageView : ImageView
    lateinit var calendarButton : ImageButton
    lateinit var writeButton : ImageButton
    lateinit var galleryButton : ImageButton

    // 11월 19일 DB 추가
    lateinit var databaseMain: AppDatabase
    lateinit var sqlDB :SQLiteDatabase

    // 12월 1일 메인 배경 이미지 ID 변수
    var mainImage : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 연결
        optionButton = findViewById(R.id.optionBtn)
        mainImageView = findViewById(R.id.mainImageView)
        calendarButton = findViewById(R.id.calBtn)
        writeButton = findViewById(R.id.writeBtn)
        galleryButton = findViewById(R.id.galBtn)

        // ---------------------------------------------------------
        // DB 연결
        var gachaLength = 0

        databaseMain = AppDatabase(this)
        // DB 불러오기 -> 생성되어 있지 않으면 onCreate 실행됨
        // 11월 25일 : DB에서 소비한도, 이번달 사용량 가져오기
        sqlDB = databaseMain.readableDatabase
        var c : Cursor
        c = sqlDB.rawQuery("SELECT * FROM userData",null)
        //gachaLength = c.count
        //Log.i("myApp","Gacha DB Length : $gachaLength")
        var userDataCheck = c.count
        // 초기설정 -------------------------------------------------
        if(userDataCheck == 0){
            sqlDB =databaseMain.writableDatabase
            // 사용자의 초기 한도는 50만원, 사용금액은 0원
            sqlDB.execSQL("INSERT INTO userData (userLimit, userSum) VALUES(500000,0)")
            sqlDB.execSQL("INSERT INTO mainImage (imgId) VALUES('peng1')")
            Log.i("myApp","Main - DB initial Data Created")
        }

        // 12월 1일 배경 펭귄 이미지 설정 -> DB 사용 => onStart()

        sqlDB.close()
        c.close()

        // 앱을 구동했을때 데이터 가져오기
        updateSum(databaseMain)

        //  인텐트 ------------------------------------------------------

        // 11월 30일 Gallery에서 Main화면의 이미지 변경을 위해 intent 사용
        // 12월 1일 - DB 처리로 변경

        // 11월 30일 DB 초기설정 방법 변경
        /*
        if(gachaLength < 9) {
            Log.i("myApp","Initial Gacha data generate")
            sqlDB =databaseMain.writableDatabase
            for (i: Int in 1..9) {
                var name: String = "peng" + i
                sqlDB.execSQL(
                    "INSERT OR IGNORE INTO getGacha (gachaId, valid) VALUES('"
                            + name + "'," + "0 );"
                )
            }
            //Log.i("DB","DB onCreate - INSERT getGacha finish")
            // 사용자의 초기 한도는 50만원, 사용금액은 0원
            sqlDB.execSQL("INSERT OR IGNORE INTO userData (userLimit, userSum) VALUES(500000,0)")
            //Log.d("DB","DB onCreate - INSERT userData finish")
        } */

        // ------------------------------------------------------------

        // 리스너
        optionButton.setOnClickListener {

            var intent = Intent(this, OptionActivity::class.java)
            intent.putExtra("userLimit",dbUserLimit)
            // 옵션 버튼 -> 팝업 메뉴 -> 팝업에서 데이터 가져오기 : startActivityForResult로 변경
            startActivityForResult(intent,0)
        }

        calendarButton.setOnClickListener {

            var intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        writeButton.setOnClickListener {

            var intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }

        galleryButton.setOnClickListener {

            var intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onStart(){
        super.onStart()
        // 11월 25일 : DB 데이터 -> 메인화면 변경
        // onStart() 실행시 DB의 사용자 한도, 합계를 불러온다. [ 처음 실행시 + 다른 엑티비티에서 넘어올때 onStart() 호출 ]
        var uData = getLimitSum(databaseMain)
        dbUserLimit = uData[0]
        dbUserSum = uData[1]
        limitCheck(dbUserLimit,dbUserSum)
        setMainImage(databaseMain)
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("myApp","MainActivity onRestart()")
    }

    //    -----------------------------------------------------------------------------------------
    var backPressTime : Long = 0

    // 11월 5일 추가 - 안드로이드 뒤로가기 버튼
    // 메인화면에서 뒤로가기버튼 누르면 -> "뒤로가기 버튼을 한번더 누르면 종료됩니다" -> 한번 더 누르면 앱 종료
    override fun onBackPressed() {
        // 만약 super.onBackPressed()로 상속받으면, 기존 뒤로가기 버튼 기능이 동작 -> 앱 바로 꺼짐
        var firstPressTime : Long = System.currentTimeMillis()
        var interval = firstPressTime - backPressTime

        if(interval < 2000){
            finish()
            return
        }
        Toast.makeText(applicationContext,"한 번더 누르면 종료됩니다",Toast.LENGTH_SHORT).show()
        backPressTime = firstPressTime
    }

    // 11월 22일 : OptionActivity 에서 전달 받은값을 결과로 받음(Result)
    // 11월 25일 : OptionActivity 에서 DB에 직접 저장하도록 변경, Result는 유지
    // RESULT_OK ==> 한도가 변경되었음
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        //super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            Log.i("myApp","Intent : move from Option to Main / userLimit Updated")
            var uData = getLimitSum(databaseMain)
            dbUserLimit = uData[0]
            dbUserSum = uData[1]
            limitCheck(dbUserLimit,dbUserSum)
        }
        else{
            Log.i("myApp","Intent : move from Option to Main / no Update")
        }
    }

    // 11월 25일 소비한도와 사용금액에 따라서 배경화면 변경
    fun limitCheck(limit : Int, sum : Int){
        var layout : RelativeLayout
        layout = findViewById(R.id.MainActivity)
        if(limit < sum){
            // 메인화면 모두 색상 변경

            optionButton = findViewById(R.id.optionBtn)
            mainImageView = findViewById(R.id.mainImageView)
            calendarButton = findViewById(R.id.calBtn)
            writeButton = findViewById(R.id.writeBtn)
            galleryButton = findViewById(R.id.galBtn)

            layout.setBackgroundResource(R.drawable.app_backgroundnon)

            var matrix = ColorMatrix()
            matrix.setSaturation(0f)
            var filter = ColorMatrixColorFilter(matrix)

            mainImageView.setColorFilter(filter)
            var calImage = ContextCompat.getDrawable(this,R.drawable.button_calendar)
            calImage!!.setColorFilter(filter)
            var writeImage = ContextCompat.getDrawable(this,R.drawable.button_write)
            writeImage!!.setColorFilter(filter)
            var galImage = ContextCompat.getDrawable(this,R.drawable.button_gallery)
            galImage!!.setColorFilter(filter)
            var optImage = ContextCompat.getDrawable(this,R.drawable.setting_white)
            optImage!!.setColorFilter(filter)


            Log.i("myApp","userLimit $limit < sum $sum")
        }
        else{
            layout.setBackgroundResource(R.drawable.app_background)
            mainImageView.setColorFilter(null)
            var calImage = ContextCompat.getDrawable(this,R.drawable.button_calendar)
            calImage!!.setColorFilter(null)
            var writeImage = ContextCompat.getDrawable(this,R.drawable.button_write)
            writeImage!!.setColorFilter(null)
            var galImage = ContextCompat.getDrawable(this,R.drawable.button_gallery)
            galImage!!.setColorFilter(null)
            var optImage = ContextCompat.getDrawable(this,R.drawable.setting_white)
            optImage!!.setColorFilter(null)

            Log.i("myApp","userLimit $limit > sum $sum")
        }
    }
    // 12월 2일
    fun setMainImage(AppDB: AppDatabase){

        var sql : SQLiteDatabase = AppDB.writableDatabase
        var c : Cursor
        c = sql.rawQuery("SELECT * FROM mainImage",null)
        c.moveToFirst()
        mainImage = c.getInt(0)
        mainImageView.setImageResource(mainImage)
        Log.i("myApp","Main - Set Main Image $mainImage")

    }

    // 클래스 내부

}

// --------------------------------------- DB ----------------------------------------
// 11월19일 DB 클래스 추가
class AppDatabase(context: Context) : SQLiteOpenHelper(context, "AppDB", null, 1) {

    override fun onCreate(p0: SQLiteDatabase?) {
        Log.d("myApp","DB onCreate")
        // 테이블 생성
        p0!!.execSQL("CREATE TABLE writeData ( dataId INTEGER PRIMARY KEY AUTOINCREMENT, year INTEGER, month INTEGER, day INTEGER, category CHAR(10), content CHAR(20), price INTEGER);")
        p0!!.execSQL("CREATE TABLE getGacha( gachaId CHAR(10) )")
        p0!!.execSQL("CREATE TABLE userData ( userLimit INTEGER PRIMARY KEY, userSum INTEGER)")
        p0!!.execSQL("CREATE TABLE mainImage (imgId INTEGER)")
        Log.d("myApp","DB onCreate - CREATE TABLE finish")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS writeData")
        p0!!.execSQL("DROP TABLE IF EXISTS getGacha")
        p0!!.execSQL("DROP TABLE IF EXISTS userData")
        onCreate(p0)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        //Log.d("myApp","DB onOpen()")
    }
}

// -------------------------------------- Function ------------------------------------------
// 11월 25일 : Limit Sum 불러오는 함수 추가
fun getLimitSum(AppDB : AppDatabase) : Array<Int>{
    var limit = 0
    var sum = 0
    var sql :SQLiteDatabase
    sql = AppDB.readableDatabase
    var userDataColumn = arrayOf("userLimit","userSum")

    var c : Cursor = sql.rawQuery("SELECT * FROM userData",null)
    while(c.moveToNext()){
        limit = c.getInt(0)
        sum = c.getInt(1)
    }
    Log.i("myApp","DB userData : $limit , $sum")
    sql.close()
    var ret = arrayOf(limit,sum)
    return ret
}

// 11월 26일 추가
fun updateSum(AppDB: AppDatabase){

    thisMonth = LocalDate.now().monthValue

    var sql : SQLiteDatabase = AppDB.readableDatabase
    var c : Cursor = sql.rawQuery("SELECT SUM(price) FROM writeData WHERE month=$thisMonth",null)
    Log.i("myApp","Main - This month = $thisMonth")
    c.moveToFirst()
    var sum = c.getInt(0)
    Log.i("myApp","DB : Get SUM Price : $sum of this month : $thisMonth")
    sql.close()
    sql = AppDB.writableDatabase
    sql.execSQL("UPDATE userData SET userSum=$sum")
    Log.i("myApp","userData - userSum Updated : $sum")
}