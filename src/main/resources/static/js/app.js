
const API_URL = "/api";

let modalProducto;
let modalCategoria;
let modalMovimiento;

let productosActuales = [];
let categoriasActuales = [];
let movimientosActuales = [];

// Normalizar listas recibidas desde la API
function normalizarLista(datos) {
    if (Array.isArray(datos)) return datos;
    if (Array.isArray(datos.content)) return datos.content;
    if (Array.isArray(datos.data)) return datos.data;
    if (Array.isArray(datos.productos)) return datos.productos;
    if (Array.isArray(datos.categorias)) return datos.categorias;
    return [];
}

// Realizar solicitudes GET
async function consultarAPI(endpoint) {
    const respuesta = await fetch(`${API_URL}${endpoint}`);

    if (!respuesta.ok) {
        throw new Error(`Error HTTP ${respuesta.status}`);
    }

    return respuesta.json();
}

// Realizar solicitudes POST, PUT y DELETE
async function enviarAPI(endpoint, metodo, datos = null) {
    const opciones = {
        method: metodo,
        headers: {
            "Content-Type": "application/json"
        }
    };

    if (datos !== null) {
        opciones.body = JSON.stringify(datos);
    }

    const respuesta = await fetch(`${API_URL}${endpoint}`, opciones);

    if (!respuesta.ok) {
        const error = await respuesta.json().catch(() => ({}));
        throw new Error(
            error.error || Object.values(error).join(", ")
            || `Error HTTP ${respuesta.status}`
        );
    }

    if (respuesta.status === 204) return null;

    return respuesta.json();
}

// Actualizar un indicador del dashboard
function actualizarNumero(id, cantidad) {
    document.getElementById(id).textContent = cantidad;
}

// Mostrar alertas del inventario
function mostrarAlertas(productosBajos, productosAgotados) {
    const contenedor = document.getElementById("alertasInventario");
    contenedor.replaceChildren();

    const alertas = [
        ...productosAgotados.map(producto => ({
            producto,
            tipo: "Agotado",
            clase: "danger",
            icono: "bi-x-circle"
        })),
        ...productosBajos.map(producto => ({
            producto,
            tipo: "Stock bajo",
            clase: "warning",
            icono: "bi-exclamation-triangle"
        }))
    ];

    if (alertas.length === 0) {
        contenedor.textContent =
            "No hay alertas. El inventario se encuentra en buen estado.";
        return;
    }

    alertas.forEach(alerta => {
        const fila = document.createElement("div");
        fila.className =
            "d-flex justify-content-between align-items-center border-bottom py-3";

        const nombre = document.createElement("span");
        nombre.className = "fw-semibold";
        nombre.textContent = alerta.producto.nombre;

        const estado = document.createElement("span");
        estado.className = `badge text-bg-${alerta.clase}`;
        estado.textContent = alerta.tipo;

        const icono = document.createElement("i");
        icono.className =
            `bi ${alerta.icono} text-${alerta.clase} fs-5 me-2`;

        fila.append(icono, nombre, estado);
        contenedor.appendChild(fila);
    });
}

// Cargar dashboard
async function cargarDashboard() {
    const resultados = await Promise.allSettled([
        consultarAPI("/productos"),
        consultarAPI("/categorias"),
        consultarAPI("/productos/bajo-stock"),
        consultarAPI("/productos/agotados")
    ]);

    const [productos, categorias, bajos, agotados] = resultados;

    actualizarNumero(
        "totalProductos",
        productos.status === "fulfilled" ? productos.value.length : "Error"
    );

    actualizarNumero(
        "totalCategorias",
        categorias.status === "fulfilled" ? categorias.value.length : "Error"
    );

    actualizarNumero(
        "productosBajoStock",
        bajos.status === "fulfilled" ? bajos.value.length : "Error"
    );

    actualizarNumero(
        "productosAgotados",
        agotados.status === "fulfilled" ? agotados.value.length : "Error"
    );

    if (bajos.status === "fulfilled" && agotados.status === "fulfilled") {
        mostrarAlertas(bajos.value, agotados.value);
    }
}

// Cargar categorías en el formulario
async function cargarCategorias() {
    categoriasActuales = normalizarLista(
        await consultarAPI("/categorias")
    );

    const selector = document.getElementById("idCategoria");
    selector.replaceChildren();

    const opcionInicial = document.createElement("option");
    opcionInicial.value = "";
    opcionInicial.textContent = "Selecciona una categoría";
    selector.appendChild(opcionInicial);

    categoriasActuales.forEach(categoria => {
        const opcion = document.createElement("option");
        opcion.value = categoria.idCategoria;
        opcion.textContent = categoria.nombre;
        selector.appendChild(opcion);
    });
}

