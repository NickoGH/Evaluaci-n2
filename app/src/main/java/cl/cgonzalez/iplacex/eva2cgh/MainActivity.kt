package cl.cgonzalez.iplacex.eva2cgh


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import cl.cgonzalez.iplacex.eva2cgh.db.Compra
import cl.cgonzalez.iplacex.eva2cgh.db.ComprasDao
import cl.cgonzalez.iplacex.eva2cgh.db.DBHelper



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Creamos un NavController, que controla la navegación entre diferentes composables en la app
            val navController = rememberNavController()
            // NavHost es un contenedor para la navegación entre composables
            // Declaramos "pantallaPrincipal" como la pantalla inicial.
            NavHost(navController = navController, startDestination = "pantallaPrincipal")
            // Aquí definimos cada ruta de navegación. Cada composable representa una pantalla en la app
            {
                composable("pantallaPrincipal") { PantallaPrincipalUI(navController) }
                composable("agregarElemento") { AgregarElementoUI(navController) }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalUI(navController: NavController) {
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val (compras, setCompras) = remember { mutableStateOf(listOf<Compra>()) }

    // Este bloque se ejecuta cuando se compone PantallaPrincipalUI.
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val dbHelper = DBHelper(contexto)
            val dao = ComprasDao(dbHelper)

            // Actualizamos el estado de las compras con los datos de la base de datos.
            setCompras(dao.finAll())
        }
    }

    // Creamos un Scaffold, que proporciona una estructura básica para la interfaz de usuario de la aplicación.
    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón para navegar a la pantalla de agregar elemento.
                ExtendedFloatingActionButton(
                    text = {  Text(stringResource(R.string.agregar)) },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Agregar") },
                    onClick = { navController.navigate("agregarElemento") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Botón para eliminar todos los elementos.
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.eliminar_todo))},
                    icon = { Icon(Icons.Filled.Delete, contentDescription = "Eliminar todo") },
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            val dbHelper = DBHelper(contexto)
                            val dao = ComprasDao(dbHelper)
                            dao.eliminarTodos()
                            withContext(Dispatchers.Main) {
                                // Actualizamos el estado de las compras a una lista vacía después de eliminar todos los elementos.
                                setCompras(emptyList())
                            }
                        }
                    }
                )
            }
        }
    ) {
        //Si la lista esta vacía mostrara el mensaje de "No hay productos que mostrar"
        if (compras.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.sin_productos),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )

            }
            //Si nuestra lista esta con productos los mostrara ordenados, dejando siempre el comprado abajo
            //y el producto por comprar arriba
        } else {
            LazyColumn() {
                items(compras) { compra ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                val dbHelper = DBHelper(contexto)
                                val dao = ComprasDao(dbHelper)
                                //cambia el estado del producto
                                dao.actualizar(Compra(compra.id, compra.producto, !compra.comprado))
                                setCompras(dao.finAll())
                            }
                        }) {
                            Icon( //usamos un check para comprado o un carrito para comprar
                                imageVector = if (compra.comprado) Icons.Filled.Check else Icons.Filled.ShoppingCart,
                                contentDescription = if (compra.comprado) "Comprado" else "Comprar",
                                tint = if (compra.comprado) Color.Green else LocalContentColor.current
                            )
                        }
                        Text(
                            text = compra.producto,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(2f)
                        )
                        IconButton(onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                val dbHelper = DBHelper(contexto)
                                val dao = ComprasDao(dbHelper)
                                dao.eliminar(compra.id)
                                setCompras(dao.finAll())
                            }
                        }) {
                            Icon( // usamos un Basurero de icono con tint rojo para eliminar los produectos de la lista
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarElementoUI(navController: NavController) {
    val contexto = LocalContext.current
    val (nombreProducto, setNombreProducto) = remember { mutableStateOf("") }
    val (mensaje, setMensaje) = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val mensajeProductoAgregado = stringResource(R.string.producto_agregado)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(mensaje)
        Image( //Insertamos una imagen y mostramos una imagen con un carrito de compras.
            painter = painterResource(id = R.drawable.carrito),
            contentDescription = stringResource(R.string.producto)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = nombreProducto,
            onValueChange = { setNombreProducto(it) },
            label = { Text(stringResource(R.string.producto)) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExtendedFloatingActionButton(
            text = { Text(stringResource(R.string.crear)) },
            icon = { Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.crear)) },
            onClick = {
                // Cuando se hace clic en el botón, lanzamos una corrutina para insertar el
                // producto en la base de datos.
                coroutineScope.launch(Dispatchers.IO) {
                    val dbHelper = DBHelper(contexto)
                    val dao = ComprasDao(dbHelper)
                    dao.insertar(Compra(0, nombreProducto, false))
                    withContext(Dispatchers.Main) {
                        // Actualizamos el estado del nombre del producto
                        // y mostramos el mensaje de producto agregado
                        setNombreProducto("")
                        setMensaje(mensajeProductoAgregado)
                    }
                }
            }
        )
        Spacer(modifier = Modifier.weight(2f))
        // Creamos una fila al final de la pantalla con un
        // botón flotante para volver a la pantalla principal.
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.volver)) },
                icon = { Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.volver)) },
                onClick = { navController.navigate("pantallaPrincipal") }
            )
        }
    }
}