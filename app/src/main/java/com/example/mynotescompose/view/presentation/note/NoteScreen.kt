package com.example.mynotescompose.view.presentation.note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mynotescompose.core.navigation.Screen
import com.example.mynotescompose.view.presentation.note.componenets.NoteItem
import com.example.mynotescompose.view.presentation.note.componenets.OrderSection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    navigator: NavController,
    vm: NoteScreenViewModel = hiltViewModel()
) {
    val state = vm.state.value
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = scaffoldState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigator.navigate(Screen.AddEditNoteScreen.route) },
                containerColor=  MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "iNotes",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(onClick = { vm.onEvent(NoteEvent.ToggleOrderSection) }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                        )
                    }
                }
                AnimatedVisibility(
                    visible = state.isOrderSectionVisible,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    OrderSection(
                        modifier = Modifier.fillMaxWidth(),
                        noteOrder = state.noteOrder,
                        onOrderChange = { vm.onEvent(NoteEvent.Order(it)) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.notes) { note ->
                        NoteItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // navigate to certain note using it's id
                                    navigator.navigate(
                                        Screen.AddEditNoteScreen.route
                                                + "?noteId=${note.id}&noteColor=${note.color}"
                                    )
                                },
                            note = note,
                            onDeleteClick = {
                                vm.onEvent(NoteEvent.DeleteNote(note))
                                coroutineScope.launch {
                                    val result = scaffoldState.showSnackbar(
                                        message = "Note deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        vm.onEvent(NoteEvent.RestoreNote)
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )

}


@Preview(showBackground = true)
@Composable
fun NoteScreenPreview() {
    NoteScreen(navigator = rememberNavController())
}