// Determinar estado del stock
function obtenerEstado(producto) {
    if (producto.stock <= 0) {
        return { texto: "Agotado", clase: "danger" };
    }

    if (producto.stock <= producto.stockMinimo) {
        return { texto: "Stock bajo", clase: "warning" };
    }

    return { texto: "Disponible", clase: "success" };
}

// Mostrar productos en la tabla
function mostrarProductos(productos) {
    const tabla = document.getElementById("tablaProductos");
    tabla.replaceChildren();

    if (productos.length === 0) {
        const fila = tabla.insertRow();
        const celda = fila.insertCell();
        celda.colSpan = 7;
        celda.className = "text-center py-4";
        celda.textContent = "No hay productos registrados.";
        return;
    }

    productos.forEach(producto => {
        const fila = tabla.insertRow();
        const estado = obtenerEstado(producto);

        const valores = [
            producto.sku,
            producto.nombre,
            producto.nombreCategoria,
            Number(producto.precio).toLocaleString("es-CO", {
                style: "currency",
                currency: "COP",
                maximumFractionDigits: 0
            }),
            producto.stock
        ];

        valores.forEach(valor => {
            const celda = fila.insertCell();
            celda.textContent = valor ?? "";
        });

        const celdaEstado = fila.insertCell();
        const insignia = document.createElement("span");
        insignia.className = `badge text-bg-${estado.clase}`;
        insignia.textContent = estado.texto;
        celdaEstado.appendChild(insignia);

        const celdaAcciones = fila.insertCell();

        const botonEditar = document.createElement("button");
        botonEditar.className = "btn btn-sm btn-outline-primary me-1";
        botonEditar.innerHTML = '<i class="bi bi-pencil"></i>';
        botonEditar.title = "Editar producto";
        botonEditar.addEventListener("click", () => editarProducto(producto.idProducto));

        const botonEliminar = document.createElement("button");
        botonEliminar.className = "btn btn-sm btn-outline-danger";
        botonEliminar.innerHTML = '<i class="bi bi-trash"></i>';
        botonEliminar.title = "Eliminar producto";
        botonEliminar.addEventListener("click", () => eliminarProducto(producto.idProducto));

        celdaAcciones.append(botonEditar, botonEliminar);
    });
}

// Consultar productos
async function cargarProductos() {
    const tabla = document.getElementById("tablaProductos");

    try {
        productosActuales = normalizarLista(
            await consultarAPI("/productos")
        );

        mostrarProductos(productosActuales);
    } catch (error) {
        tabla.textContent = `No se pudieron cargar los productos: ${error.message}`;
    }
}

// Cargar y mostrar categorías
async function cargarCategorias() {
    categoriasActuales = normalizarLista(
        await consultarAPI("/categorias")
    );

    const tabla = document.getElementById("tablaCategorias");
    tabla.replaceChildren();

    if (categoriasActuales.length === 0) {
        const fila = tabla.insertRow();
        const celda = fila.insertCell();
        celda.colSpan = 4;
        celda.className = "text-center py-4";
        celda.textContent = "No hay categorías registradas.";
        return;
    }

    categoriasActuales.forEach(categoria => {
        const fila = tabla.insertRow();

        const valores = [
            categoria.idCategoria,
            categoria.nombre,
            categoria.descripcion
        ];

        valores.forEach(valor => {
            const celda = fila.insertCell();
            celda.textContent = valor ?? "";
        });

        const acciones = fila.insertCell();

        const botonEditar = document.createElement("button");
        botonEditar.className = "btn btn-sm btn-outline-primary me-1";
        botonEditar.innerHTML = '<i class="bi bi-pencil"></i>';
        botonEditar.title = "Editar categoría";
        botonEditar.addEventListener("click", () =>
            editarCategoria(categoria.idCategoria)
        );

        const botonEliminar = document.createElement("button");
        botonEliminar.className = "btn btn-sm btn-outline-danger";
        botonEliminar.innerHTML = '<i class="bi bi-trash"></i>';
        botonEliminar.title = "Eliminar categoría";
        botonEliminar.addEventListener("click", () =>
            eliminarCategoria(categoria.idCategoria)
        );

        acciones.append(botonEditar, botonEliminar);
    });

    // Actualizar también las categorías del formulario de productos
    const selector = document.getElementById("idCategoria");
    const seleccionAnterior = selector.value;

    selector.replaceChildren();

    const opcionInicial = document.createElement("option");
    opcionInicial.value = "";
    opcionInicial.textContent = "Selecciona una categoría";
    selector.appendChild(opcionInicial);

    categoriasActuales.forEach(categoria => {
        const opcion = document.createElement("option");
        opcion.value = categoria.idCategoria;
        opcion.textContent = categoria.nombre;
        selector.appendChild(opcion);
    });

    selector.value = seleccionAnterior;
}

