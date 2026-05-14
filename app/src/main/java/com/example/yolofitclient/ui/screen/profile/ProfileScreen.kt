package com.example.yolofitclient.ui.screen.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.yolofitclient.data.dto.UserDto
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.ui.theme.AuthColors
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape


@Composable
fun ProfileScreen(
//    onSaveClick: (UserDto) -> Unit = {},
//    onLogoutClick: () -> Unit = {}
) {

    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedBirthDate by remember { mutableStateOf("") }
    var editedGender by remember { mutableStateOf("") }
    var editedHeight by remember { mutableStateOf("") }
    var editedWeight by remember { mutableStateOf("") }
    var editedFitnessLevel by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var showGenderDropdown by remember { mutableStateOf(false) }
    var showFitnessDropdown by remember { mutableStateOf(false) }

    val genders = listOf("Мужской", "Женский")
    val fitnessLevels = listOf("Начинающий", "Средний", "Продвинутый", "Профессионал")

    var user by remember { mutableStateOf<UserDto?>(null) }

    LaunchedEffect(Unit) {
        val currentUser = AuthLocalDataSource.getCurrentUser()
        user = currentUser
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    Box(
        modifier = Modifier.fillMaxSize().background(AuthColors.Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuthColors.AccentGreen.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.05f),
                    radius = size.minDimension * 0.7f
                ),
                radius = size.minDimension * 0.7f,
                center = Offset(size.width * 0.5f, size.height * 0.05f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuthColors.AccentGreenDark.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.8f),
                    radius = size.minDimension * 0.5f
                ),
                radius = size.minDimension * 0.5f,
                center = Offset(size.width * 0.8f, size.height * 0.8f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Кнопка редактирования вверху
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ПРОФИЛЬ",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = AuthColors.TextPrimary,
                            letterSpacing = 2.sp,
                            fontSize = 32.sp
                        )
                    )

                    IconButton(
                        onClick = {
                            if (isEditing) {
                                // Сохраняем изменения
                                val updatedUser = user?.copy(
                                    name = editedName,
                                    email = editedEmail.ifEmpty { null },
                                    birthDate = editedBirthDate.ifEmpty { null },
                                    gender = editedGender,
                                    height = editedHeight,
                                    weight = editedWeight,
                                    fitnessLevel = editedFitnessLevel,
                                    photoUrl = photoUri?.toString() ?: user!!.photoUrl
                                )
//                                onSaveClick(updatedUser)
                            }
                            isEditing = !isEditing
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isEditing) AuthColors.AccentGreen.copy(alpha = 0.2f)
                                else AuthColors.FieldBackground
                            )
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Сохранить" else "Редактировать",
                            tint = if (isEditing) AuthColors.AccentGreen else AuthColors.TextSecondary
                        )
                    }
                }
            }

            // Аватар
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        AuthColors.AccentGreen.copy(alpha = 0.3f),
                                        AuthColors.AccentGreenDark.copy(alpha = 0.15f)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                )
                            )
                            .then(
                                if (isEditing) {
                                    Modifier.clickable { imagePickerLauncher.launch("image/*") }
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri != null) {
                            // Выбранное фото
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photoUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Аватар",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else if (user?.photoUrl != null) {
                            // Фото с сервера
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user!!.photoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Аватар",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Заглушка
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AuthColors.AccentGreen,
                                modifier = Modifier.size(70.dp)
                            )
                        }

                        // Кнопка камеры при редактировании
                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 8.dp, y = 8.dp)
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AuthColors.AccentGreen)
                                    .border(3.dp, AuthColors.Background, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Изменить фото",
                                    tint = AuthColors.Background,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Имя (всегда показываем)
            item {
                if (isEditing) {
                    EditTextField(
                        value = editedName ?: "",
                        onValueChange = { editedName = it },
                        label = "Имя",
                        icon = Icons.Default.Person
                    )
                } else {
                    Text(
                        text = user?.name ?: "",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AuthColors.TextPrimary,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = user?.fitnessLevel ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AuthColors.AccentGreen,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                SectionTitle("ЛИЧНАЯ ИНФОРМАЦИЯ")
            }

            // Информационные поля
            item {
                InfoField(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = user?.email ?: "Не указан",
                    isEditing = isEditing,
                    editedValue = editedEmail,
                    onEditValueChange = { editedEmail = it },
                    keyboardType = KeyboardType.Email
                )
            }

            item {
                InfoField(
                    icon = Icons.Default.CalendarMonth,
                    label = "Дата рождения",
                    value = user?.birthDate ?: "Не указана",
                    isEditing = isEditing,
                    editedValue = editedBirthDate,
                    onEditValueChange = { editedBirthDate = it },
                    placeholder = "ГГГГ-ММ-ДД"
                )
            }

            item {
                GenderField(
                    isEditing = isEditing,
                    gender = if (isEditing) editedGender else user?.gender,
                    onGenderChange = { editedGender = it },
                    genders = genders,
                    showDropdown = showGenderDropdown,
                    onDropdownToggle = { showGenderDropdown = it }
                )
            }

            // Заголовок секции
            item {
                SectionTitle("ФИЗИЧЕСКИЕ ПАРАМЕТРЫ")
            }

            // Физические параметры
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PhysicalField(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Height,
                        label = "Рост",
                        value = "${user?.height} см",
                        isEditing = isEditing,
                        editedValue = editedHeight ?: "",
                        onEditValueChange = { editedHeight = it },
                        suffix = "см"
                    )

                    PhysicalField(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.MonitorWeight,
                        label = "Вес",
                        value = "${user?.weight} кг",
                        isEditing = isEditing,
                        editedValue = editedWeight ?: "",
                        onEditValueChange = { editedWeight = it },
                        suffix = "кг"
                    )
                }
            }

            item {
                FitnessLevelField(
                    isEditing = isEditing,
                    fitnessLevel = if (isEditing) editedFitnessLevel else user?.fitnessLevel,
                    onLevelChange = { editedFitnessLevel = it },
                    levels = fitnessLevels,
                    showDropdown = showFitnessDropdown,
                    onDropdownToggle = { showFitnessDropdown = it }
                )
            }

            // Кнопки действий
            item {
                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    Button(
                        onClick = {
                            val updatedUser = user?.copy(
                                name = editedName,
                                email = editedEmail.ifEmpty { null },
                                birthDate = editedBirthDate.ifEmpty { null },
                                gender = editedGender,
                                height = editedHeight,
                                weight = editedWeight,
                                fitnessLevel = editedFitnessLevel,
                                photoUrl = photoUri?.toString() ?: user!!.photoUrl
                            )
//                            onSaveClick(updatedUser)
                            isEditing = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = DiagonalRoundedCornerShape(
                            topLeft = 40f,
                            topRight = 16f,
                            bottomRight = 40f,
                            bottomLeft = 16f
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            AuthColors.AccentGreen,
                                            AuthColors.AccentGreenDark
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AuthColors.Background
                                )
                                Text(
                                    text = "Сохранить изменения",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AuthColors.Background
                                    )
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = DiagonalRoundedCornerShape(
                        topLeft = 16f,
                        topRight = 40f,
                        bottomRight = 16f,
                        bottomLeft = 40f
                    ),
                    border = BorderStroke(1.dp, AuthColors.ErrorRed),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AuthColors.ErrorRed.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        tint = AuthColors.ErrorRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Выйти из аккаунта",
                        color = AuthColors.ErrorRed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            color = AuthColors.AccentGreen,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun InfoField(
    icon: ImageVector,
    label: String,
    value: String,
    isEditing: Boolean,
    editedValue: String,
    onEditValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuthColors.CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        AuthColors.AccentGreen.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AuthColors.AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (isEditing) {
                TextField(
                    value = editedValue,
                    onValueChange = onEditValueChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            placeholder.ifEmpty { label },
                            color = AuthColors.TextDim
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = AuthColors.AccentGreen,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AuthColors.AccentGreen,
                        focusedTextColor = AuthColors.TextPrimary,
                        unfocusedTextColor = AuthColors.TextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    singleLine = true
                )
            } else {
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AuthColors.TextDim,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = AuthColors.TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun GenderField(
    isEditing: Boolean,
    gender: String?,
    onGenderChange: (String) -> Unit,
    genders: List<String>,
    showDropdown: Boolean,
    onDropdownToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuthColors.CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        AuthColors.AccentGreen.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Wc,
                    contentDescription = null,
                    tint = AuthColors.AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (isEditing) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AuthColors.FieldBackground)
                        .clickable { onDropdownToggle(true) }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = gender ?: "",
                            color = AuthColors.AccentGreen
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = AuthColors.AccentGreen
                        )
                    }

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { onDropdownToggle(false) },
                        modifier = Modifier.background(AuthColors.FieldBackground)
                    ) {
                        genders.forEach { genderOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        genderOption,
                                        color = if (gender == genderOption)
                                            AuthColors.AccentGreen
                                        else AuthColors.TextSecondary
                                    )
                                },
                                onClick = {
                                    onGenderChange(genderOption)
                                    onDropdownToggle(false)
                                }
                            )
                        }
                    }
                }
            } else {
                Column {
                    Text(
                        text = "Пол",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AuthColors.TextDim,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = gender ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = AuthColors.TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PhysicalField(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    isEditing: Boolean,
    editedValue: String,
    onEditValueChange: (String) -> Unit,
    suffix: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuthColors.CardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                AuthColors.AccentGreen.copy(alpha = 0.2f),
                                AuthColors.AccentGreenDark.copy(alpha = 0.1f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AuthColors.AccentGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            if (isEditing) {
                TextField(
                    value = editedValue,
                    onValueChange = onEditValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuthColors.AccentGreen,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = AuthColors.AccentGreen,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AuthColors.AccentGreen,
                        focusedTextColor = AuthColors.AccentGreen,
                        unfocusedTextColor = AuthColors.AccentGreen
                    )
                )
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AuthColors.TextDim,
                        letterSpacing = 1.sp
                    )
                )
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuthColors.AccentGreen,
                        fontSize = 24.sp
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AuthColors.TextDim,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun FitnessLevelField(
    isEditing: Boolean,
    fitnessLevel: String?,
    onLevelChange: (String) -> Unit,
    levels: List<String>,
    showDropdown: Boolean,
    onDropdownToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuthColors.CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        AuthColors.AccentGreen.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = AuthColors.AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (isEditing) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AuthColors.FieldBackground)
                        .clickable { onDropdownToggle(true) }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fitnessLevel ?: "",
                            color = AuthColors.AccentGreen
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = AuthColors.AccentGreen
                        )
                    }

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { onDropdownToggle(false) },
                        modifier = Modifier.background(AuthColors.FieldBackground)
                    ) {
                        levels.forEach { level ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        level,
                                        color = if (fitnessLevel == level)
                                            AuthColors.AccentGreen
                                        else AuthColors.TextSecondary
                                    )
                                },
                                onClick = {
                                    onLevelChange(level)
                                    onDropdownToggle(false)
                                }
                            )
                        }
                    }
                }
            } else {
                Column {
                    Text(
                        text = "Уровень подготовки",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AuthColors.TextDim,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = fitnessLevel ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = AuthColors.TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuthColors.CardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        AuthColors.AccentGreen.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AuthColors.AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(label, color = AuthColors.TextDim)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = AuthColors.AccentGreen,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AuthColors.AccentGreen,
                    focusedTextColor = AuthColors.TextPrimary,
                    unfocusedTextColor = AuthColors.TextPrimary
                ),
                singleLine = true
            )
        }
    }
}