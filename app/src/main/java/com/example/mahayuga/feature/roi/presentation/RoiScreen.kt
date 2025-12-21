package com.example.mahayuga.feature.roi.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mahayuga.ui.theme.BrandBlue
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

// ⚡ HELPER: Smart Indian Formatting
fun formatIndian(amount: Double): String {
    return try {
        val formatter = NumberFormat.getInstance(Locale("en", "IN"))
        if (amount % 1.0 == 0.0) {
            formatter.maximumFractionDigits = 0
        } else {
            formatter.maximumFractionDigits = 2
        }
        formatter.format(amount)
    } catch (e: Exception) {
        String.format("%.0f", amount)
    }
}

class IndianNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        if (original.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val parts = original.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) "." + parts[1] else ""

        val formattedInteger = formatIndianInteger(integerPart)
        val formatted = formattedInteger + decimalPart

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val separatorIndex = original.indexOf('.')
                val offsetInInteger = if (separatorIndex == -1 || offset <= separatorIndex) offset else separatorIndex
                val commasAdded = countCommasAdded(integerPart.take(offsetInInteger))
                return if (separatorIndex != -1 && offset > separatorIndex) {
                    offsetInInteger + commasAdded + (offset - separatorIndex)
                } else {
                    offset + commasAdded
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val cleanTextUpToOffset = formatted.take(offset).replace(",", "")
                return min(cleanTextUpToOffset.length, original.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    private fun formatIndianInteger(number: String): String {
        if (number.isEmpty()) return ""
        val sb = StringBuilder(number)
        val len = sb.length
        if (len <= 3) return sb.toString()
        var i = len - 3
        while (i > 0) {
            sb.insert(i, ",")
            i -= 2
        }
        return sb.toString()
    }

    private fun countCommasAdded(subString: String): Int {
        if (subString.length <= 3) return 0
        val chunks = (subString.length - 3)
        return 1 + (chunks - 1) / 2
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoiScreen(
    onBackClick: () -> Unit,
    viewModel: RoiViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pdfGenerator = remember { RoiPdfGenerator(context) }
    var showCounterResultPage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            showCounterResultPage -> "Counter Offer Analysis"
                            state.currentStep == 5 -> "ROI Calculation"
                            else -> "ROI Calculator"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showCounterResultPage) {
                            showCounterResultPage = false
                        } else if (state.currentStep > 0) {
                            viewModel.previousStep()
                        } else {
                            onBackClick()
                        }
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
                .imePadding()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            if (showCounterResultPage) {
                CounterOfferResultScreen(
                    state = state,
                    onSharePdf = {
                        scope.launch { pdfGenerator.generateAndSharePdf(state, PdfMode.COUNTER_OFFER) }
                    },
                    onBack = { showCounterResultPage = false }
                )
            } else {
                if (state.currentStep in 1..4) {
                    RoiProgressBar(currentStep = state.currentStep, totalSteps = 4)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                AnimatedContent(
                    targetState = state.currentStep,
                    label = "roi_steps"
                ) { step ->
                    when (step) {
                        0 -> ModeSelectionScreen(viewModel)
                        1 -> Step1Property(state, viewModel)
                        2 -> Step2Lease(state, viewModel)
                        3 -> Step3Expenses(state, viewModel)
                        4 -> Step4Financials(state, viewModel)
                        5 -> Step5Result(
                            state = state,
                            vm = viewModel,
                            onShare = {
                                scope.launch { pdfGenerator.generateAndSharePdf(state, PdfMode.REPORT) }
                            },
                            onShowCounterDetails = {
                                showCounterResultPage = true
                            }
                        )
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
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ModeSelectionScreen(vm: RoiViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Select your role", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandBlue)
        ModeCard("I am a Buyer", "Calculate ROI based on Property Price", Icons.Default.ShoppingCart, { vm.selectMode(true) })
        ModeCard("I am a Seller", "Calculate Selling Price based on Desired ROI", Icons.Default.Sell, { vm.selectMode(false) })
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
            Box(modifier = Modifier.size(48.dp).background(BrandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
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
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Retail", "Office", "Warehouse")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("Property Information")
        RoiInput("Property Name (Optional)", state.propertyName) { vm.updatePropertyInfo(name = it) }
        RoiInput("Address (Optional)", state.propertyAddress) { vm.updatePropertyInfo(address = it) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RoiInput("Building Age (Yrs)", state.buildingAge, Modifier.weight(1f), isNumber = true) { vm.updatePropertyInfo(age = it) }
            RoiInput("Saleable Area (Sq Ft)*", state.saleableArea, Modifier.weight(1f), isNumber = true) { vm.updatePropertyInfo(area = it) }
        }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().menuAnchor(), readOnly = true, value = state.propertyType.ifEmpty { "Select Property Type" },
                onValueChange = {}, label = { Text("Property Type") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(), shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth()) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(text = { Text(selectionOption) }, onClick = { vm.updatePropertyInfo(type = selectionOption); expanded = false })
                }
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
        RoiInput("Property Tax / Month (₹)*", state.propertyTaxMonthly, isNumber = true) { vm.updateExpenses(tax = it) }
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        RoiInput("Maintenance Cost / Month (₹)*", state.maintenanceCost, isNumber = true) { vm.updateExpenses(maint = it) }
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

        // ⚡ NEW: Mandatory Registry Input
        SectionHeader("Registry")
        RoiInput("Registry Cost (%)*", state.registryInput, isNumber = true) { vm.updateFinancials(registry = it) }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step5Result(
    state: RoiState,
    vm: RoiViewModel,
    onShare: () -> Unit,
    onShowCounterDetails: () -> Unit
) {
    var showCounterDialog by remember { mutableStateOf(false) }
    var showCashFlowSheet by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                Text("₹${formatIndian(state.netAnnualIncome)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BrandBlue)
            }
        }

        Text("Financial Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (state.isBuyerMode) {
                    ResultRow("Base Price", state.acquisitionCost.toDoubleOrNull() ?: 0.0)
                } else {
                    ResultRow("SELLING PRICE", state.calculatedSellingPrice, isBold = true)
                }
                ResultRow("Registry Cost", state.registryCost)
                ResultRow("Legal & Others", state.totalOtherCharges)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ResultRow("TOTAL INVESTMENT", state.totalInvestment, isBold = true)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = BrandBlue),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (state.isBuyerMode) "PROJECTED ROI" else "TARGET ROI", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.8f))
                Text(text = String.format("%.2f%%", state.calculatedRoi), style = MaterialTheme.typography.displayLarge, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.isBuyerMode) {
                Button(
                    onClick = { showCounterDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Calculate, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Counter ROI")
                }
            }
            // ⚡ CASHFLOW BUTTON NOW BLUE
            Button(
                onClick = { vm.generateCashFlow(); showCashFlowSheet = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Timeline, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cash Flow")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onShare,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Share, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Full Report PDF", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (showCounterDialog) {
        CounterRoiDialog(
            currentRoi = state.calculatedRoi,
            onDismiss = { showCounterDialog = false },
            onCalculate = { targetRoi -> vm.calculateCounterOffer(targetRoi) },
            resultPrice = state.counterOfferPrice,
            onViewDetails = { showCounterDialog = false; onShowCounterDetails() }
        )
    }

    if (showCashFlowSheet) {
        ModalBottomSheet(onDismissRequest = { showCashFlowSheet = false }) {
            CashFlowContent(state.cashFlows)
        }
    }
}

@Composable
fun CounterOfferResultScreen(state: RoiState, onSharePdf: () -> Unit, onBack: () -> Unit) {
    val counterPrice = state.counterOfferPrice ?: 0.0
    val registry = state.registryCost // Recalculated in VM for counter
    val totalInvest = counterPrice + registry + state.totalOtherCharges

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Based on your Target ROI of ${String.format("%.2f%%", state.counterOfferRoi ?: 0.0)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(colors = CardDefaults.cardColors(containerColor = BrandBlue.copy(alpha = 0.1f)), border = androidx.compose.foundation.BorderStroke(1.dp, BrandBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("PROPOSED OFFER PRICE", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("₹${formatIndian(counterPrice)}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Text("Projected Financials", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                ResultRow("Offer Price (Base)", counterPrice)
                ResultRow("Registry", registry)
                ResultRow("Legal & Others", state.totalOtherCharges)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                ResultRow("TOTAL INVESTMENT", totalInvest, isBold = true)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onSharePdf, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandBlue), shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Default.Share, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Counter Proposal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CounterRoiDialog(currentRoi: Double, onDismiss: () -> Unit, onCalculate: (Double) -> Unit, resultPrice: Double?, onViewDetails: () -> Unit) {
    var targetRoiStr by remember { mutableStateOf("") }
    var hasCalculated by remember { mutableStateOf(false) }

    LaunchedEffect(resultPrice, hasCalculated) {
        if (hasCalculated && resultPrice != null && resultPrice > 0) {
            hasCalculated = false
            onViewDetails()
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(modifier = Modifier.padding(16.dp).fillMaxWidth(0.95f).animateContentSize(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(12.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(56.dp).background(BrandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Calculate, null, tint = BrandBlue, modifier = Modifier.size(32.dp)) }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Counter Offer Calculator", style = MaterialTheme.typography.headlineSmall, color = BrandBlue, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Current ROI", style = MaterialTheme.typography.bodyMedium); Text(String.format("%.2f%%", currentRoi), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold) }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(value = targetRoiStr, onValueChange = { targetRoiStr = it; hasCalculated = false }, label = { Text("Enter Target ROI (%)") }, placeholder = { Text("e.g. 8.5") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandBlue, focusedLabelColor = BrandBlue))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(visible = hasCalculated && (resultPrice == null || resultPrice <= 0)) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error); Spacer(modifier = Modifier.width(12.dp)); Text("ROI unachievable. Please lower target.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer) }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp)) { Text("Cancel") }
                    Button(onClick = { val target = targetRoiStr.toDoubleOrNull() ?: 0.0; if (target > 0) { onCalculate(target); hasCalculated = true } }, modifier = Modifier.weight(1f).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandBlue), shape = RoundedCornerShape(12.dp)) { Text("Calculate") }
                }
            }
        }
    }
}

@Composable
fun CashFlowContent(cashFlows: List<CashFlowRow>) {
    val totalIncome = cashFlows.sumOf { it.netIncome }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(max = 600.dp)) {
        Text("Projected Cash Flow", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandBlue)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)).padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Year", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
            Text("Rent", modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
            Text("Net Income", modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
        }
        LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
            items(cashFlows) { row ->
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(row.year.toString(), modifier = Modifier.weight(0.5f))
                        Text("₹${formatIndian(row.annualRent)}", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text("₹${formatIndian(row.netIncome)}", modifier = Modifier.weight(1f), textAlign = TextAlign.End, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = BrandBlue.copy(alpha = 0.15f)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("TOTAL PROJECTED INCOME", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text("₹${formatIndian(totalIncome)}", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.ExtraBold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RoiInput(label: String, value: String, modifier: Modifier = Modifier, isNumber: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (isNumber) {
                val filtered = input.filter { it.isDigit() || it == '.' }
                if (filtered.count { it == '.' } <= 1) onValueChange(filtered)
            } else {
                onValueChange(input)
            }
        },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next) else KeyboardOptions.Default,
        singleLine = true,
        visualTransformation = if (isNumber) IndianNumberVisualTransformation() else VisualTransformation.None
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = BrandBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
}

@Composable
fun ResultRow(label: String, amount: Double, isNegative: Boolean = false, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = if(isBold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium, fontWeight = if(isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = "${if(isNegative) "- " else ""}₹${formatIndian(kotlin.math.abs(amount))}", style = if(isBold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium, fontWeight = if(isBold) FontWeight.Bold else FontWeight.Normal, color = if (isNegative) Color.Red else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun RoiProgressBar(currentStep: Int, totalSteps: Int) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..totalSteps) {
            val isActive = i <= currentStep
            val color = if (isActive) BrandBlue else MaterialTheme.colorScheme.surfaceVariant
            if (i > 1) HorizontalDivider(modifier = Modifier.weight(1f), thickness = 2.dp, color = color)
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
                if (isActive) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) else Text(i.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}