// Abrir formulario de nueva categoría
function nuevaCategoria() {
    document.getElementById("formCategoria").reset();
    document.getElementById("idCategoriaEdicion").value = "";

    document.getElementById("tituloModalCategoria").textContent =
        "Registrar categoría";

    document.getElementById("btnGuardarCategoria").textContent =
        "Guardar categoría";

    document.getElementById("mensajeCategoria").textContent = "";

    modalCategoria.show();
}

// Consultar categoría para editarla
async function editarCategoria(id) {
    try {
        const categoria = await consultarAPI(`/categorias/${id}`);

        document.getElementById("idCategoriaEdicion").value =
            categoria.idCategoria;

        document.getElementById("nombreCategoria").value =
            categoria.nombre;

        document.getElementById("descripcionCategoria").value =
            categoria.descripcion || "";

        document.getElementById("tituloModalCategoria").textContent =
            "Editar categoría";

        document.getElementById("btnGuardarCategoria").textContent =
            "Actualizar categoría";

        document.getElementById("mensajeCategoria").textContent = "";

        modalCategoria.show();

    } catch (error) {
        alert(`No se pudo consultar la categoría: ${error.message}`);
    }
}

// Registrar o actualizar categoría
async function guardarCategoria(evento) {
    evento.preventDefault();

    const id = document.getElementById("idCategoriaEdicion").value;

    const datos = {
        nombre: document.getElementById("nombreCategoria").value.trim(),
        descripcion: document.getElementById("descripcionCategoria").value.trim()
    };

    const mensaje = document.getElementById("mensajeCategoria");
    const boton = document.getElementById("btnGuardarCategoria");

    boton.disabled = true;
    mensaje.textContent = "";

    try {
        if (id) {
            await enviarAPI(`/categorias/${id}`, "PUT", datos);
        } else {
            await enviarAPI("/categorias", "POST", datos);
        }

        modalCategoria.hide();

        await cargarCategorias();
        await cargarDashboard();

        alert(id
            ? "Categoría actualizada correctamente."
            : "Categoría registrada correctamente."
        );

    } catch (error) {
        mensaje.className = "alert alert-danger mt-3";
        mensaje.textContent = error.message;

    } finally {
        boton.disabled = false;
    }
}

// Eliminar categoría
async function eliminarCategoria(id) {
    const confirmar = confirm(
        "¿Estás seguro de que deseas eliminar esta categoría?"
    );

    if (!confirmar) return;

    try {
        await enviarAPI(`/categorias/${id}`, "DELETE");

        alert("Categoría eliminada correctamente.");

        await cargarCategorias();
        await cargarDashboard();

    } catch (error) {
        alert(`No se pudo eliminar la categoría: ${error.message}`);
    }
}

// Cargar productos en el selector de movimientos
async function cargarProductosMovimiento() {
    const productos = normalizarLista(
        await consultarAPI("/productos")
    );

    const selector = document.getElementById("productoMovimiento");
    selector.replaceChildren();

    const opcionInicial = document.createElement("option");
    opcionInicial.value = "";
    opcionInicial.textContent = "Selecciona un producto";
    selector.appendChild(opcionInicial);

    productos.forEach(producto => {
        const opcion = document.createElement("option");
        opcion.value = producto.idProducto;
        opcion.textContent =
            `${producto.nombre} (Stock: ${producto.stock})`;
        selector.appendChild(opcion);
    });
}

