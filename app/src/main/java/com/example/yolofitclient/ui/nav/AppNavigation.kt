package com.example.yolofitclient.ui.nav


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.yolofitclient.ui.screen.exercise.ExerciseListScreen
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yolofitclient.ui.screen.login.LoginScreen
import com.example.yolofitclient.ui.screen.profile.ProfileScreen
import com.example.yolofitclient.ui.screen.register.RegisterScreen
import androidx.compose.material3.Icon
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.yolofitclient.ui.screen.calendar.CalendarScreen
import com.example.yolofitclient.ui.screen.createworkout.CreateWorkoutScreen
import com.example.yolofitclient.ui.screen.exercise.SharedWorkoutViewModel
import com.example.yolofitclient.ui.screen.home.HomeScreen
import com.example.yolofitclient.ui.screen.workout.WorkoutScreen
import com.example.yolofitclient.ui.screen.workoutdetails.WorkoutDetailScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val sharedWorkoutViewModel: SharedWorkoutViewModel = viewModel()

    Box(modifier = Modifier.fillMaxSize()) {

        NavHost(
            navController = navController,
            startDestination = LoginRoute,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<ExerciseListRoute> {
                ExerciseListScreen(
                    onWorkoutCreateClick = { selectedIds ->
                        sharedWorkoutViewModel.setSelectedIds(selectedIds)
                        navController.navigate(CreateWorkoutRoute)
                    }
                )
            }
            composable<RegisterRoute> {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(ExerciseListRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate(LoginRoute){
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<LoginRoute> {
                LoginScreen(
                    navController,
                    onLoginSuccess = {
                        navController.navigate(ExerciseListRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    },
                    toRegister = {
                        navController.navigate(RegisterRoute)
                    }
                )
            }
            composable<ProfileRoute> {
                ProfileScreen(
                    sharedWorkoutViewModel = sharedWorkoutViewModel,
                    onLogoutClick = {
                        navController.navigate(LoginRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<CreateWorkoutRoute> {
                val ids by sharedWorkoutViewModel.selectedIds.collectAsState()

                CreateWorkoutScreen(
                    selectedIds = ids,
                    onBackClick = {
                        sharedWorkoutViewModel.clearSelection()
                        navController.popBackStack()
                    },
                    onWorkoutCreated = {
                        sharedWorkoutViewModel.clearSelection()
                        navController.popBackStack()
                    }
                )
            }
            composable<HomeRoute> {
                HomeScreen(
                    onWorkoutStartClick = { workoutId ->
                        sharedWorkoutViewModel.setWorkoutId(workoutId)
                        navController.navigate(WorkoutRoute)
                    },
                    onWorkoutDetailClick = { workoutId ->
                        sharedWorkoutViewModel.setWorkoutId(workoutId)
                        navController.navigate(WorkoutDetailRoute)
                    }
                )
            }
            composable<WorkoutRoute> {
                val workoutId by sharedWorkoutViewModel.selectedWorkoutId.collectAsState()

                WorkoutScreen(
                    workoutId = workoutId ?: 0,
                    onFinish = {
                        sharedWorkoutViewModel.clearWorkoutId()
                        navController.navigate(HomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    },
                    onBack = {
                        sharedWorkoutViewModel.clearWorkoutId()
                        navController.popBackStack()
                    }
                )
            }
            composable<WorkoutDetailRoute> {
                val workoutId by sharedWorkoutViewModel.selectedWorkoutId.collectAsState()
                WorkoutDetailScreen(
                    workoutId = workoutId ?: 0,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<CalendarRoute>{
                CalendarScreen(
                    onWorkoutClick = { workoutId ->
                        sharedWorkoutViewModel.setWorkoutId(workoutId)
                        navController.navigate(WorkoutDetailRoute)
                    }
                )
            }

        }

        val showBottomBar = currentRoute in listOf(
            ExerciseListRoute::class.qualifiedName,
            ProfileRoute::class.qualifiedName,
            HomeRoute::class.qualifiedName,
            CalendarRoute::class.qualifiedName
        )

        if (showBottomBar) {
            BottomNavBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

    }
}

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem(
            route = ExerciseListRoute::class.qualifiedName ?: "exerciseList",
            icon = Icons.Default.FitnessCenter,
            label = "База"
        ),
        BottomNavItem(
            route = ProfileRoute::class.qualifiedName ?: "profile",
            icon = Icons.Default.Person,
            label = "Профиль"
        ),
        BottomNavItem(
            route = HomeRoute::class.qualifiedName ?: "home",
            icon = Icons.Default.Home,
            label = "Дом"
        ),
        BottomNavItem(
            route = CalendarRoute::class.qualifiedName ?: "calendar",
            icon = Icons.Default.Event,
            label = "Календарь"
        )
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentRoute) {
        val index = items.indexOfFirst { it.route == currentRoute }
        if (index >= 0) selectedIndex = index
    }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenWidthDp = configuration.screenWidthDp.dp
    val navBarPadding = 32.dp
    val navBarWidth = screenWidthDp - navBarPadding
    val itemWidth = navBarWidth / items.size

    val indicatorPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "indicator"
    )

    val indicatorOffsetDp = with(density) {
        val itemWidthPx = itemWidth.toPx()
        val indicatorWidthPx = 80.dp.toPx()
        val offsetPx = (indicatorPosition * itemWidthPx) + (itemWidthPx / 2) - (indicatorWidthPx / 2)
        offsetPx.toDp()
    }

    Box(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF1A1F1A).copy(alpha = 0.95f))
        ) {
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffsetDp)
                    .padding(vertical = 6.dp)
                    .width(80.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF5AF698).copy(alpha = 0.3f),
                                Color(0xFF06D482).copy(alpha = 0.15f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFFB2EA1B)
                        else Color(0xFF728371),
                        animationSpec = tween(300),
                        label = "iconColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentRoute != item.route) {
                                    when (item.route) {
                                        items[0].route -> navController.navigate(ExerciseListRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        items[1].route -> navController.navigate(ProfileRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        items[2].route -> navController.navigate(HomeRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        items[3].route -> navController.navigate(CalendarRoute){
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)


