package com.example.appproject1

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class UpdateActivity : AppCompatActivity() {

    lateinit var gachaLayout : RelativeLayout
    lateinit var gachaBtn : ImageButton
    lateinit var resultLayout : RelativeLayout
    lateinit var closeBtn : ImageButton
    val random = Random()

    // 11월 30일 DB 추가
    lateinit var databaseUpdate: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        gachaLayout = findViewById(R.id.gachalayout)
        gachaBtn = findViewById(R.id.gachaBtn)
        resultLayout = findViewById(R.id.resultlayout)
        closeBtn = findViewById(R.id.closeBtn)

        databaseUpdate = AppDatabase(this)

        gachaBtn.setOnClickListener {
            gachaLayout.visibility = View.GONE
            resultLayout.visibility = View.VISIBLE
            var resultId : String = playGacha()
            gachaDataCheck(databaseUpdate,resultId)


        }

        closeBtn.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            // 11월 5일 : intent로 이동할때 현재 Update -> 뽑기 결과 확인 -> Main화면 에서 뒤로가기 버튼시 Update로
            // 돌아오는것을 방지
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


    }

    // 11월 5일 : 결과 확인을 위한 창이 나왔을때 뒤로가기 버튼을 누르면 Write로 돌아가는것이 아니라
    // Main화면으로 돌아가도록 설정
    override fun onBackPressed() {
        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    //가챠 부분 11/28일
    // 29일 30일 수정
    fun playGacha() : String{
        val resultImg : ImageView= findViewById(R.id.resultImage)
        val resultTxt : TextView = findViewById(R.id.resultTxt)
        val resultNum : Int = random.nextInt(1000)
        var resultId : String = ""

        if (resultNum<=225){
            resultId = "peng1"
            resultTxt.setText("You get\nNomal\npenguin")
            resultImg.setImageResource(R.drawable.peng_basic)
        }
        if (225<resultNum&&resultNum<=450)
        {
            resultId="peng2"
            resultTxt.setText("You get\nNomal\npenguin")
            resultImg.setImageResource(R.drawable.peng_rain)
        }
        if (450<resultNum&&resultNum<=550)
        {
            resultId="peng3"
            resultTxt.setText("You get\nRare\npenguin")
            resultImg.setImageResource(R.drawable.peng_adelly)
        }
        if (550<resultNum&&resultNum<=650)
        {
            resultId="peng4"
            resultTxt.setText("You get\nRare\npenguin")
            resultImg.setImageResource(R.drawable.peng_ribbon)
        }
        if (650<resultNum&&resultNum<=750)
        {
            resultId="peng5"
            resultTxt.setText("You get\nRare\npenguin")
            resultImg.setImageResource(R.drawable.peng_plant)
        }
        if (750<resultNum&&resultNum<=825)
        {
            resultId="peng6"
            resultTxt.setText("You get\nSuper Rare\npenguin")
            resultImg.setImageResource(R.drawable.peng_egg)
        }
        if (825<resultNum&&resultNum <=900)
        {
            resultId="peng7"
            resultTxt.setText("You get\nSuper Rare\npenguin")
            resultImg.setImageResource(R.drawable.peng_adellyking)
        }
        if (900<resultNum&&resultNum<=950)
        {
            resultId="peng8"
            resultTxt.setText("You get\nSpecial\npenguin")
            resultImg.setImageResource(R.drawable.peng_rainking)
        }
        if (950<resultNum&&resultNum<=1000)
        {
            resultId="peng9"
            resultTxt.setText("You get\nSpecial\npenguin")
            resultImg.setImageResource(R.drawable.peng_baby)
        }
        return resultId
    }

    // 11월 30일 UpdateActivity gacha DB 처리 함수
    fun gachaDataCheck(AppDB : AppDatabase, dName : String){

        var sql : SQLiteDatabase = AppDB.readableDatabase
        var c : Cursor = sql.rawQuery("SELECT * FROM getGacha WHERE gachaId = '$dName'",null)
        var length = c.count
        Log.i("myApp","Update - $length"+"data in getGacha DB")
        sql.close()
        //Log.i("myApp","Update cursor length = $length")
        if(length == 0){    // not duplicate
            sql =AppDB.writableDatabase
            sql.execSQL("INSERT OR IGNORE INTO getGacha (gachaId) VALUES('$dName')")
            Log.i("myApp","DB INSERT - $dName")
        }
    }
}
