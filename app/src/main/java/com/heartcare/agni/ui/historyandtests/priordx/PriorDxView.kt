package com.heartcare.agni.ui.historyandtests.priordx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.PriorDiagnosis
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.ui.common.ExpandableCard
import com.heartcare.agni.ui.historyandtests.HistoryTakingAndTestsViewModel

@Composable
fun PriorDxView(
    viewModel: HistoryTakingAndTestsViewModel
) {
    if (viewModel.priorDxList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.no_record_found),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.weight(2f))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                viewModel.priorDxList.forEach { priorDx ->
                    ExpandableCard(
                        createdOn = priorDx.createdOn!!,
                        practitionerName = priorDx.practitionerName ?: "",
                        listOfItems = getListOfPriorDx(priorDx),
                        isBulleted = true
                    )
                }
            }
        }
    }
}

private fun getListOfPriorDx(
    priorDx: PriorDxResponse
): List<String> {
    return mutableListOf<String>().apply {
        if (priorDx.hasHypertension) add(PriorDiagnosis.HYPERTENSION.display)
        if (priorDx.hasHeartDiseases) add(PriorDiagnosis.HEART_DISEASE.display)
        if (priorDx.hasTransientIschaemicAttack) add(PriorDiagnosis.TIA.display)
        if (priorDx.hasDiabetes) add(PriorDiagnosis.DIABETES.display)
        if (priorDx.hasHypercholesterolaemia) add(PriorDiagnosis.HYPERCHOLESTEROLAEMIA.display)
        if (priorDx.hasCovid) add(PriorDiagnosis.COVID_19.display)
        if (priorDx.hasCancer) add("${PriorDiagnosis.CANCER.display}: ${priorDx.cancer}")
        if (priorDx.hasAsthma) add(PriorDiagnosis.ASTHMA.display)
        if (priorDx.hasChronicObstructivePulmonaryDisease) add(PriorDiagnosis.COPD.display)
        if (priorDx.hasChronicKidneyDiseases) add(PriorDiagnosis.CHRONIC_KIDNEY_DISEASE.display)
        if (priorDx.hasTuberculosis) add(PriorDiagnosis.TUBERCULOSIS.display)
        if (priorDx.hasAids) add(PriorDiagnosis.AIDS_OR_HIV.display)
        if (priorDx.hasOthers) add("${PriorDiagnosis.OTHERS.display}: ${priorDx.others}")
    }
}