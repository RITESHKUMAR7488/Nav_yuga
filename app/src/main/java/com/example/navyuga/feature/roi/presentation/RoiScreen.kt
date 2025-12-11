package com.example.navyuga.feature.roi.presentation

import android.content.Context
import android.view.View
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.navyuga.ui.theme.BrandBlue
import com.example.navyuga.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoiScreen(
    onBackClick: () -> Unit,
    viewModel: RoiViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if(state.currentStep == 5) "ROI Calculation" else "ROI Calculator") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.currentStep > 0) viewModel.previousStep() else onBackClick()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            if (state.currentStep in 1..4) {
                // Progress Indicator (Only in flow)
                RoiProgressBar(currentStep = state.currentStep, totalSteps = 4)
                Spacer(modifier = Modifier.height(24.dp))
            }

            AnimatedContent(
                targetState = state.currentStep,
                label = "roi_steps",
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                }
            ) { step ->
                when (step) {
                    0 -> ModeSelectionScreen(viewModel)
                    1 -> Step1Property(state, viewModel)
                    2 -> Step2Lease(state, viewModel)
                    3 -> Step3Expenses(state, viewModel)
                    4 -> Step4Financials(state, viewModel)
                    5 -> Step5Result(state, onShare = { context ->
                        shareRoiReport(context, state)
                    })
                }
            }

            if (state.currentStep in 1..4) {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.nextStep() },
                    enabled = viewModel.canProceed(state),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text(if (state.currentStep == 4) "Calculate" else "Next Step")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

// --- Steps Composables ---

@Composable
fun ModeSelectionScreen(vm: RoiViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Select your role",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BrandBlue
        )

        ModeCard(
            title = "I am a Buyer",
            subtitle = "Calculate ROI based on Property Price",
            icon = Icons.Default.ShoppingCart,
            onClick = { vm.selectMode(true) }
        )

        ModeCard(
            title = "I am a Seller",
            subtitle = "Calculate Selling Price based on Desired ROI",
            icon = Icons.Default.Sell,
            onClick = { vm.selectMode(false) }
        )
    }
}

@Composable
fun ModeCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(BrandBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = BrandBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Property(state: RoiState, vm: RoiViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Property Information")
        RoiInput("Property Name (Optional)", state.propertyName) { vm.updatePropertyInfo(name = it) }
        RoiInput("Address (Optional)", state.propertyAddress) { vm.updatePropertyInfo(address = it) }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoiInput("Building Age (Yrs)", state.buildingAge, Modifier.weight(1f), isNumber = true) { vm.updatePropertyInfo(age = it) }
            RoiInput("Saleable Area (Sq Ft)*", state.saleableArea, Modifier.weight(1f), isNumber = true) { vm.updatePropertyInfo(area = it) }
        }

        Text("Property Type", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Retail", "Office", "Warehouse").forEach { type ->
                FilterChip(
                    selected = state.propertyType == type,
                    onClick = { vm.updatePropertyInfo(type = type) },
                    label = { Text(type) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoiInput("Floor", state.floor, Modifier.weight(1f)) { vm.updatePropertyInfo(floor = it) }
            RoiInput("Car Park", state.carPark, Modifier.weight(1f)) { vm.updatePropertyInfo(carPark = it) }
        }
    }
}

@Composable
fun Step2Lease(state: RoiState, vm: RoiViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Lease Income Information")
        RoiInput("Tenant Name", state.tenantName) { vm.updateLeaseInfo(tenant = it) }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoiInput("Occupation (Years)*", state.periodOfOccupation, Modifier.weight(1f), isNumber = true) { vm.updateLeaseInfo(occupation = it) }
            RoiInput("Lock-in Period", state.lockInPeriod, Modifier.weight(1f)) { vm.updateLeaseInfo(lockIn = it) }
        }

        SectionHeader("Financials")
        RoiInput("Monthly Rent (₹)*", state.monthlyRent, isNumber = true) { vm.updateLeaseInfo(rent = it) }
        RoiInput("Security Deposit (₹)", state.securityDeposit, isNumber = true) { vm.updateLeaseInfo(deposit = it) }

        SectionHeader("Escalation")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoiInput("Percentage (%)", state.escalationPercent, Modifier.weight(1f), isNumber = true) { vm.updateLeaseInfo(escPercent = it) }
            RoiInput("Frequency (Years)", state.escalationYears, Modifier.weight(1f), isNumber = true) { vm.updateLeaseInfo(escYears = it) }
        }
    }
}

