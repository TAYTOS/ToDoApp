package com.example.todoapp

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ── Color Palette ──────────────────────────────────────────────────────────────
private val BgDark      = Color(0xFF0E1117)
private val CardBg      = Color(0xFF181D27)
private val CardBg2     = Color(0xFF1F2535)
private val AccentGreen = Color(0xFF00E5A0)
private val AccentAmber = Color(0xFFFFB830)
private val AccentRed   = Color(0xFFFF4F6A)
private val AccentBlue  = Color(0xFF4FC3F7)
private val TextMain    = Color(0xFFECEFF4)
private val TextMuted   = Color(0xFF6B7490)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(vm: TodoViewModel = viewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val focus = LocalFocusManager.current
    val done  = state.todos.count { it.isCompleted }
    val total = state.todos.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // Subtle gradient orbs in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(AccentGreen.copy(alpha = 0.05f), 500f, Offset(size.width, 0f))
            drawCircle(AccentBlue.copy(alpha = 0.04f),  400f, Offset(0f, size.height * .5f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── HEADER ──────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(start = 26.dp, end = 26.dp, top = 32.dp, bottom = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .background(AccentGreen, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "TO·DO APP",
                        color      = AccentGreen,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Mis\nTareas",
                    color      = TextMain,
                    fontSize   = 38.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 42.sp,
                    letterSpacing = (-1).sp
                )
                if (total > 0) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "$done de $total completadas",
                            color    = TextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            "${if (total > 0) (done * 100 / total) else 0}%",
                            color      = AccentGreen,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress      = { if (total > 0) done.toFloat() / total else 0f },
                        modifier      = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(50)),
                        color         = AccentGreen,
                        trackColor    = CardBg2
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── INPUT CARD ──────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBg)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value         = state.inputText,
                    onValueChange = vm::onInputChange,
                    modifier      = Modifier.fillMaxWidth(),
                    placeholder   = { Text("Nueva tarea…", color = TextMuted, fontSize = 14.sp) },
                    leadingIcon   = {
                        Icon(Icons.Outlined.Add, null, tint = AccentGreen)
                    },
                    trailingIcon  = {
                        if (state.inputText.isNotBlank())
                            IconButton(onClick = { vm.onInputChange("") }) {
                                Icon(Icons.Default.Close, null, tint = TextMuted)
                            }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        vm.addTodo(); focus.clearFocus()
                    }),
                    singleLine = true,
                    shape      = RoundedCornerShape(14.dp),
                    colors     = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = AccentGreen,
                        unfocusedBorderColor    = CardBg2,
                        focusedTextColor        = TextMain,
                        unfocusedTextColor      = TextMain,
                        cursorColor             = AccentGreen,
                        focusedContainerColor   = CardBg2,
                        unfocusedContainerColor = CardBg2
                    )
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Priority chips
                    Text("Prioridad:", color = TextMuted, fontSize = 11.sp)
                    Priority.entries.forEach { p ->
                        PriorityChip(p, isSelected = state.selectedPriority == p) {
                            vm.onPriorityChange(p)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    // Confirm button
                    Button(
                        onClick  = { vm.addTodo(); focus.clearFocus() },
                        enabled  = state.inputText.isNotBlank(),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = AccentGreen,
                            contentColor           = BgDark,
                            disabledContainerColor = CardBg2,
                            disabledContentColor   = TextMuted
                        ),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Agregar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── TASK LIST ────────────────────────────────────────────
            if (state.todos.isEmpty()) {
                EmptyState()
            } else {
                // Section label
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "TAREAS PENDIENTES",
                        color         = TextMuted,
                        fontSize      = 10.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        "$total ${if (total == 1) "tarea" else "tareas"}",
                        color    = TextMuted,
                        fontSize = 10.sp
                    )
                }
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier        = Modifier.fillMaxSize(),
                    contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(state.todos, key = { _, it -> it.id }) { _, item ->
                        AnimatedContent(
                            targetState = state.editingId == item.id,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "card_${item.id}"
                        ) { isEditing ->
                            if (isEditing) {
                                EditCard(
                                    text      = state.editingText,
                                    onChange  = vm::onEditTextChange,
                                    onConfirm = vm::confirmEdit,
                                    onCancel  = vm::cancelEdit
                                )
                            } else {
                                TodoCard(
                                    item     = item,
                                    onToggle = { vm.toggleComplete(item.id) },
                                    onEdit   = { vm.startEditing(item.id) },
                                    onDelete = { vm.deleteTodo(item.id) }
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

// ── Priority Chip ─────────────────────────────────────────────────────────────

@Composable
private fun PriorityChip(p: Priority, isSelected: Boolean, onClick: () -> Unit) {
    val (label, color) = when (p) {
        Priority.LOW    -> "Baja"  to AccentGreen
        Priority.MEDIUM -> "Media" to AccentAmber
        Priority.HIGH   -> "Alta"  to AccentRed
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) color.copy(.18f) else CardBg2)
            .border(1.dp, if (isSelected) color else Color.Transparent, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = if (isSelected) color else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// ── Todo Card ─────────────────────────────────────────────────────────────────

@Composable
private fun TodoCard(
    item: TodoItem,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = when (item.priority) {
        Priority.LOW    -> AccentGreen
        Priority.MEDIUM -> AccentAmber
        Priority.HIGH   -> AccentRed
    }
    val priorityLabel = when (item.priority) {
        Priority.LOW    -> "● Baja"
        Priority.MEDIUM -> "● Media"
        Priority.HIGH   -> "● Alta"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            // Colored left border via drawBehind
            .drawBehind {
                drawLine(
                    color       = accentColor,
                    start       = Offset(0f, 20f),
                    end         = Offset(0f, size.height - 20f),
                    strokeWidth = 4f,
                    cap         = StrokeCap.Round
                )
            }
            .padding(start = 18.dp, end = 6.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox with custom colors
        Checkbox(
            checked         = item.isCompleted,
            onCheckedChange = { onToggle() },
            modifier        = Modifier.size(22.dp),
            colors          = CheckboxDefaults.colors(
                checkedColor   = AccentGreen,
                uncheckedColor = TextMuted,
                checkmarkColor = BgDark
            )
        )
        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text           = item.text,
                color          = if (item.isCompleted) TextMuted else TextMain,
                fontSize       = 15.sp,
                fontWeight     = FontWeight.SemiBold,
                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines       = 2,
                overflow       = TextOverflow.Ellipsis,
                lineHeight     = 20.sp
            )
            Spacer(Modifier.height(3.dp))
            Text(priorityLabel, color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }

        // Edit button
        IconButton(onClick = onEdit, modifier = Modifier.size(38.dp)) {
            Icon(
                Icons.Outlined.Edit,
                contentDescription = "Editar",
                tint     = AccentBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        // Delete button
        IconButton(onClick = onDelete, modifier = Modifier.size(38.dp)) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = "Eliminar",
                tint     = AccentRed,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── Edit Card ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCard(
    text: String,
    onChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg2)
            .border(1.dp, AccentBlue.copy(.4f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        OutlinedTextField(
            value         = text,
            onValueChange = onChange,
            modifier      = Modifier.fillMaxWidth(),
            label         = { Text("Editar tarea", color = AccentBlue, fontSize = 12.sp) },
            singleLine    = false,
            maxLines      = 3,
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = AccentBlue,
                unfocusedBorderColor    = CardBg,
                focusedTextColor        = TextMain,
                unfocusedTextColor      = TextMain,
                cursorColor             = AccentBlue,
                focusedContainerColor   = CardBg,
                unfocusedContainerColor = CardBg
            )
        )
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancelar", color = TextMuted, fontSize = 13.sp)
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onConfirm,
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor   = BgDark
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(5.dp))
                Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(90.dp)
                    .background(
                        Brush.radialGradient(listOf(AccentGreen.copy(.12f), Color.Transparent)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint     = AccentGreen,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "¡Sin tareas!",
                color      = TextMain,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Agrega tu primera tarea arriba.",
                color    = TextMuted,
                fontSize = 14.sp
            )
        }
    }
}
