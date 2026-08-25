package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Preset
import com.example.models.HeadphoneProfile
import com.example.models.defaultHeadphoneProfiles
import com.example.ui.components.PremiumEqBand
import com.example.ui.components.RotaryKnob
import com.example.viewmodel.MainViewModel
import com.example.visualizer.AudioVisualizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val preset by viewModel.currentPreset.collectAsStateWithLifecycle()
    val profile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val isEqEnabled by viewModel.eqEnabled.collectAsStateWithLifecycle()
    
    val bands by viewModel.bands.collectAsStateWithLifecycle()
    val bassAmount by viewModel.bassAmount.collectAsStateWithLifecycle()
    val midsAmount by viewModel.midsAmount.collectAsStateWithLifecycle()
    val trebleAmount by viewModel.trebleAmount.collectAsStateWithLifecycle()
    val preamp by viewModel.preamp.collectAsStateWithLifecycle()
    
    var showPresetSheet by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Bluetooth",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.setEqEnabled(!isEqEnabled) }) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Power",
                            tint = if (isEqEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    IconButton(onClick = { /* Open Settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Visualizer Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                AudioVisualizer(isPlaying = isEqEnabled, modifier = Modifier.padding(16.dp))
                
                // Status Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(if (isEqEnabled) com.example.ui.theme.SuccessGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEqEnabled) "DSP ACTIVE" else "BYPASS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Preset & Profile Selectors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SelectorCard(
                    title = "PRESET",
                    value = preset.name,
                    onClick = { showPresetSheet = true },
                    modifier = Modifier.weight(1f)
                )
                SelectorCard(
                    title = "HEADPHONE",
                    value = profile.name,
                    onClick = { showProfileSheet = true },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Master Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PremiumEqBand(
                    label = "PREAMP",
                    value = preamp,
                    onValueChange = { viewModel.updatePreamp(it) }
                )
                
                RotaryKnob(
                    value = bassAmount,
                    onValueChange = { viewModel.updateBass(it) },
                    valueRange = -10f..10f,
                    label = "BASS BOOST",
                    modifier = Modifier.size(120.dp)
                )
                
                PremiumEqBand(
                    label = "TREBLE",
                    value = trebleAmount,
                    onValueChange = { viewModel.updateTreble(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 10-Band EQ
            Text(
                text = "PARAMETRIC EQ",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val labels = listOf("31", "62", "125", "250", "500", "1k", "2k", "4k", "8k", "16k")
                    bands.forEachIndexed { index, value ->
                        PremiumEqBand(
                            label = labels[index],
                            value = value,
                            onValueChange = { viewModel.updateBand(index, it) }
                        )
                    }
                }
            }
            
            // Bottom Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.resetCurrent() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                }
                
                IconButton(onClick = { viewModel.setEqEnabled(!isEqEnabled) }) {
                    Icon(imageVector = Icons.Default.Compare, contentDescription = "A/B Compare", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Preset Bottom Sheet
        if (showPresetSheet) {
            val allPresets by viewModel.allPresets.collectAsStateWithLifecycle()
            
            ModalBottomSheet(
                onDismissRequest = { showPresetSheet = false },
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Preset", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(allPresets) { item ->
                            PresetChip(
                                name = item.name,
                                isSelected = item.id == preset.id,
                                onClick = {
                                    viewModel.selectPreset(item)
                                    showPresetSheet = false
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
        
        // Profile Bottom Sheet
        if (showProfileSheet) {
            ModalBottomSheet(
                onDismissRequest = { showProfileSheet = false },
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Headphone Profile", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    defaultHeadphoneProfiles.forEach { item ->
                        ProfileRow(
                            profile = item,
                            isSelected = item.id == profile.id,
                            onClick = {
                                viewModel.setProfile(item)
                                showProfileSheet = false
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun SelectorCard(title: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PresetChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileRow(profile: HeadphoneProfile, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = profile.name,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
