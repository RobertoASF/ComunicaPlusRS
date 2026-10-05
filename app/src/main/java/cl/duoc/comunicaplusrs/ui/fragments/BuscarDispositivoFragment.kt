package cl.duoc.comunicaplusrs.ui.fragments

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cl.duoc.comunicaplusrs.R
import cl.duoc.comunicaplusrs.databinding.FragmentBuscarDispositivoBinding
import cl.duoc.comunicaplusrs.model.Dispositivo
import cl.duoc.comunicaplusrs.utils.crearIntentMapa
import cl.duoc.comunicaplusrs.utils.crearIntentMapaWeb
import cl.duoc.comunicaplusrs.utils.formatearCoordenadas
import cl.duoc.comunicaplusrs.utils.formatearFecha
import cl.duoc.comunicaplusrs.viewmodel.DispositivosUiState
import cl.duoc.comunicaplusrs.viewmodel.DispositivosViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

// Pantalla hecha con el sistema de vistas (XML + ViewBinding) en un Fragment.
class BuscarDispositivoFragment : Fragment(R.layout.fragment_buscar_dispositivo) {

    private var _binding: FragmentBuscarDispositivoBinding? = null
    private val binding get() = _binding!!

    // viewModels() es una extensión KTX de fragment-ktx.
    private val viewModel: DispositivosViewModel by viewModels {
        DispositivosViewModel.Factory
    }

    private val adapter = DispositivosAdapter(
        onVerMapa = { abrirMapa(it) },
        onEliminar = { confirmarEliminar(it) }
    )

    private val pedirPermisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) {
            viewModel.actualizarUbicacion()
        } else {
            viewModel.mostrarError(getString(R.string.permiso_ubicacion_denegado))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBuscarDispositivoBinding.bind(view)

        binding.rvDispositivos.adapter = adapter

        binding.btnActualizarUbicacion.setOnClickListener {
            revisarPermisosYActualizar()
        }

        binding.btnVerMapa.setOnClickListener {
            viewModel.uiState.value.actual?.let { abrirMapa(it) }
        }

        binding.swMostrarDistancia.setOnCheckedChangeListener { _, activo ->
            viewModel.cambiarMostrarDistancia(activo)
        }

        // Si el permiso ya estaba dado, la ubicación se actualiza al abrir la pantalla.
        if (savedInstanceState == null && tienePermisoUbicacion()) {
            viewModel.actualizarUbicacion()
        }

        // repeatOnLifecycle deja de escuchar cuando la pantalla no está visible.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { mostrarEstado(it) }
            }
        }
    }

    private fun mostrarEstado(estado: DispositivosUiState) {
        binding.progreso.isVisible = estado.cargando
        binding.btnActualizarUbicacion.isEnabled = !estado.cargando
        binding.tvNombreDispositivo.text = estado.nombreDispositivo

        val actual = estado.actual
        if (actual == null) {
            binding.tvCoordenadas.text = getString(R.string.sin_ubicacion)
            binding.tvDireccion.isVisible = false
            binding.tvFechaActualizacion.isVisible = false
            binding.btnVerMapa.isEnabled = false
        } else {
            binding.tvCoordenadas.text = getString(
                R.string.coordenadas_precision,
                formatearCoordenadas(actual.latitud, actual.longitud),
                actual.precision.toInt()
            )
            binding.tvDireccion.isVisible = actual.direccion.isNotBlank()
            binding.tvDireccion.text = actual.direccion
            binding.tvFechaActualizacion.isVisible = true
            binding.tvFechaActualizacion.text = getString(
                R.string.actualizado_el,
                formatearFecha(actual.fecha)
            )
            binding.btnVerMapa.isEnabled = true
        }

        binding.tvMensaje.isVisible = estado.mensaje.isNotBlank()
        binding.tvMensaje.text = estado.mensaje
        binding.tvMensaje.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (estado.esError) R.color.texto_error else R.color.texto_exito
            )
        )

        if (binding.swMostrarDistancia.isChecked != estado.mostrarDistancia) {
            binding.swMostrarDistancia.isChecked = estado.mostrarDistancia
        }

        adapter.submitList(estado.otros)
        binding.tvSinDispositivos.isVisible = estado.otros.isEmpty()
    }

    private fun tienePermisoUbicacion(): Boolean {
        val permisos = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permisos.any {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun revisarPermisosYActualizar() {
        if (tienePermisoUbicacion()) {
            viewModel.actualizarUbicacion()
        } else {
            pedirPermisos.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun abrirMapa(dispositivo: Dispositivo) {
        try {
            startActivity(crearIntentMapa(dispositivo.latitud, dispositivo.longitud, dispositivo.nombre))
        } catch (e: ActivityNotFoundException) {
            // Sin app de mapas se abre Google Maps en el navegador.
            startActivity(crearIntentMapaWeb(dispositivo.latitud, dispositivo.longitud))
        }
    }

    private fun confirmarEliminar(dispositivo: Dispositivo) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.eliminar_dispositivo)
            .setMessage(getString(R.string.confirmar_eliminar_dispositivo, dispositivo.nombre))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                viewModel.eliminar(dispositivo)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
