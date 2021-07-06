package com.example.eyesee

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.eyesee.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var bitmap: Bitmap
    lateinit var imgview:ImageView
    lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imgview = findViewById(R.id.imageView)
        val fileName = "label.txt"
        val inputString = application.assets.open(fileName).bufferedReader().use { it.readText() }
        var townList = inputString.split("\n")
        var btn : Button = findViewById(R.id.btn)
        textToSpeech = TextToSpeech(this,this)
        btn.setOnClickListener {
            var ch : String = "Hello"
            textToSpeech.speak(ch,TextToSpeech.QUEUE_FLUSH,null)
        }
        var tv:TextView = findViewById(R.id.textView)
        var select: Button = findViewById(R.id.button3)
        select.setOnClickListener(View.OnClickListener {
            var intent:Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type ="image/*"
            startActivityForResult(intent, 100)
        })
        var predict:Button = findViewById(R.id.button4)
        predict.setOnClickListener(View.OnClickListener {
            var resized:Bitmap = Bitmap.createScaledBitmap(bitmap, 224,224,true)
            val model = MobilenetV110224Quant.newInstance(this)
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
            var tbuffer = TensorImage.fromBitmap(resized)
            var byteBuffer = tbuffer.buffer
            inputFeature0.loadBuffer(byteBuffer)
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            var max =getMax(outputFeature0.floatArray)
            tv.setText(townList[max])
            model.close()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imgview.setImageURI(data?.data)
        var uri:Uri?=data?.data
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
    }

    fun getMax(arr:FloatArray) : Int{
        var ind = 0
        var min = 0.0f
        for(i in 0..1000)
        {
            if(arr[i]>min)
            {
                ind=i
                min= arr[i]
            }
        }
        return ind
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val res : Int = textToSpeech.setLanguage(Locale.US)
            if(res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this,"Language Not Supported",Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this,"fuck ho raha hai",Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(textToSpeech != null){
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

}