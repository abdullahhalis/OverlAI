package com.abdullahhalis.overlai.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abdullahhalis.overlai.R
import com.abdullahhalis.overlai.presentation.main.component.LanguagePicker
import com.abdullahhalis.overlai.utils.OcrLanguage
import com.abdullahhalis.overlai.utils.TranslationLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isOverlayRunning: Boolean,
    onStartOverlay: () -> Unit,
    onStopOverlay: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val sourceLanguage by viewModel.sourceLanguage.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_overlai),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(96.dp)
            )
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                "Translate Anything on Your Screen",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.weight(1.5f))
            Column(
                modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LanguagePicker(
                    label = "Translate From",
                    options = OcrLanguage.entries.map { language ->
                        language.name.lowercase().replaceFirstChar { it.uppercase() }
                    },
                    selected = sourceLanguage.name.lowercase().replaceFirstChar { it.uppercase() },
                    onSelected = { viewModel.setSourceLanguage(OcrLanguage.valueOf(it.uppercase()))}
                )
                Icon(
                    imageVector = Icons.Outlined.SwapVert,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .padding(8.dp)
                        .size(24.dp)
                )
                LanguagePicker(
                    label = "Translate To",
                    options = TranslationLanguage.entries.map { language ->
                        language.name.lowercase().replaceFirstChar { it.uppercase() }
                    },
                    selected = targetLanguage.name.lowercase().replaceFirstChar { it.uppercase() },
                    onSelected = { viewModel.setTargetLanguage(TranslationLanguage.valueOf(it.uppercase()))}
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onStartOverlay,
                shape = RoundedCornerShape(8.dp),
                enabled = !isOverlayRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayCircleOutline,
                        contentDescription = "start overlay"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Start Overlay",
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStopOverlay,
                shape = RoundedCornerShape(8.dp),
                enabled = isOverlayRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant

                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.StopCircle,
                        contentDescription = "stop overlay"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Stop Overlay",
                    )
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}