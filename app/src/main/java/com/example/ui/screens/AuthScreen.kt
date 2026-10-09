package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CreamBase
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.PeachLight
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary
import com.example.viewmodel.MindNestViewModel
import com.example.viewmodel.Screen

@Composable
fun AuthScreen(
    viewModel: MindNestViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("BCA Program") }
    var semester by remember { mutableIntStateOf(3) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CreamBase, Color(0xFFF7F4EC), CreamBase)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding Logo
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(3.dp, RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.2.dp, Color(0xFFDED8CD), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mindnest_logo),
                    contentDescription = "MindNest",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MindNest",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark,
                    fontSize = 26.sp,
                    letterSpacing = (-0.5).sp
                )
            )

            Text(
                text = "Premium AI Study Workspace for BCA Students",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF708078),
                    fontSize = 12.5.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFE5E0D6), RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Sign In / Sign Up Switcher
                    TabRow(
                        selectedTabIndex = if (isSignUp) 1 else 0,
                        containerColor = CreamBase,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[if (isSignUp) 1 else 0]),
                                color = SagePrimary,
                                height = 2.5.dp
                            )
                        },
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = !isSignUp,
                            onClick = { isSignUp = false },
                            text = {
                                Text(
                                    text = "Sign In",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Normal,
                                        color = if (!isSignUp) SageDark else Color.Gray
                                    )
                                )
                            }
                        )
                        Tab(
                            selected = isSignUp,
                            onClick = { isSignUp = true },
                            text = {
                                Text(
                                    text = "Sign Up",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSignUp) SageDark else Color.Gray
                                    )
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (isSignUp) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SagePrimary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                unfocusedBorderColor = Color(0xFFDED8CD)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SagePrimary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SagePrimary,
                            unfocusedBorderColor = Color(0xFFDED8CD)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SagePrimary) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SagePrimary,
                            unfocusedBorderColor = Color(0xFFDED8CD)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    if (!isSignUp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showForgotPasswordDialog = true }) {
                                Text(
                                    text = "Forgot Password?",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = LavenderPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Target BCA Semester:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SageDark
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..6).forEach { sem ->
                                val selected = semester == sem
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) SagePrimary else CreamBase)
                                        .clickable { semester = sem }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$sem",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (selected) Color.White else SageDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Primary Auth Action
                    Button(
                        onClick = {
                            val targetName = when {
                                isSignUp && fullName.isNotBlank() -> fullName.trim()
                                email.isNotBlank() -> {
                                    val handle = email.substringBefore("@").replace(".", " ").replace("_", " ")
                                    handle.split(" ").filter { it.isNotBlank() }.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                                }
                                else -> "Rudransh Goyal"
                            }
                            val targetEmail = if (email.isNotBlank()) email else "rudranshgoyal44@gmail.com"
                            viewModel.updateProfile(
                                name = targetName,
                                email = targetEmail,
                                university = university,
                                semester = semester
                            )
                            Toast.makeText(context, if (isSignUp) "Welcome to MindNest, $targetName!" else "Welcome back, $targetName!", Toast.LENGTH_SHORT).show()
                            viewModel.navigateTo(Screen.DASHBOARD)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_button")
                    ) {
                        Text(
                            text = if (isSignUp) "Create MindNest Account" else "Sign In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google Login Button
                    OutlinedButton(
                        onClick = {
                            viewModel.updateProfile(
                                name = "Rudransh Goyal",
                                email = "rudranshgoyal44@gmail.com",
                                university = "BCA Program",
                                semester = 3
                            )
                            Toast.makeText(context, "Signed in with Google as Rudransh Goyal", Toast.LENGTH_SHORT).show()
                            viewModel.navigateTo(Screen.DASHBOARD)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SageDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDED8CD)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("google_login_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Continue as Guest Button
                    TextButton(
                        onClick = {
                            viewModel.continueAsGuest()
                            Toast.makeText(context, "Continuing as Guest Student", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guest_login_button")
                    ) {
                        Text(
                            text = "Continue as Guest Student",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SageDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text(
                    text = "Reset Password",
                    fontWeight = FontWeight.Bold,
                    color = SageDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your registered email address to receive password reset instructions:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF56625C))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        placeholder = { Text("student@example.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Password reset link sent to $forgotEmail", Toast.LENGTH_LONG).show()
                        showForgotPasswordDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                ) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
