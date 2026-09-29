package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamCategory
import com.example.ui.theme.*

@Composable
fun BreakingNewsTicker(
    headline: String = "WBHRB Staff Nurse Grade II 2026 Notification Released! New Mock Tests Active",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BreakingRed,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Alert",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "BREAKING",
                        color = SurfaceWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Text(
                text = headline,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BreakingAlertBanner(
    title: String = "JENPAS-UG 2026 Admit Card Released",
    subtitle: String = "Tap to view all alerts",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .testTag("breaking_alert_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDC2626)) // Vibrant Red from image
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Alert",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = SurfaceWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = subtitle,
                        color = SurfaceWhite.copy(alpha = 0.85f),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Alert",
                tint = SurfaceWhite,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun ExamCategoryCard(
    category: ExamCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Exact colors and icons matching WhatsApp Image 2026-09-27 at 10.52.28 PM.jpeg
    val (icon, iconBg, iconTint) = when (category.code) {
        "JENPAS_UG" -> Triple(Icons.Default.School, Color(0xFFE2E8F0), Color(0xFF1E293B))
        "ANM_GNM" -> Triple(Icons.Default.MedicalServices, Color(0xFFDCFCE7), Color(0xFF16A34A))
        "CULET" -> Triple(Icons.Default.MenuBook, Color(0xFFFFEDD5), Color(0xFFD97706))
        "JECA" -> Triple(Icons.Default.Laptop, Color(0xFFEEF2FF), Color(0xFF4F46E5))
        "JELET" -> Triple(Icons.Default.PrecisionManufacturing, Color(0xFFF3E8FF), Color(0xFF9333EA))
        "JEMSCN" -> Triple(Icons.Default.Healing, Color(0xFFE0F2FE), Color(0xFF0284C7))
        "JEPBN" -> Triple(Icons.Default.LocalHospital, Color(0xFFFFE4E6), Color(0xFFE11D48))
        "AIBE" -> Triple(Icons.Default.Gavel, Color(0xFFFEF3C7), Color(0xFFB45309))
        "PHARMACIST_GR3" -> Triple(Icons.Default.Medication, Color(0xFFECFDF5), Color(0xFF059669))
        "WB_CHO" -> Triple(Icons.Default.HealthAndSafety, Color(0xFFCCFBF1), Color(0xFF0D9488))
        "NORCET_10" -> Triple(Icons.Default.LocalHospital, Color(0xFFDBEAFE), Color(0xFF2563EB))
        "PARAMEDICAL" -> Triple(Icons.Default.Science, Color(0xFFEDE9FE), Color(0xFF7C3AED))
        "RRB_NURSE" -> Triple(Icons.Default.DirectionsTransit, Color(0xFFFEE2E2), Color(0xFFDC2626))
        "WBHRB_SN" -> Triple(Icons.Default.Verified, Color(0xFFD1FAE5), Color(0xFF059669))
        "ESIC_NURSE" -> Triple(Icons.Default.Security, Color(0xFFE0E7FF), Color(0xFF4338CA))
        "AIIMS_NURSE" -> Triple(Icons.Default.Stars, Color(0xFFFEF3C7), Color(0xFFD97706))
        else -> Triple(Icons.Default.School, Color(0xFFEEF2F6), Color(0xFF1E3A8A))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(148.dp)
            .shadow(elevation = 1.5.dp, shape = RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("exam_card_${category.code}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = category.name,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (category.subtitle.isNotBlank()) category.subtitle else category.description,
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BanglaExamTopBar(
    studentName: String,
    unreadNotificationCount: Int = 3,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceWhite,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Greeting
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(RoyalBlue800, IndigoPrimary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Logo",
                        tint = SurfaceWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "WBTOPPER",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalBlue800,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        text = "Hello, $studentName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondaryLight
                    )
                }
            }

            // Actions: Notification bell & Avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceCardLight)
                        .testTag("notification_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = BreakingRed,
                                    contentColor = SurfaceWhite
                                ) {
                                    Text("$unreadNotificationCount", fontSize = 9.sp)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextPrimaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue50)
                        .testTag("header_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = RoyalBlue800,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
