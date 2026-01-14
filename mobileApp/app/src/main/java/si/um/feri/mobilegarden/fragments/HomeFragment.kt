package si.um.feri.mobilegarden.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
            capturedBitmap?.let {
                showUploadDialog(it)
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
                Log.i("HomeFragment", "Response: $responseText")

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

    private fun showUploadDialog(bitmap: Bitmap) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_upload, null)

        val etUrl = dialogView.findViewById<EditText>(R.id.etUrl)
        etUrl.setText("http://10.77.233.73:5000/upload")
        val etUsername = dialogView.findViewById<EditText>(R.id.etUsername)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val etGardenName = dialogView.findViewById<EditText>(R.id.etGardenName)
        val etWidth = dialogView.findViewById<EditText>(R.id.etWidth)
        val etHeight = dialogView.findViewById<EditText>(R.id.etHeight)

        AlertDialog.Builder(requireContext())
            .setTitle("Upload image")
            .setView(dialogView)
            .setPositiveButton("Send") { _, _ ->

                val url = etUrl.text.toString().trim()    .ifEmpty { "http://10.77.233.73:5000" }
                val username = etUsername.text.toString().trim()
                val password = etPassword.text.toString()
                val gardenName = etGardenName.text.toString().trim()
                val width = etWidth.text.toString().toIntOrNull()
                val height = etHeight.text.toString().toIntOrNull()

                // validation
                if (url.isEmpty() || username.isEmpty() || password.isEmpty() ||
                    gardenName.isEmpty() || width == null || height == null ||
                    width !in 1..40 || height !in 1..40
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill all fields correctly",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                uploadImage(bitmap, url, username, password, gardenName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}