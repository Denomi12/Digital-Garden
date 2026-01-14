package si.um.feri.mobilegarden.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import si.um.feri.mobilegarden.R
import si.um.feri.mobilegarden.databinding.FragmentHomeBinding
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment() {
    private var capturedBitmap: Bitmap? = null


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            capturedBitmap = it
            binding.ivCapturedPhoto.setImageBitmap(it)
            Log.i("MobileGardenLog", it.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoToGenerateExtremeEvent.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_generateExtremeEventFragment)
        }

        binding.btnAddExtremeEvent.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addExtremeEventFragment)
        }

        binding.btnOpenMap.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
        }

        binding.btnCapturePhoto.setOnClickListener {
            try {
                takePicture.launch(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btnSend.setOnClickListener {
            if (capturedBitmap != null) {
                uploadImage(capturedBitmap!!, "http://10.77.233.73:5000/upload", "1", "1", "Garden Name")
            }
            else {
                Toast.makeText(requireContext(), "No image captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return stream.toByteArray()
    }

    private fun uploadImage(bitmap: Bitmap, url: String, username: String, password: String, gardenName: String) {

        Thread {
            try {
                val client = OkHttpClient()

                val imageBytes = bitmapToByteArray(bitmap)

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "image",
                        "${gardenName}.jpg",
                        imageBytes.toRequestBody("image/jpeg".toMediaType())
                    )
                    .addFormDataPart("username", username)
                    .addFormDataPart("password", password)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                val responseText = response.body?.string() ?: "No response body"

                requireActivity().runOnUiThread {
                    val message = if (response.isSuccessful) {
                        "Upload OK:\n$responseText"
                    } else {
                        "Upload FAILED (${response.code}):\n$responseText"
                    }

                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Upload FAILED:\n${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}