package si.um.feri.mobilegarden

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import si.um.feri.mobilegarden.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        binding.btnGenerate.setOnClickListener {
            findNavController(R.id.navHostFragment).popBackStack(R.id.generateExtremeEventFragment, true)
            findNavController(R.id.navHostFragment).navigate(R.id.generateExtremeEventFragment)

        }
        binding.btnHome.setOnClickListener {
            findNavController(R.id.navHostFragment).popBackStack(R.id.homeFragment, true)
            findNavController(R.id.navHostFragment).navigate(R.id.homeFragment)
        }
        binding.btnMap.setOnClickListener {
            findNavController(R.id.navHostFragment).popBackStack(R.id.mapFragment, true)
            findNavController(R.id.navHostFragment).navigate(R.id.mapFragment)
        }
    }
}