package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntz.distributor_app.data.model.Distributor
import com.ntz.distributor_app.ui.viewmodel.* // Import semua state

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    distributorViewModel: DistributorViewModel = hiltViewModel(), // Inject DistributorViewModel
    // authViewModel: AuthViewModel = hiltViewModel(), // Inject AuthViewModel jika perlu info user
    onNavigateToPreferences: () -> Unit,
    onLogout: () -> Unit
) {
    val allDistributorsState by distributorViewModel._allDistributorsState.collectAsState()
    val recommendationState by distributorViewModel.recommendationState.collectAsState()

    // State untuk menampilkan dialog logout
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Distributor App") },
                actions = {
                    // Tombol Refresh Distributor
                    IconButton(onClick = { distributorViewModel.refreshDistributors() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh Data")
                    }
                    // Tombol ke Preferences
                    IconButton(onClick = onNavigateToPreferences) {
                        Icon(Icons.Filled.Settings, contentDescription = "Buka Preferensi")
                    }
                    // Tombol Logout
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            val isLoadingRecommendation = recommendationState is RecommendationUiState.Loading
            ExtendedFloatingActionButton(
                onClick = { distributorViewModel.getRecommendations() },
                // Nonaktifkan tombol saat sedang loading rekomendasi
                // enabled = !isLoadingRecommendation, // Ganti jadi modifier di Column nanti
                text = { Text("Dapatkan Rekomendasi") },
                icon = { /* Icon */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --- Bagian Rekomendasi ---
            RecommendationSection(recommendationState)

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // --- Bagian Semua Distributor ---
            AllDistributorsSection(allDistributorsState)
        }
    }

    // Dialog Konfirmasi Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout() // Panggil fungsi logout dari callback
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                Button(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


@Composable
fun RecommendationSection(state: RecommendationUiState) {
    Column {
        Text("Rekomendasi Untuk Anda", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        when (state) {
            is RecommendationUiState.Idle -> {
                Text("Tekan tombol 'Dapatkan Rekomendasi'.")
            }
            is RecommendationUiState.Loading -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mencari rekomendasi...")
                }
            }
            is RecommendationUiState.Success -> {
                if (state.recommendedDistributors.isEmpty()) {
                    Text("Tidak ada distributor yang cocok dengan preferensi Anda saat ini.")
                } else {
                    // Tampilkan hasil rekomendasi (mungkin dalam LazyRow atau Column terbatas)
                    LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) { // Batasi tinggi
                        items(state.recommendedDistributors) { distributor ->
                            // Gunakan Item yang sama tapi dengan highlight
                            DistributorItemView(distributor = distributor, isRecommended = true)
                        }
                    }
                }
            }
            is RecommendationUiState.Error -> {
                Text("Gagal mendapatkan rekomendasi: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


@Composable
fun AllDistributorsSection(state: AllDistributorsUiState) {
    Column(modifier = Modifier.fillMaxWidth()) { // Ambil sisa ruang
        Text("Semua Distributor", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        when (state) {
            is AllDistributorsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is AllDistributorsUiState.Success -> {
                if (state.distributors.isEmpty()) {
                    Text("Tidak ada data distributor.")
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) { // Ambil sisa ruang vertikal
                        items(state.distributors, key = { it.id }) { distributor ->
                            DistributorItemView(distributor = distributor)
                        }
                    }
                }
            }
            is AllDistributorsUiState.Error -> {
                Text("Gagal memuat daftar: ${state.message}", color = MaterialTheme.colorScheme.error)
                // Mungkin tambahkan tombol retry
                Button(onClick = { /* Panggil refresh dari ViewModel */ }) {
                    Text("Coba Lagi")
                }
            }
        }
    }
}


// Composable terpisah untuk menampilkan item distributor
@Composable
fun DistributorItemView(distributor: Distributor, isRecommended: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isRecommended) 4.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRecommended) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            Text(distributor.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Lokasi: ${distributor.location}", style = MaterialTheme.typography.bodyMedium)
            Text("Kategori: ${distributor.productCategories.joinToString()}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Detail: ${distributor.details}", style = MaterialTheme.typography.bodySmall)
            // Tambahkan gambar jika ada URL
            // if (distributor.imageUrl != null) { AsyncImage(...) }
        }
    }
}