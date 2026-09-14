package cl.duoc.comunicaplusrs.data

import cl.duoc.comunicaplusrs.model.Usuario

object UsuarioRepository {

    const val MAX_USUARIOS = 5

    // Se limita el registro a un máximo de 5 usuarios para cumplir con la actividad.
    // Los espacios vacíos del array quedan en null hasta que alguien se registra.
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

        // Si el array ya está lleno no se agrega otro usuario.
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

        // filterNotNull() deja solo los usuarios registrados y
        // any { } revisa con una lambda si alguno coincide con el correo y la contraseña.
        return usuarios
            .filterNotNull()
            .any {
                it.correo.equals(
                    correo.trim(),
                    ignoreCase = true
                ) && it.password == password
            }
    }

    // Se busca el usuario dentro de los registros guardados en memoria.
    // find { } devuelve el primero que cumple la condición o null si no existe.
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