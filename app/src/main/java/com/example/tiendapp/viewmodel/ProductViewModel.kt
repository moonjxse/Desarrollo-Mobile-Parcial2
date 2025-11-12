package com.example.tiendapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendapp.data.AppDatabase
import com.example.tiendapp.data.Product
import com.example.tiendapp.model.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository

    init {
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)

        viewModelScope.launch(Dispatchers.IO) {
            val count = repository.countProducts()
            if (count == 0) {
                loadProductsFromAssets(application)
            }
        }
    }

    val allProducts = repository.allProducts

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            repository.insertProduct(product)
            _message.value = "Producto agregado correctamente"
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
            _message.value = "Producto actualizado correctamente"
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _message.value = "Producto eliminado correctamente"
        }
    }

    fun clearMessage() {
        _message.value = null
    }


    private suspend fun loadProductsFromAssets(application: Application) {
        try {

            val inputStream = application.assets.open("products.json")
            val json = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            val jsonArray = JSONArray(json)

            val context = getApplication<Application>().applicationContext
            val packageName = context.packageName


            val imageMap = mapOf(

                "Auriculares Bluetooth" to "audifonos",
                "Smartwatch Deportivo" to "relojdeportivo",
                "Cámara 4K Compacta" to "camara",
                "Teclado Mecánico RGB" to "tecladomecanicorgb"

            )

            val productList = mutableListOf<Product>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val nombre = obj.getString("nombre")
                val descripcion = obj.optString("descripcion", "")
                val precio = if (obj.has("precio")) {

                    obj.getDouble("precio").toInt()
                } else {
                    obj.optInt("precio", 0)
                }
                val stock = obj.optInt("stock", 10)


                val imageName = imageMap[nombre] ?: "logo"

                val resId = context.resources.getIdentifier(imageName, "drawable", packageName)

                // construir URI que Coil entiende para recursos locales
                // si resId == 0 (no existe), apuntamos al recurso por defecto
                val finalResId = if (resId != 0) resId else {
                    context.resources.getIdentifier("logo", "drawable", packageName)
                }
                val resourceUri = "android.resource://$packageName/$finalResId"


                val product = Product(
                    id = 0,
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    imagenUrl = resourceUri,
                    stock = stock
                )

                productList.add(product)
            }

            repository.insertAll(productList)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