@Composable
fun Step3Expenses(state: RoiState, vm: RoiViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Monthly Expenses")
        RoiInput("Property Tax / Month (₹)", state.propertyTaxMonthly, isNumber = true) { vm.updateExpenses(tax = it) }

        Divider(color = MaterialTheme.colorScheme.surfaceVariant)

        RoiInput("Maintenance Cost / Month (₹)", state.maintenanceCost, isNumber = true) { vm.updateExpenses(maint = it) }

        Text("Paid By", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = !state.isMaintenanceByLandlord, onClick = { vm.updateExpenses(byLandlord = false) })
            Text("Tenant")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = state.isMaintenanceByLandlord, onClick = { vm.updateExpenses(byLandlord = true) })
            Text("Landlord")
        }
    }
}

@Composable
fun Step4Financials(state: RoiState, vm: RoiViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (state.isBuyerMode) {
            SectionHeader("Acquisition Details")
            RoiInput("Total Acquisition Cost (₹)*", state.acquisitionCost, isNumber = true) { vm.updateFinancials(cost = it) }
        } else {
            SectionHeader("Sales Target")
            RoiInput("Desired ROI (%)*", state.targetRoi, isNumber = true) { vm.updateFinancials(targetRoi = it) }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Additional Charges (Optional)", style = MaterialTheme.typography.titleSmall, color = BrandBlue)
                Spacer(modifier = Modifier.height(8.dp))
                RoiInput("Legal Charges", state.legalCharges, isNumber = true) { vm.updateFinancials(legal = it) }
                Spacer(modifier = Modifier.height(8.dp))
                RoiInput("Electricity Charges", state.electricityCharges, isNumber = true) { vm.updateFinancials(elec = it) }
                Spacer(modifier = Modifier.height(8.dp))
                RoiInput("DG Charges", state.dgCharges, isNumber = true) { vm.updateFinancials(dg = it) }
                Spacer(modifier = Modifier.height(8.dp))
                RoiInput("Fire Fighting Charges", state.fireFightingCharges, isNumber = true) { vm.updateFinancials(fire = it) }
            }
        }

        Text(
            text = if(state.isBuyerMode) "Note: 8% Registry Cost will be automatically added to the total investment." else "Note: Registry cost (8%) will be reverse calculated.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun Step5Result(state: RoiState, onShare: (Context) -> Unit) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // --- Income Analysis (Moved Up) ---
        Text("Income Analysis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                ResultRow("Gross Rent / Year", state.grossAnnualRent)
                ResultRow("Property Tax / Year", -state.totalPropertyTaxAnnually, isNegative = true)
                if (state.isMaintenanceByLandlord) {
                    ResultRow("Maintenance / Year", -(state.maintenanceCost.toDoubleOrNull()?:0.0) * 12, isNegative = true)
                }
            }
        }

        // NET INCOME - BOLD and Separate
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandBlue.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, BrandBlue)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("NET ANNUAL INCOME", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
                Text(
                    text = "₹${String.format("%,.0f", state.netAnnualIncome)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
            }
        }

        // --- Investment Breakdown ---
        Text("Financial Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (state.isBuyerMode) {
                    ResultRow("Base Price", state.acquisitionCost.toDoubleOrNull() ?: 0.0)
                } else {
                    ResultRow("SELLING PRICE", state.calculatedSellingPrice, isBold = true)
                }

                ResultRow("Registry (8%)", state.registryCost)
                ResultRow("Legal & Others", state.totalOtherCharges)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ResultRow("TOTAL INVESTMENT", state.totalInvestment, isBold = true)
            }
        }

        // --- Final Result Box (Blue Theme) ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandBlue),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (state.isBuyerMode) "PROJECTED ROI" else "TARGET ROI",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = String.format("%.2f%%", state.calculatedRoi),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onShare(context) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share PDF Report")
        }
    }
}

// --- Common Components ---

@Composable
fun RoiInput(label: String, value: String, modifier: Modifier = Modifier, isNumber: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next) else KeyboardOptions.Default,
        singleLine = true
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = BrandBlue,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun ResultRow(label: String, amount: Double, isNegative: Boolean = false, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = if(isBold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium, fontWeight = if(isBold) FontWeight.Bold else FontWeight.Normal)
        Text(
            text = "${if(isNegative) "- " else ""}₹${String.format("%,.0f", kotlin.math.abs(amount))}",
            style = if(isBold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if(isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isNegative) Color.Red else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RoiProgressBar(currentStep: Int, totalSteps: Int) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..totalSteps) {
            val isActive = i <= currentStep
            val color = if (isActive) BrandBlue else MaterialTheme.colorScheme.surfaceVariant

            if (i > 1) {
                Divider(
                    modifier = Modifier.weight(1f).height(2.dp),
                    color = color
                )
            }

            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(color),
                contentAlignment = Alignment.Center
            ) {
                if (isActive) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(i.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

fun shareRoiReport(context: Context, state: RoiState) {
    val pdfGenerator = RoiPdfGenerator(context)
    pdfGenerator.generateAndSharePdf(state)
}