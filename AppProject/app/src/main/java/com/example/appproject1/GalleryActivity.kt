package com.example.appproject1

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class GalleryActivity : AppCompatActivity() {

    lateinit var backBtn : ImageButton

    lateinit var p1Btn : ImageButton
    lateinit var p2Btn : ImageButton
    lateinit var p3Btn : ImageButton
    lateinit var p4Btn : ImageButton
    lateinit var p5Btn : ImageButton
    lateinit var p6Btn : ImageButton
    lateinit var p7Btn : ImageButton
    lateinit var p8Btn : ImageButton
    lateinit var p9Btn : ImageButton

    // 11월 30일 DB 추가
    lateinit var databaseGal: AppDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        // 12월 1일
        val display =
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val width = (display.width * 0.7).toInt() //Display 사이즈의 70%
        val height = (display.height * 0.8).toInt() //Display 사이즈의 90%
        window.attributes.width = width
        window.attributes.height = height

        backBtn = findViewById(R.id.galleryBackBtn)

        // 11월 19일 Button -> ImageButton 수정
        // 11월 25일 수정 xml파일 id
        p1Btn = findViewById(R.id.peng1Btn)
        p2Btn = findViewById(R.id.peng2Btn)
        p3Btn = findViewById(R.id.peng3Btn)
        p4Btn = findViewById(R.id.peng4Btn)
        p5Btn = findViewById(R.id.peng5Btn)
        p6Btn = findViewById(R.id.peng6Btn)
        p7Btn = findViewById(R.id.peng7Btn)
        p8Btn = findViewById(R.id.peng8Btn)
        p9Btn = findViewById(R.id.peng9Btn)

        // 11월 30일 DB
        databaseGal = AppDatabase(this)

        getGachaData(databaseGal)

        //  리스너 ------------------------------------------------

        backBtn.setOnClickListener {
            // 11월 5일 : Back 버튼을 기존 안드로이드 뒤로가기 버튼과 똑같은 기능하도록 설정
            //onBackPressed()

            // 11월 25일 onBackPressed() : deprecated -> finish() 로 변경
            //finish()

            // 11월 30일 : 인텐트 전달을 위해 onBackPressed() 오버라이드
            onBackPressed()
        }

    }
    override fun onBackPressed(){


        var intent = Intent(this,MainActivity::class.java)
//        intent.putExtra("imgId",changeImgId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    fun getGachaData(AppDB : AppDatabase){

        var sql : SQLiteDatabase = AppDB.readableDatabase
        var c : Cursor = sql.rawQuery("SELECT * FROM getGacha",null)
        var length = c.count
        Log.i("myApp","Calendar - Found $length Gacha Data")
        var tmpImgBtn : ImageButton
        while(c.moveToNext()){
            var gachaId = c.getString(0)
            when(gachaId){
                "peng1" -> { tmpImgBtn=findViewById(R.id.peng1Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_basic)
                    attachClickListener(tmpImgBtn,R.drawable.peng_basic,"Basic","Normal") }

                "peng2" -> { tmpImgBtn=findViewById(R.id.peng2Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_rain)
                    attachClickListener(tmpImgBtn,R.drawable.peng_rain,"Rain","Normal") }

                "peng3" -> { tmpImgBtn=findViewById(R.id.peng3Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_adelly)
                    attachClickListener(tmpImgBtn,R.drawable.peng_adelly,"Adelly","Rare") }

                "peng4" -> { tmpImgBtn=findViewById(R.id.peng4Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_ribbon)
                    attachClickListener(tmpImgBtn,R.drawable.peng_ribbon,"Ribbon","Rare")}

                "peng5" -> { tmpImgBtn=findViewById(R.id.peng5Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_plant)
                    attachClickListener(tmpImgBtn,R.drawable.peng_plant,"Plant","Rare") }

                "peng6" -> { tmpImgBtn=findViewById(R.id.peng6Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_egg)
                    attachClickListener(tmpImgBtn,R.drawable.peng_egg,"Egg","Super Rare")}

                "peng7" -> { tmpImgBtn=findViewById(R.id.peng7Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_adellyking)
                    attachClickListener(tmpImgBtn,R.drawable.peng_adellyking,"Adelly King","Super Rare")}

                "peng8" -> { tmpImgBtn=findViewById(R.id.peng8Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_rainking)
                    attachClickListener(tmpImgBtn,R.drawable.peng_rainking,"Rain King","Special")}

                "peng9" -> { tmpImgBtn=findViewById(R.id.peng9Btn)
                    tmpImgBtn.setBackgroundResource(R.drawable.peng_baby)
                    attachClickListener(tmpImgBtn,R.drawable.peng_baby,"Baby","Special") }
            }
        }
        sql.close()
    }
    // 획득한 각 이미지 버튼마다 클릭/롱클릭 리스너 붙이는 메소드 구현
    fun attachClickListener(Btn : ImageButton , imageId : Int, imgName : String, imgGrade : String){

        Btn.setOnClickListener{
            //var mainImage : ImageView = findViewById(R.id.mainImageView)
            //mainImage.setImageResource(ImgId)
            //changeImgId = imgId
            var AppDB = AppDatabase(this)
            var sql = AppDB.writableDatabase
            sql.execSQL("UPDATE mainImage SET imgId=$imageId")

            Toast.makeText(applicationContext,"메인화면 이미지를 변경했습니다.", Toast.LENGTH_SHORT).show()
        }
        Btn.setOnLongClickListener{
            var infoView : View = View.inflate(this,R.layout.penginfo,null)
            var dialog = AlertDialog.Builder(this, R.style.PengInfo)
            dialog.setView(infoView)
            dialog.setNegativeButton("CLOSE",null)
            var pengImg = infoView.findViewById<ImageView>(R.id.penginfoImage)
            pengImg.setImageResource(imageId)
            var pengTxt = infoView.findViewById<TextView>(R.id.penginfoText)
            pengTxt.text=imgName
            var pengGrade = infoView.findViewById<TextView>(R.id.pengGrade)
            pengGrade.text= imgGrade

            dialog.show()
            true
        }



    }

    // 클래스 내부



}