// Mostrar el historial de movimientos
function mostrarMovimientos(movimientos) {
    const tabla = document.getElementById("tablaMovimientos");
    tabla.replaceChildren();

    if (movimientos.length === 0) {
        const fila = tabla.insertRow();
        const celda = fila.insertCell();
        celda.colSpan = 7;
        celda.className = "text-center py-4";
        celda.textContent = "No hay movimientos registrados.";
        return;
    }

    movimientos.forEach(movimiento => {
        const fila = tabla.insertRow();

        const fecha = movimiento.fecha
            ? new Date(movimiento.fecha).toLocaleString("es-CO")
            : "Sin fecha";

        const valores = [
            movimiento.idMovimiento,
            fecha,
            movimiento.nombreProducto,
            movimiento.skuProducto,
            movimiento.tipoMovimiento,
            movimiento.cantidad,
            movimiento.observacion || "Sin observación"
        ];

        valores.forEach((valor, indice) => {
            const celda = fila.insertCell();
            celda.textContent = valor ?? "";

            if (indice === 4) {
                const insignia = document.createElement("span");

                insignia.className =
                    movimiento.tipoMovimiento === "ENTRADA"
                        ? "badge text-bg-success"
                        : "badge text-bg-danger";

                insignia.textContent = movimiento.tipoMovimiento;
                celda.replaceChildren(insignia);
            }
        });
    });
}

// Actualizar resumen de movimientos
function actualizarResumenMovimientos(movimientos) {
    const entradas = movimientos.filter(
        movimiento => movimiento.tipoMovimiento === "ENTRADA"
    );

    const salidas = movimientos.filter(
        movimiento => movimiento.tipoMovimiento === "SALIDA"
    );

    actualizarNumero("totalEntradas", entradas.length);
    actualizarNumero("totalSalidas", salidas.length);
    actualizarNumero("totalMovimientos", movimientos.length);
}

// Consultar movimientos en el backend
async function cargarMovimientos() {
    const tabla = document.getElementById("tablaMovimientos");

    try {
        movimientosActuales = normalizarLista(
            await consultarAPI("/movimientos")
        );

        mostrarMovimientos(movimientosActuales);
        actualizarResumenMovimientos(movimientosActuales);

    } catch (error) {
        tabla.textContent =
            `No se pudieron cargar los movimientos: ${error.message}`;
    }
}

// Abrir formulario de movimientos
async function nuevoMovimiento() {
    document.getElementById("formMovimiento").reset();
    document.getElementById("mensajeMovimiento").textContent = "";

    try {
        await cargarProductosMovimiento();
        modalMovimiento.show();

    } catch (error) {
        alert(`No se pudieron cargar los productos: ${error.message}`);
    }
}

// Registrar entrada o salida
async function guardarMovimiento(evento) {
    evento.preventDefault();

    const tipo = document.getElementById("tipoMovimiento").value;

    const datos = {
        idProducto: Number(
            document.getElementById("productoMovimiento").value
        ),
        cantidad: Number(
            document.getElementById("cantidadMovimiento").value
        ),
        observacion: document.getElementById(
            "observacionMovimiento"
        ).value.trim()
    };

    const mensaje = document.getElementById("mensajeMovimiento");
    const boton = document.getElementById("btnGuardarMovimiento");

    boton.disabled = true;
    mensaje.textContent = "";

    try {
        const endpoint = tipo === "ENTRADA"
            ? "/movimientos/entrada"
            : "/movimientos/salida";

        await enviarAPI(endpoint, "POST", datos);

        modalMovimiento.hide();

        await cargarMovimientos();
        await cargarProductos();
        await cargarDashboard();

        alert(
            tipo === "ENTRADA"
                ? "Entrada registrada correctamente."
                : "Salida registrada correctamente."
        );

    } catch (error) {
        mensaje.className = "alert alert-danger mt-3";
        mensaje.textContent = error.message;

    } finally {
        boton.disabled = false;
    }
}


// Abrir formulario para nuevo producto
function nuevoProducto() {
    document.getElementById("formProducto").reset();
    document.getElementById("idProducto").value = "";
    document.getElementById("tituloModalProducto").textContent =
        "Registrar producto";
    document.getElementById("btnGuardarProducto").textContent =
        "Guardar producto";
    document.getElementById("mensajeProducto").textContent = "";

    modalProducto.show();
}

// Editar producto
async function editarProducto(id) {
    try {
        const producto = await consultarAPI(`/productos/${id}`);

        document.getElementById("idProducto").value = producto.idProducto;
        document.getElementById("sku").value = producto.sku;
        document.getElementById("nombre").value = producto.nombre;
        document.getElementById("descripcion").value =
            producto.descripcion || "";
        document.getElementById("precio").value = producto.precio;
        document.getElementById("stock").value = producto.stock;
        document.getElementById("stockMinimo").value = producto.stockMinimo;
        document.getElementById("idCategoria").value = producto.idCategoria;

        document.getElementById("tituloModalProducto").textContent =
            "Editar producto";
        document.getElementById("btnGuardarProducto").textContent =
            "Actualizar producto";
        document.getElementById("mensajeProducto").textContent = "";

        modalProducto.show();
    } catch (error) {
        alert(`No se pudo consultar el producto: ${error.message}`);
    }
}

