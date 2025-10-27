(function () {
    function obtenerValorElemento(idElemento) {
        const elemento = document.getElementById(idElemento);
        if (!elemento) {
            console.error(`Elemento con id '${idElemento}' no encontrado`);
            return null;
        }
        return elemento.value.trim();
    }

    function guardarUsuarioEnLocalStorage(usuarioId, usuarioNombre) {
        if (!usuarioId || !usuarioNombre) {
            console.error("ID o nombre del usuario inválido");
            return;
        }

        localStorage.setItem("usuarioId", usuarioId);
        localStorage.setItem("usuarioNombre", usuarioNombre);
    }

    function inicializarGuardado() {
        const formulario = document.querySelector(".login-form");
        if (!formulario) {
            console.error("Formulario de login encontrado");
            return;
        }

        formulario.addEventListener("submit", () => {
            const usuarioId = obtenerValorElemento("usuario-id");
            const usuarioNombre = obtenerValorElemento("usuario-nombre");
            guardarUsuarioEnLocalStorage(usuarioId, usuarioNombre);
        });
    }

    inicializarGuardado();
})();

(function () {
    const campoId = document.getElementById("usuario-id");
    const campoNombre = document.getElementById("usuario-nombre");

    if (!campoId || !campoNombre) {
        console.warn("Campos ocultos de usuario no encontrados en esta vista");
        return;
    }

    const usuarioId = campoId.value.trim();
    const usuarioNombre = campoNombre.value.trim();

    if (!usuarioId || !usuarioNombre) {
        console.error("Datos del usuario no válidos para LocalStorage");
        return;
    }

    localStorage.setItem("usuarioId", usuarioId);
    localStorage.setItem("usuarioNombre", usuarioNombre);

    console.info("✅ Usuario guardado en LocalStorage");
})();

document.addEventListener("DOMContentLoaded", () => {
    const botonLogout = document.getElementById("logout-btn");
    if (!botonLogout) return;

    botonLogout.addEventListener("click", (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = botonLogout.href;
    });
});
