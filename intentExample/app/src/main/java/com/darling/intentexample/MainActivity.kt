package com.darling.intentexample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.net.Uri;
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_AGE = "extra_age"
    }
    // Activity Result API: chọn ảnh
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            val tv = findViewById<TextView>(R.id.tvPickedImage)
            if (uri != null) {
                tv.text = "Picked: $uri"
            } else {
                tv.text = "No image selected"
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //code từng nút sẽ đặt ở đây
        //nút 1
        val btnOpenDetail = findViewById<Button>(R.id.btnOpenDetail)
        btnOpenDetail.setOnClickListener {
            //tạo đối tượng intent, chỉ định rõ activity nguồn là this (MainActivity), activity
            //đích đến là
            //DetailActivity
            val intent = Intent(this, DetailActivity::class.java)
            //start activity đích
            startActivity(intent)
        }

        //nút 2
        val btnSendData = findViewById<Button>(R.id.btnSendData)
        btnSendData.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            //đẩy dữ liệu "Kim Giao" vào key đặt tên EXTRA_NAME - các em có thể
            // đặt tên key theo ý muốn
            intent.putExtra(EXTRA_NAME, "Kim Giao")
            //tương tự đẩy dữ liệu "41" và key EXTRA_AGE
            intent.putExtra(EXTRA_AGE, 41)
            startActivity(intent)
        }

        //nút 3
        val btnOpenWeb = findViewById<Button>(R.id.btnOpenWeb)
        btnOpenWeb.setOnClickListener {
            //tạo intent (loại implicit), với action là view (xem) trang google.com
            //như vậy android sẽ xem xét các app có trên điện thoại, app nào có activity có thể thực hiện action
            //này
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://www.google.com"))
            startActivity(intent)
        }

        //nút 4
        val btnDial = findViewById<Button>(R.id.btnDial)
        btnDial.setOnClickListener {
            //tạo intent (implicit) với action là dial
            //nếu dùng acction_call sẽ bị lỗi yêu cầu permission --> dùng action_dial
            //khi này android sẽ quét xem app nào có thể nhận action này, và mở app đó lên
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:0919132051")
            }
            startActivity(intent)
        }

        //nút 5
        val btnEmail = findViewById<Button>(R.id.btnEmail)
        btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:someone@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "This is subject")
                putExtra(Intent.EXTRA_TEXT, "Hello! This is a test email.")
            }

            // tránh crash nếu máy không có app email
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Không có app Email để mở",
                    Toast.LENGTH_SHORT).show()
            }
        }

        //nút 6
        val btnShare = findViewById<Button>(R.id.btnShare)
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Hello from IntentPractice!")
            }
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ"))
        }

        //nút 7
        val btnPickImage = findViewById<Button>(R.id.btnPickImage)
        btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }
}