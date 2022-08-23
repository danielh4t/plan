package app.stacq.plan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.stacq.plan.databinding.ActivityCreateBinding
import coil.load

class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create)
        binding.createImage.load("https://plan-node-api.herokuapp.com/images/image.png") {
            placeholder(R.color.purple_500)
        }

    }
}