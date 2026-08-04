package com.examtracker.app.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examtracker.app.R
import com.examtracker.app.settings.AppTheme
import com.examtracker.app.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val appTheme by
    viewModel.appTheme.collectAsStateWithLifecycle()

    val customBackgroundUri by
    viewModel.customBackgroundUri.collectAsStateWithLifecycle()

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                try {
                    context.contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                } catch (_: SecurityException) {
                    // Bazı cihaz veya picker uygulamaları kalıcı izin
                    // vermeyebilir. URI yine mevcut oturumda kullanılabilir.
                }

                viewModel.setCustomBackgroundUri(
                    uri.toString()
                )
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.settings_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                stringResource(
                                    id = R.string.content_description_back
                                )
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            Text(
                text = stringResource(
                    id = R.string.settings_theme_section_title
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {
                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_system
                        ),
                        selected = appTheme == AppTheme.SYSTEM,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.SYSTEM
                            )
                        }
                    )

                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_light
                        ),
                        selected = appTheme == AppTheme.LIGHT,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.LIGHT
                            )
                        }
                    )

                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_dark
                        ),
                        selected = appTheme == AppTheme.DARK,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.DARK
                            )
                        }
                    )

                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_night_blue
                        ),
                        selected = appTheme == AppTheme.NIGHT_BLUE,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.NIGHT_BLUE
                            )
                        }
                    )

                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_light_paper
                        ),
                        selected = appTheme == AppTheme.LIGHT_PAPER,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.LIGHT_PAPER
                            )
                        }
                    )

                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_dark_grid
                        ),
                        selected = appTheme == AppTheme.DARK_GRID,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.DARK_GRID
                            )
                        }
                    )

                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_amoled
                        ),
                        selected = appTheme == AppTheme.AMOLED,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.AMOLED
                            )
                        }
                    )

                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_forest
                        ),
                        selected = appTheme == AppTheme.FOREST,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.FOREST
                            )
                        }
                    )

                    ThemeSelectionRow(
                        label = stringResource(
                            id = R.string.settings_theme_sunset
                        ),
                        selected = appTheme == AppTheme.SUNSET,
                        onClick = {
                            viewModel.setAppTheme(
                                AppTheme.SUNSET
                            )
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = stringResource(
                    id = R.string.settings_custom_background_title
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = stringResource(
                    id = R.string.settings_custom_background_description
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (customBackgroundUri == null) {
                        Text(
                            text = stringResource(
                                id = R.string.settings_no_custom_background
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = stringResource(
                                id = R.string.settings_custom_background_selected
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = customBackgroundUri.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Button(
                        onClick = {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts
                                        .PickVisualMedia
                                        .ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.settings_choose_background_photo
                            )
                        )
                    }

                    if (customBackgroundUri != null) {
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        OutlinedButton(
                            onClick = {
                                viewModel.setCustomBackgroundUri(
                                    null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.settings_remove_background_photo
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeSelectionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text = label,
            modifier = Modifier.padding(
                start = 8.dp
            )
        )
    }
}