// Registrar o actualizar producto
async function guardarProducto(evento) {
    evento.preventDefault();

    const id = document.getElementById("idProducto").value;

    const datos = {
        sku: document.getElementById("sku").value.trim(),
        nombre: document.getElementById("nombre").value.trim(),
        descripcion: document.getElementById("descripcion").value.trim(),
        precio: Number(document.getElementById("precio").value),
        stock: Number(document.getElementById("stock").value),
        stockMinimo: Number(document.getElementById("stockMinimo").value),
        idCategoria: Number(document.getElementById("idCategoria").value)
    };

    const mensaje = document.getElementById("mensajeProducto");
    const boton = document.getElementById("btnGuardarProducto");

    boton.disabled = true;
    mensaje.textContent = "";

    try {
        if (id) {
            await enviarAPI(`/productos/${id}`, "PUT", datos);
        } else {
            await enviarAPI("/productos", "POST", datos);
        }

        modalProducto.hide();
        await cargarProductos();
        await cargarDashboard();

        alert(id ? "Producto actualizado correctamente."
                 : "Producto registrado correctamente.");

    } catch (error) {
        mensaje.className = "alert alert-danger mt-3";
        mensaje.textContent = error.message;
    } finally {
        boton.disabled = false;
    }
}

// Eliminar producto
async function eliminarProducto(id) {
    const confirmar = confirm(
        "¿Estás seguro de que deseas eliminar este producto?"
    );

    if (!confirmar) return;

    try {
        await enviarAPI(`/productos/${id}`, "DELETE");

        alert("Producto eliminado correctamente.");

        await cargarProductos();
        await cargarDashboard();

    } catch (error) {
        alert(`No se pudo eliminar el producto: ${error.message}`);
    }
}

// Navegación entre secciones

// Navegación entre secciones

// Navegación entre secciones
function configurarNavegacion() {
    const enlaces = document.querySelectorAll(".sidebar .nav-link");

    enlaces.forEach(enlace => {
        enlace.addEventListener("click", evento => {
            evento.preventDefault();

            const seccion = enlace.dataset.seccion;

            const seccionesDisponibles = [
                "dashboard",
                "productos",
                "categorias",
                "movimientos"
            ];

            if (!seccionesDisponibles.includes(seccion)) {
                alert("Esta sección todavía está en desarrollo.");
                return;
            }

            // Ocultar todas las secciones
            seccionesDisponibles.forEach(nombre => {
                document.getElementById(`seccion-${nombre}`)
                    .classList.add("d-none");
            });

            // Mostrar la sección seleccionada
            document.getElementById(`seccion-${seccion}`)
                .classList.remove("d-none");

            // Actualizar menú activo
            enlaces.forEach(item => item.classList.remove("active"));
            enlace.classList.add("active");

            // Actualizar título superior
            const titulos = {
                dashboard: "Panel principal",
                productos: "Productos",
                categorias: "Categorías",
                movimientos: "Movimientos"
            };

            document.querySelector(".topbar h5").textContent =
                titulos[seccion];

            // Cargar información de cada sección
            if (seccion === "productos") {
                cargarProductos();
            }

            if (seccion === "categorias") {
                cargarCategorias();
            }

            if (seccion === "movimientos") {
                cargarMovimientos();
            }
        });
    });
}



// Inicializar aplicación
document.addEventListener("DOMContentLoaded", async () => {
    modalProducto = new bootstrap.Modal(
        document.getElementById("modalProducto")
    );
    modalCategoria = new bootstrap.Modal(
    document.getElementById("modalCategoria")
);
modalMovimiento = new bootstrap.Modal(
    document.getElementById("modalMovimiento")
);

    configurarNavegacion();

    document.getElementById("btnNuevoProducto")
        .addEventListener("click", nuevoProducto);

    document.getElementById("formProducto")
        .addEventListener("submit", guardarProducto);

        document.getElementById("btnNuevaCategoria")
    .addEventListener("click", nuevaCategoria);

document.getElementById("formCategoria")
    .addEventListener("submit", guardarCategoria);

    document.getElementById("btnNuevoMovimiento")
    .addEventListener("click", nuevoMovimiento);

document.getElementById("formMovimiento")
    .addEventListener("submit", guardarMovimiento);

    await cargarDashboard();

    try {
        await cargarCategorias();
    } catch (error) {
        console.error("Error cargando categorías:", error);
    }
});
