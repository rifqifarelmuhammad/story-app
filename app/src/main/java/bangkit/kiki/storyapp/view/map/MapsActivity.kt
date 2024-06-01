package bangkit.kiki.storyapp.view.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import bangkit.kiki.storyapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import bangkit.kiki.storyapp.databinding.ActivityMapsBinding
import bangkit.kiki.storyapp.view.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.getStories(user.token)
            }
        }

        viewModel.isError.observe(this) { isError ->
            if (isError) {
                Toast.makeText(this, viewModel.errorMessage.value, Toast.LENGTH_SHORT).show()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        viewModel.listStory.observe(this) { stories ->
            stories.forEach { story ->
                val latLng = story.lat?.let { story.lon?.let { it1 -> LatLng(it, it1) } }
                latLng?.let {
                    MarkerOptions()
                        .position(it)
                        .title(story.name)
                        .snippet(story.description)
                }?.let {
                    mMap.addMarker(
                        it
                    )
                }
            }

            val mapZoom = stories[0].lat?.let { stories[0].lon?.let { it1 ->
                LatLng(it,
                    it1
                )
            } }
            mapZoom?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
                ?.let { mMap.animateCamera(it) }
        }
    }
}