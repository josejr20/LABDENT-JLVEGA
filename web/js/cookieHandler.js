/**
 * Utilidad JavaScript para gestionar cookies de usuario
 * Reemplazo seguro de localStorage
 */

// ✅ Objeto que simula localStorage usando cookies
const secureStorage = {
    /**
     * Obtiene un valor desde las cookies
     */
    getItem: function(key) {
        const cookieMap = {
            'usuarioId': 'labdent_uid',
            'usuarioNombre': 'labdent_uname',
            'usuarioRol': 'labdent_urol',
            'email': 'labdent_email'
        };
        
        const cookieName = cookieMap[key];
        if (!cookieName) return null;
        
        const value = this.getCookie(cookieName);
        
        // Para cookies encriptadas que necesitan desencriptación en servidor
        if (key === 'usuarioId' || key === 'usuarioRol') {
            // Estas son encriptadas y solo se pueden leer desde el servidor
            // Devolver null para forzar lectura desde atributos de sesión
            return null;
        }
        
        return value;
    },
    
    /**
     * No permite setear desde JavaScript por seguridad
     */
    setItem: function(key, value) {
        console.warn('⚠️ No se permite guardar datos desde JavaScript. Use el servidor.');
        return false;
    },
    
    /**
     * Limpia todas las cookies (debe hacerse desde el servidor)
     */
    clear: function() {
        console.warn('⚠️ La limpieza de cookies debe hacerse desde el logout del servidor.');
        return false;
    },
    
    /**
     * Obtiene una cookie por nombre
     */
    getCookie: function(name) {
        const nameEQ = name + "=";
        const ca = document.cookie.split(';');
        for(let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) === ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0) {
                return decodeURIComponent(c.substring(nameEQ.length, c.length));
            }
        }
        return null;
    },
    
    /**
     * Verifica si existe una cookie
     */
    hasItem: function(key) {
        return this.getItem(key) !== null;
    }
};

/**
 * Obtiene datos del usuario desde atributos de la página
 * (Insertados por JSP de forma segura)
 */
function obtenerDatosUsuario() {
    const userData = {
        id: null,
        nombre: null,
        rol: null,
        email: null
    };
    
    // Intentar leer desde elementos ocultos en la página (más seguro)
    const idElement = document.getElementById('usuario-id');
    const nombreElement = document.getElementById('usuario-nombre');
    const rolElement = document.getElementById('usuario-rol');
    const emailElement = document.getElementById('usuario-email');
    
    if (idElement) userData.id = idElement.value;
    if (nombreElement) userData.nombre = nombreElement.value;
    if (rolElement) userData.rol = rolElement.value;
    if (emailElement) userData.email = emailElement.value;
    
    // Si no hay elementos, intentar desde cookies (solo email)
    if (!userData.email) {
        userData.email = secureStorage.getItem('email');
    }
    
    return userData;
}

/**
 * Verifica si el usuario está autenticado
 */
function estaAutenticado() {
    const usuario = obtenerDatosUsuario();
    return usuario.id !== null && usuario.id !== '';
}

/**
 * Redirige al login si no está autenticado
 */
function requerirAutenticacion() {
    if (!estaAutenticado()) {
        window.location.href = 'login?error=session_expired';
        return false;
    }
    return true;
}

/**
 * Obtiene el ID del usuario actual
 */
function obtenerUsuarioId() {
    const usuario = obtenerDatosUsuario();
    return usuario.id;
}

/**
 * Obtiene el nombre del usuario actual
 */
function obtenerUsuarioNombre() {
    const usuario = obtenerDatosUsuario();
    return usuario.nombre;
}

/**
 * Obtiene el rol del usuario actual
 */
function obtenerUsuarioRol() {
    const usuario = obtenerDatosUsuario();
    return usuario.rol;
}

/**
 * Cierra sesión de forma segura
 */
function cerrarSesion() {
    // Limpiar cualquier dato temporal en memoria
    sessionStorage.clear();
    
    // Redirigir al logout del servidor
    window.location.href = 'logout';
}

// ✅ Reemplazar localStorage con secureStorage en el scope global
// (Para compatibilidad con código existente que usa localStorage)
if (typeof window !== 'undefined') {
    // Advertir sobre el uso de localStorage
    const originalLocalStorage = window.localStorage;
    
    // Interceptar accesos a localStorage
    Object.defineProperty(window, 'localStorage', {
        get: function() {
            console.warn('⚠️ localStorage está deshabilitado por seguridad. Use secureStorage o atributos de sesión.');
            return secureStorage;
        },
        configurable: false
    });
}

// ✅ Protección contra XSS
document.addEventListener('DOMContentLoaded', function() {
    // Limpiar sessionStorage al cargar (por seguridad)
    sessionStorage.clear();
    
    // Validar que estamos en una página segura (HTTPS en producción)
    if (location.protocol !== 'https:' && location.hostname !== 'localhost') {
        console.warn('⚠️ ADVERTENCIA: Conexión no segura. Use HTTPS en producción.');
    }
});

// Exportar funciones para uso global
window.secureStorage = secureStorage;
window.obtenerDatosUsuario = obtenerDatosUsuario;
window.estaAutenticado = estaAutenticado;
window.requerirAutenticacion = requerirAutenticacion;
window.obtenerUsuarioId = obtenerUsuarioId;
window.obtenerUsuarioNombre = obtenerUsuarioNombre;
window.obtenerUsuarioRol = obtenerUsuarioRol;
window.cerrarSesion = cerrarSesion;