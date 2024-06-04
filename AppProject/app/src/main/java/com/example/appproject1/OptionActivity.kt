package com.example.appproject1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_option.*
import java.lang.Exception

class OptionActivity : AppCompatActivity() {

    lateinit var optlay1 : RelativeLayout
    lateinit var backBtn1 : ImageButton
    lateinit var setLimitBtn : ImageButton
    lateinit var manBtn : ImageButton

    lateinit var optlay2 : RelativeLayout
    lateinit var backBtn2 : ImageButton
    lateinit var editText : EditText
    lateinit var submitBtn : ImageButton

    // DB 설정
    lateinit var databaseOption: AppDatabase
    lateinit var sqlDB : SQLiteDatabase

    // 11월 30일 옵션 팝업 [설명서] 추가

    lateinit var optlay3 : RelativeLayout
    lateinit var manContent: ViewFlipper
    lateinit var backBtn3 : ImageButton
    lateinit var NextBtn : Button
    lateinit var PrevBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 11월 22일 Option 메뉴를 팝업으로 구현
        // 타이틀바 없애기 -> 동작안함 -> 해결
        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_option)

        optlay1 = findViewById(R.id.optionLayout1)
        backBtn1 = findViewById(R.id.backBtn1)
        setLimitBtn = findViewById(R.id.setLimitBtn)
        manBtn = findViewById(R.id.manualBtn)
        optlay2 = findViewById(R.id.optionLayout2)
        backBtn2 = findViewById(R.id.backBtn2)
        editText = findViewById<EditText>(R.id.editText)
        submitBtn = findViewById(R.id.submitBtn)

        optlay3 = findViewById(R.id.optionLayout3)
        backBtn3 = findViewById(R.id.backBtn3)
        //View Flipper
        manContent = findViewById(R.id.manualView)
        NextBtn = findViewById(R.id.nextBtn)
        PrevBtn = findViewById(R.id.prevBtn)


        // DB 설정
        databaseOption = AppDatabase(this)
        var dbUserLimit = 0
        var dbUserSum = 0

        // DB에서 사용자 정보 가져오기
        sqlDB = databaseOption.readableDatabase

        var userDataColumn = arrayOf("userLimit","userSum")
        var c : Cursor = sqlDB.query("userData",userDataColumn,null,null,null,null,null)
        while(c.moveToNext()){
            dbUserLimit = c.getInt(0)
            dbUserSum = c.getInt(1)
        }

        backBtn1.setOnClickListener {

            // 11월 5일 : Back 버튼을 기존 안드로이드 뒤로가기 버튼과 똑같은 기능하도록 설정
            //onBackPressed()
            // 11월 22일 : 팝업 형식에서 Back 버튼을 눌렀을때 팝업(엑티비티) 닫기 기능 구현
            finish()
        }

        setLimitBtn.setOnClickListener {
            optlay1.visibility = View.GONE
            optlay2.visibility = View.VISIBLE
        }

        backBtn2.setOnClickListener {
            optlay1.visibility = View.VISIBLE
            optlay2.visibility = View.GONE
        }

        submitBtn.setOnClickListener {
            // 11월 25일 : 소비 한도 설정(okay) 버튼 누르면 데이터(RESULT_OK) 전달
            var intent = Intent(this, MainActivity::class.java)
            // intent.putExtra("Name",value);
            //intent.putExtra("Limit")
            var limit = dbUserLimit
            try {
                var tmp : String = editText.text.toString()
                limit = tmp.toInt()
            }catch (e : Exception){
                Log.i("myApp","ERROR $e")
                Toast.makeText(applicationContext,"숫자만 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
                //finish()
            }
            // okay 버튼 누를시 키보드 안사라짐 -> 키보드 숨기는 메소드
            var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken,0)
            //intent.putExtra("limit",limit)
            setResult(Activity.RESULT_OK,intent)
            //finish()

            // 검사
            if(dbUserLimit == limit){
                Log.i("myApp","Error : user limit has same value before")
                Toast.makeText(applicationContext,"소비 한도가 이전과 동일합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // DB 저장
            sqlDB =databaseOption.writableDatabase
            sqlDB.execSQL("UPDATE userData SET userLimit=$limit WHERE userLimit=$dbUserLimit")

            optlay1.visibility = View.VISIBLE
            optlay2.visibility = View.GONE
            Log.i("myApp","Option : user limit set : $limit")
            Toast.makeText(applicationContext,"소비 한도를 $limit"+"원으로 설정했습니다.", Toast.LENGTH_SHORT).show()
        }

        // 11월 30일 옵션 설명서 추가
        manBtn.setOnClickListener{
            optlay1.visibility = View.GONE
            optlay3.visibility = View.VISIBLE
        }

        backBtn3.setOnClickListener{
            optlay1.visibility = View.VISIBLE
            optlay3.visibility = View.GONE
        }
        // View Fliper 제어
        NextBtn.setOnClickListener{
            manContent.showNext()
        }
        PrevBtn.setOnClickListener{
            manContent.showPrevious()
        }


    }
    // 11월 22일 팝업 기능 구현
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        // 팝업 창 바깥 터치시 창이 안닫히게
        if(event?.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false
        }
        return true
    }

    // 11월 22일 안드로이드 뒤로가기 버튼 기능 수정
    override fun onBackPressed(){
        return
    }


}