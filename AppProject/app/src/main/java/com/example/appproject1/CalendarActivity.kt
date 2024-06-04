package com.example.appproject1

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get
import java.time.LocalDate


class CalendarActivity : AppCompatActivity() {

    // 11월 19일 11월 27일
    lateinit var monthSum : TextView

    lateinit var calview : CalendarView
    lateinit var slinearLayout : LinearLayout
    lateinit var scrlView : ScrollView
    lateinit var backBtn : Button

    // 11월 19일 DB 추가
    lateinit var databaseCal: AppDatabase
    lateinit var sqlDB : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        // 12월 1일
        val display =
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val width = (display.width * 0.7).toInt() //Display 사이즈의 70%
        val height = (display.height * 0.9).toInt() //Display 사이즈의 90%
        window.attributes.width = width
        window.attributes.height = height

        monthSum = findViewById(R.id.monthSumTxt)
        calview = findViewById(R.id.calview)
        slinearLayout = findViewById(R.id.scrollLinear)
        scrlView = findViewById(R.id.scrollView)
        backBtn = findViewById(R.id.calBackBtn)

        // DB 연결
        // 11월 27일 이번 달[해당 달의]사용 금액 함수로 구현
        // 캘린더 뷰 첫 실행 -> 오늘 날짜로 나옴 : 이번 달 | 날짜 변경 -> onDateChangeListener
        databaseCal = AppDatabase(this)
        var thisDay = LocalDate.now().dayOfMonth
        var thisMonth = LocalDate.now().monthValue
        var thisYear = LocalDate.now().year

        var sum = getMonthSum(databaseCal,thisMonth)
        monthSum.text = "이번달 사용금액 : $sum" +"원"
        getListOfDay(databaseCal,scrlView,thisYear,thisMonth,thisDay)

        // 리스너 ------------------------------------------------------------------

        calview.setOnDateChangeListener { view, year, month, dayOfMonth ->

            var sum = getMonthSum(databaseCal,month+1)
            monthSum.text = "이번달 사용금액 : $sum" +"원"
            getListOfDay(databaseCal, scrlView, year,month+1,dayOfMonth)

        }
        backBtn.setOnClickListener {
            finish()
        }


    }

    fun getListOfDay(AppDB : AppDatabase, scrView : ScrollView, year : Int, month : Int, day : Int) {

        var sql : SQLiteDatabase = AppDB.readableDatabase
        var linear = LinearLayout(this)
        linear.setOrientation(LinearLayout.VERTICAL)

        var parm = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        scrView.removeAllViews()
        var c : Cursor = sql.rawQuery(
            "SELECT category,content,price FROM writeData WHERE year='$year' and month='$month' and day='$day'",null)

        if(c != null){
            while(c.moveToNext()){
                var text = TextView(this)
                var category : String = c.getString(0)
                var content : String = c.getString((1))
                var price : Int = c.getInt(2)

                text.setText("카테고리 : $category\n내용 : $content\n가격 : $price"+"원\n")
                text.setTextAppearance(this,R.style.ScrollText)

                linear.addView(text)
            }
        }
        scrView.addView(linear,parm)
        //linear.bringToFront()
        sql.close()
    }
    // 12월 1일 팝업 기능 구현
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        // 팝업 창 바깥 터치시 창이 안닫히게
        if(event?.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false
        }
        return true
    }

    // 클래스 내부
}

// -------------------------------------- Function ------------------------------------------
fun getMonthSum(AppDB : AppDatabase, month : Int) : Int {

    var sum : Int
    var sql : SQLiteDatabase = AppDB.readableDatabase
    var c : Cursor = sql.rawQuery("SELECT SUM(price) FROM writeData WHERE month=$month",null)
    c.moveToFirst()
    sum = c.getInt(0)
    //Log.i("myApp","DB : Calendar Get SUM Price : $sum of Month : $month")
    c.close()
    sql.close()
    return sum
}
