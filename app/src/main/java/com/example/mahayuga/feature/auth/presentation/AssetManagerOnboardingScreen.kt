package com.example.mahayuga.feature.auth.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mahayuga.core.common.UiState
import com.example.mahayuga.feature.auth.data.model.*
import com.example.mahayuga.ui.theme.* // ⚡ UPDATED IMPORT

private val NavyBg = Color(0xFF0F172A)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGrey = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetManagerOnboardingScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val amState by viewModel.amRegisterState.collectAsState()
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 5

    var showSuccessDialog by remember { mutableStateOf(false) }

    // --- A. Credentials ---
    var workEmail by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // --- B. Primary Contact ---
    var fullName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var commMethod by remember { mutableStateOf("Email") }

    // --- C. Entity Basics ---
    var entityName by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("") }
    var entityType by remember { mutableStateOf("Company") }
    var country by remember { mutableStateOf("India") }
    var website by remember { mutableStateOf("") }
    var yearsOp by remember { mutableStateOf("") }
    var aumRange by remember { mutableStateOf("50Cr - 100Cr") }

    // Address (Registered)
    var regLine1 by remember { mutableStateOf("") }
    var regCity by remember { mutableStateOf("") }
    var regState by remember { mutableStateOf("") }
    var regPin by remember { mutableStateOf("") }
    var isOperatingSame by remember { mutableStateOf(true) }

    // Operating Address (if different)
    var opLine1 by remember { mutableStateOf("") }
    var opCity by remember { mutableStateOf("") }
    var assetClasses by remember { mutableStateOf("") }
    var opCities by remember { mutableStateOf("") }

    // --- D. Business Identifiers ---
    var pan by remember { mutableStateOf("") }
    var gstin by remember { mutableStateOf("") }
    var cin by remember { mutableStateOf("") }
    var sebi by remember { mutableStateOf("") }

    // --- E. Bank Details ---
    var accName by remember { mutableStateOf("") }
    var accNumber by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }

    // --- F. Consent ---
    var authRep by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }
    var digitalKyc by remember { mutableStateOf(false) }
    var noSanctions by remember { mutableStateOf(false) }

    LaunchedEffect(amState) {
        if (amState is UiState.Success) {
            viewModel.resetAmRegisterState()
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = BricxBrandBlue,
                    modifier = Modifier.size(48.dp)
                )
            }, // ⚡ UPDATED
            title = { Text("Application Submitted") },
            text = { Text("Your Partner Application has been sent for review.\n\nYou will be notified once the Admin verifies your entity details. Please check back later.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("welcome") { popUpTo(0) { inclusive = true } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BricxBrandBlue) // ⚡ UPDATED
                ) { Text("Back to Home") }
            },
            containerColor = Color(0xFF1E293B),
            titleContentColor = TextWhite,
            textContentColor = TextGrey
        )
    }

    Scaffold(
        containerColor = NavyBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Partner Registration",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (currentStep > 1) currentStep-- else navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextWhite)
                    }
                },
                actions = {
                    Text(
                        "Step $currentStep/$totalSteps",
                        color = BricxBrandBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 16.dp)
                    ) // ⚡ UPDATED
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { currentStep / totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = BricxBrandBlue, // ⚡ UPDATED
                trackColor = Color.White.copy(0.1f),
            )
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(targetState = currentStep, label = "Wizard") { step ->
                when (step) {
                    1 -> Step1AccountAndContact(
                        workEmail,
                        { workEmail = it },
                        mobile,
                        { mobile = it },
                        password,
                        { password = it },
                        confirmPassword,
                        { confirmPassword = it },
                        fullName,
                        { fullName = it },
                        designation,
                        { designation = it },
                        department,
                        { department = it },
                        whatsapp,
                        { whatsapp = it },
                        commMethod,
                        { commMethod = it })

                    2 -> Step2EntityProfile(
                        entityName,
                        { entityName = it },
                        brandName,
                        { brandName = it },
                        entityType,
                        { entityType = it },
                        country,
                        { country = it },
                        website,
                        { website = it },
                        yearsOp,
                        { yearsOp = it },
                        aumRange,
                        { aumRange = it },
                        regLine1,
                        { regLine1 = it },
                        regCity,
                        { regCity = it },
                        regState,
                        { regState = it },
                        regPin,
                        { regPin = it },
                        isOperatingSame,
                        { isOperatingSame = it },
                        opLine1,
                        { opLine1 = it },
                        opCity,
                        { opCity = it },
                        assetClasses,
                        { assetClasses = it },
                        opCities,
                        { opCities = it })

                    3 -> Step3Identifiers(
                        pan,
                        { pan = it },
                        gstin,
                        { gstin = it },
                        cin,
                        { cin = it },
                        sebi,
                        { sebi = it })

                    4 -> Step4Bank(
                        accName,
                        { accName = it },
                        accNumber,
                        { accNumber = it },
                        ifsc,
                        { ifsc = it },
                        bankName,
                        { bankName = it },
                        branch,
                        { branch = it })

                    5 -> Step5Consent(
                        authRep,
                        { authRep = it },
                        terms,
                        { terms = it },
                        digitalKyc,
                        { digitalKyc = it },
                        noSanctions,
                        { noSanctions = it })
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (amState is UiState.Failure) {
                Text(
                    text = (amState as UiState.Failure).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (currentStep < totalSteps) {
                        currentStep++
                    } else {
                        if (password != confirmPassword) return@Button
                        val amData = AssetManagerModel(
                            email = workEmail,
                            mobile = mobile,
                            contactName = fullName,
                            designation = designation,
                            department = department,
                            whatsappNumber = whatsapp,
                            preferredCommunication = commMethod,
                            entityLegalName = entityName,
                            brandName = brandName,
                            entityType = entityType,
                            country = country,
                            website = website,
                            yearsInOperation = yearsOp,
                            aumRange = aumRange,
                            registeredAddress = AddressModel(
                                regLine1,
                                "",
                                regCity,
                                regState,
                                regPin
                            ),
                            isOperatingSameAsRegistered = isOperatingSame,
                            operatingAddress = if (isOperatingSame) AddressModel() else AddressModel(
                                opLine1,
                                "",
                                opCity,
                                "",
                                ""
                            ),
                            primaryAssetClasses = assetClasses.split(",").map { it.trim() },
                            operatingCities = opCities.split(",").map { it.trim() },
                            pan = pan,
                            gstin = gstin,
                            cinOrLlpin = cin,
                            sebiRegistrationNo = sebi,
                            bankAccount = BankDetailsModel(
                                accName,
                                accNumber,
                                ifsc,
                                bankName,
                                branch
                            ),
                            isAuthorizedRepresentative = authRep,
                            hasAgreedToTerms = terms,
                            hasConsentedToDigitalKyc = digitalKyc,
                            hasNoSanctionsDeclared = noSanctions
                        )
                        viewModel.registerAssetManager(amData, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BricxBrandBlue), // ⚡ UPDATED
                enabled = amState !is UiState.Loading
            ) {
                if (amState is UiState.Loading) {
                    CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (currentStep == totalSteps) "Submit Application" else "Next")
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun Step1AccountAndContact(
    email: String, onEmail: (String) -> Unit, mobile: String, onMobile: (String) -> Unit,
    pass: String, onPass: (String) -> Unit, confirmPass: String, onConfirmPass: (String) -> Unit,
    name: String, onName: (String) -> Unit, desig: String, onDesig: (String) -> Unit,
    dept: String, onDept: (String) -> Unit, whatsapp: String, onWhatsapp: (String) -> Unit,
    comm: String, onComm: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("A. Account Credentials")
        AmTextField(email, onEmail, "Work Email", keyboardType = KeyboardType.Email)
        AmTextField(mobile, onMobile, "Mobile Number (OTP)", keyboardType = KeyboardType.Phone)
        AmTextField(pass, onPass, "Password", isPassword = true)
        AmTextField(confirmPass, onConfirmPass, "Confirm Password", isPassword = true)

        SectionTitle("B. Primary Contact")
        AmTextField(name, onName, "Full Name")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) { AmTextField(desig, onDesig, "Designation") }
            Box(Modifier.weight(1f)) { AmTextField(dept, onDept, "Department (Opt)") }
        }
        AmTextField(
            whatsapp,
            onWhatsapp,
            "WhatsApp Number (Optional)",
            keyboardType = KeyboardType.Phone
        )

        Text("Preferred Communication", color = TextGrey, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Email", "Call", "WhatsApp").forEach { method ->
                FilterChip(
                    selected = comm == method,
                    onClick = { onComm(method) },
                    label = { Text(method) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BricxBrandBlue,
                        selectedLabelColor = TextWhite
                    ) // ⚡ UPDATED
                )
            }
        }
    }
}

@Composable
fun Step2EntityProfile(
    name: String, onName: (String) -> Unit, brand: String, onBrand: (String) -> Unit,
    type: String, onType: (String) -> Unit, country: String, onCountry: (String) -> Unit,
    web: String, onWeb: (String) -> Unit, years: String, onYears: (String) -> Unit,
    aum: String, onAum: (String) -> Unit, rLine1: String, onRLine1: (String) -> Unit,
    rCity: String, onRCity: (String) -> Unit, rState: String, onRState: (String) -> Unit,
    rPin: String, onRPin: (String) -> Unit, isSame: Boolean, onIsSame: (Boolean) -> Unit,
    oLine1: String, onOLine1: (String) -> Unit, oCity: String, onOCity: (String) -> Unit,
    assets: String, onAssets: (String) -> Unit, cities: String, onCities: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("C. Entity Basics")
        AmTextField(name, onName, "Legal Entity Name")
        AmTextField(brand, onBrand, "Brand / Trade Name")
        AmDropdown(
            "Entity Type",
            listOf("Company", "LLP", "Partnership", "Trust", "REIT/InvIT"),
            type,
            onType
        )
        AmTextField(country, onCountry, "Country")

        Text("Registered Address", color = TextWhite, fontWeight = FontWeight.Bold)
        AmTextField(rLine1, onRLine1, "Line 1, Line 2")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) { AmTextField(rCity, onRCity, "City") }
            Box(Modifier.weight(1f)) { AmTextField(rState, onRState, "State") }
        }
        AmTextField(rPin, onRPin, "Pincode", keyboardType = KeyboardType.Number)

        AmCheckbox("Operating address same as registered?", isSame, onIsSame)
        if (!isSame) {
            AmTextField(oLine1, onOLine1, "Operating Address Line 1")
            AmTextField(oCity, onOCity, "Operating City")
        }

        AmTextField(web, onWeb, "Website (Optional)")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) {
                AmTextField(
                    years,
                    onYears,
                    "Years in Op",
                    keyboardType = KeyboardType.Number
                )
            }
            Box(Modifier.weight(1f)) {
                AmDropdown(
                    "AUM Range",
                    listOf("<50Cr", "50-200Cr", "200-500Cr", "500Cr+"),
                    aum,
                    onAum
                )
            }
        }

        AmTextField(assets, onAssets, "Asset Classes (e.g. Commercial, Land)")
        AmTextField(cities, onCities, "Operating Cities (e.g. Mumbai, Delhi)")
    }
}

