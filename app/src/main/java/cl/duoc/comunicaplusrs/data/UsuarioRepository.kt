package cl.duoc.comunicaplusrs.data

import cl.duoc.comunicaplusrs.model.Usuario

object UsuarioRepository {

    private const val MAX_USUARIOS = 5

    private val usuarios: Array<Usuario?> = arrayOfNulls(MAX_USUARIOS)

    private var cantidadUsuarios = 0

    enum class ResultadoRegistro {
        EXITO,
        CORREO_EXISTENTE,
        LIMITE_ALCANZADO
    }

    fun registrarUsuario(usuario: Usuario): ResultadoRegistro {

        if (buscarPorCorreo(usuario.correo) != null) {
            return ResultadoRegistro.CORREO_EXISTENTE
        }

        if (cantidadUsuarios >= MAX_USUARIOS) {
            return ResultadoRegistro.LIMITE_ALCANZADO
        }

        usuarios[cantidadUsuarios] = usuario
        cantidadUsuarios++

        return ResultadoRegistro.EXITO
    }

    fun autenticar(
        correo: String,
        password: String
    ): Boolean {

        return usuarios
            .filterNotNull()
            .any {
                it.correo.equals(
                    correo.trim(),
                    ignoreCase = true
                ) && it.password == password
            }
    }

    fun buscarPorCorreo(correo: String): Usuario? {

        return usuarios
            .filterNotNull()
            .find {
                it.correo.equals(
                    correo.trim(),
                    ignoreCase = true
                )
            }
    }

    fun obtenerUsuarios(): List<Usuario> {
        return usuarios.filterNotNull()
    }

    fun cantidadUsuarios(): Int {
        return cantidadUsuarios
    }

    fun quedanCupos(): Boolean {
        return cantidadUsuarios < MAX_USUARIOS
    }
}