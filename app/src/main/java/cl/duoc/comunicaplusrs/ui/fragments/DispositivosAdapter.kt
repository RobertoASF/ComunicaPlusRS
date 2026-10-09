package cl.duoc.comunicaplusrs.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cl.duoc.comunicaplusrs.R
import cl.duoc.comunicaplusrs.databinding.ItemDispositivoBinding
import cl.duoc.comunicaplusrs.model.Dispositivo
import cl.duoc.comunicaplusrs.utils.formatearFecha
import cl.duoc.comunicaplusrs.viewmodel.ItemDispositivo

// Adapter de la lista de dispositivos. ListAdapter solo redibuja las filas que cambian.
class DispositivosAdapter(
    private val onVerMapa: (Dispositivo) -> Unit,
    private val onEliminar: (Dispositivo) -> Unit
) : ListAdapter<ItemDispositivo, DispositivosAdapter.DispositivoViewHolder>(Comparador) {

    class DispositivoViewHolder(
        val binding: ItemDispositivoBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DispositivoViewHolder {
        val binding = ItemDispositivoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DispositivoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DispositivoViewHolder, position: Int) {
        val item = getItem(position)
        val dispositivo = item.dispositivo
        val contexto = holder.itemView.context

        holder.binding.tvNombre.text = dispositivo.nombre
        holder.binding.tvDetalle.text = dispositivo.direccion.ifBlank {
            contexto.getString(R.string.sin_direccion)
        }
        holder.binding.tvFecha.text = contexto.getString(
            R.string.actualizado_el,
            formatearFecha(dispositivo.fecha)
        )

        holder.binding.tvDistancia.isVisible = item.distancia != null
        holder.binding.tvDistancia.text = contexto.getString(
            R.string.distancia_aproximada,
            item.distancia.orEmpty()
        )

        holder.binding.btnMapa.setOnClickListener { onVerMapa(dispositivo) }
        holder.binding.btnEliminar.setOnClickListener { onEliminar(dispositivo) }
    }

    object Comparador : DiffUtil.ItemCallback<ItemDispositivo>() {
        override fun areItemsTheSame(anterior: ItemDispositivo, nuevo: ItemDispositivo): Boolean {
            return anterior.dispositivo.id == nuevo.dispositivo.id
        }

        override fun areContentsTheSame(anterior: ItemDispositivo, nuevo: ItemDispositivo): Boolean {
            return anterior == nuevo
        }
    }
}