@Composable
fun Step3Identifiers(
    pan: String, onPan: (String) -> Unit, gst: String, onGst: (String) -> Unit,
    cin: String, onCin: (String) -> Unit, sebi: String, onSebi: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("D. Business Identifiers")
        AmTextField(pan, onPan, "PAN (Entity)")
        AmTextField(gst, onGst, "GSTIN (If applicable)")
        AmTextField(cin, onCin, "CIN / LLPIN")
        AmTextField(sebi, onSebi, "SEBI Registration No. (Optional)")
    }
}

@Composable
fun Step4Bank(
    name: String, onName: (String) -> Unit, acc: String, onAcc: (String) -> Unit,
    ifsc: String, onIfsc: (String) -> Unit, bank: String, onBank: (String) -> Unit,
    branch: String, onBranch: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("E. Bank Details (Optional)")
        Text("Recommended for faster payouts.", color = TextGrey, fontSize = 12.sp)
        AmTextField(name, onName, "Account Name")
        AmTextField(acc, onAcc, "Account Number", keyboardType = KeyboardType.Number)
        AmTextField(ifsc, onIfsc, "IFSC Code")
        AmTextField(bank, onBank, "Bank Name")
        AmTextField(branch, onBranch, "Branch")

        OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Upload, null)
            Spacer(Modifier.width(8.dp))
            Text("Upload Cancelled Cheque")
        }
    }
}

@Composable
fun Step5Consent(
    auth: Boolean, onAuth: (Boolean) -> Unit, terms: Boolean, onTerms: (Boolean) -> Unit,
    kyc: Boolean, onKyc: (Boolean) -> Unit, sanc: Boolean, onSanc: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle("F. Consent & Declarations")
        AmCheckbox("I am authorized to represent this entity.", auth, onAuth)
        AmCheckbox("I agree to Terms & Privacy Policy.", terms, onTerms)
        AmCheckbox("I consent to digital KYC & verification.", kyc, onKyc)
        AmCheckbox("No sanctions / no adverse regulatory action.", sanc, onSanc)
    }
}