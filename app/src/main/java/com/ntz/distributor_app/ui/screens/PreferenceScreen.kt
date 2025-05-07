package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntz.distributor_app.data.model.UserPreferences
import com.ntz.distributor_app.ui.viewmodel.PreferenceUiState
import com.ntz.distributor_app.ui.viewmodel.PreferenceViewModel

// Contoh Kategori (ambil dari sumber data dinamis jika perlu)
val allCategories = listOf("Makanan", "Minuman Ringan", "Kebutuhan Pokok", "Elektronik", "ATK", "Pembersih", "Gadget")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    preferenceViewModel: PreferenceViewModel = hiltViewModel(),
    onSaveComplete: () -> Unit // Callback saat selesai simpan
) {
    val uiState by preferenceViewModel.uiState.collectAsState()
    // State lokal untuk menampung preferensi yang sedang diedit user di UI
    val editablePreferences by preferenceViewModel._editablePreferences.collectAsState()

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    var showSaveSuccessSnackbar by remember { mutableStateOf(false) }


    // Efek untuk menampilkan snackbar saat save berhasil (jika diperlukan)
    LaunchedEffect(showSaveSuccessSnackbar) {
        if (showSaveSuccessSnackbar) {
            snackbarHostState.showSnackbar(
                message = "Preferensi berhasil disimpan!",
                duration = SnackbarDuration.Short
            )
            showSaveSuccessSnackbar = false // Reset state
            onSaveComplete() // Panggil callback setelah snackbar hilang (atau langsung)
        }
    }
    // Efek untuk menampilkan snackbar saat error loading/saving
    LaunchedEffect(uiState) {
        if (uiState is PreferenceUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (uiState as PreferenceUiState.Error).message,
                duration = SnackbarDuration.Long
            )
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Preferensi Distributor") },
                // Tombol kembali (opsional, tergantung alur navigasi)
//                navigationIcon = {
//                    IconButton(onClick = onSaveComplete) { // Atau navController.popBackStack()
//                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
//                    }
//                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    preferenceViewModel.savePreferences()
                    // Tampilkan indikator loading atau nonaktifkan tombol saat saving
                    showSaveSuccessSnackbar = true // Tampilkan notif sukses
                },
                text = { Text("Simpan Preferensi") },
                icon = { /* Icon Simpan */ }
            )
        }
    ) { paddingValues ->

        when (val state = uiState) {
            is PreferenceUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PreferenceUiState.Success, is PreferenceUiState.Error -> { // Tetap tampilkan UI walau ada error awal
                // Gunakan editablePreferences untuk menampilkan & mengedit
                val currentPrefs = editablePreferences ?: UserPreferences() // Default jika null

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // Agar bisa discroll
                ) {
                    Text("Pilih Kategori Produk:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Gunakan Grid atau Column untuk menampilkan Checkbox
                    allCategories.forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = currentPrefs.preferredCategories.contains(category),
                                onCheckedChange = { isChecked ->
                                    preferenceViewModel.updateEditableCategory(category, isChecked)
                                }
                            )
                            Text(category, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Lokasi Prioritas:", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = currentPrefs.preferredLocation,
                        onValueChange = { preferenceViewModel.updateEditableLocation(it) },
                        label = { Text("Contoh: Jakarta Selatan, Bandung") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Batas Minimal Order (Opsional):", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        // Konversi Int? ke String, tampilkan "" jika null
                        value = currentPrefs.minOrder?.toString() ?: "",
                        onValueChange = { newValue ->
                            // Hanya terima angka atau string kosong
                            if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                preferenceViewModel.updateEditableMinOrder(newValue)
                            }
                        },
                        label = { Text("Contoh: 500000 (tanpa titik/koma)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
    }
}