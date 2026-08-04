package com.examtracker.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.examtracker.app.R

@Composable
fun SessionCompletionDialog(
    focusMinutes: Int,
    onDismiss: () -> Unit,
    onSave: (
        correctCount: Int,
        wrongCount: Int,
        blankCount: Int,
        note: String?
    ) -> Unit
) {
    var correctText by remember {
        mutableStateOf("0")
    }

    var wrongText by remember {
        mutableStateOf("0")
    }

    var blankText by remember {
        mutableStateOf("0")
    }

    var noteText by remember {
        mutableStateOf("")
    }

    fun parseNonNegativeInt(text: String): Int {
        return text.toIntOrNull()
            ?.coerceAtLeast(0)
            ?: 0
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(
                    id = R.string
                        .session_completion_dialog_title
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(
                        id = R.string
                            .session_completion_dialog_focus_summary_format,
                        focusMinutes
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                CompletionNumberField(
                    value = correctText,
                    onValueChange = {
                        correctText = it
                    },
                    label = stringResource(
                        id = R.string
                            .session_completion_correct_label
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                CompletionNumberField(
                    value = wrongText,
                    onValueChange = {
                        wrongText = it
                    },
                    label = stringResource(
                        id = R.string
                            .session_completion_wrong_label
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                CompletionNumberField(
                    value = blankText,
                    onValueChange = {
                        blankText = it
                    },
                    label = stringResource(
                        id = R.string
                            .session_completion_blank_label
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = {
                        noteText = it
                    },
                    label = {
                        Text(
                            text = stringResource(
                                id = R.string
                                    .session_completion_note_label
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        parseNonNegativeInt(correctText),
                        parseNonNegativeInt(wrongText),
                        parseNonNegativeInt(blankText),
                        noteText
                    )
                }
            ) {
                Text(
                    text = stringResource(
                        id = R.string.action_save
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(
                        id = R.string.action_cancel
                    )
                )
            }
        }
    )
}

@Composable
private fun CompletionNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (
                newValue.isEmpty() ||
                newValue.all { character ->
                    character.isDigit()
                }
            ) {
                onValueChange(newValue)
            }
        },
        label = {
            Text(text = label)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier.fillMaxWidth()
    )
}