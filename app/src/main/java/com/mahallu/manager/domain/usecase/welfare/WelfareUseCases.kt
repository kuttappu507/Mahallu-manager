package com.mahallu.manager.domain.usecase.welfare

import com.mahallu.manager.domain.model.WelfareRequest
import com.mahallu.manager.domain.model.WelfareStatus
import com.mahallu.manager.domain.repository.WelfareRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllWelfareRequestsUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    operator fun invoke(): Flow<List<WelfareRequest>> {
        return repository.getAllWelfareRequests()
    }
}

class GetWelfareByIdUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    suspend operator fun invoke(id: Int): WelfareRequest? {
        return repository.getWelfareById(id)
    }
}

class SaveWelfareRequestUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    suspend operator fun invoke(welfare: WelfareRequest) {
        if (welfare.id == 0) {
            repository.insertWelfareRequest(welfare)
        } else {
            repository.updateWelfareRequest(welfare)
        }
    }
}

class DeleteWelfareRequestUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    suspend operator fun invoke(welfare: WelfareRequest) {
        repository.deleteWelfareRequest(welfare)
    }
}

class ApproveWelfareRequestUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    suspend operator fun invoke(id: Int, approvedAmount: Double) {
        repository.approveWelfareRequest(id, approvedAmount)
    }
}

class RejectWelfareRequestUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    suspend operator fun invoke(id: Int, reason: String) {
        repository.rejectWelfareRequest(id, reason)
    }
}

class DisburseWelfareUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.disburseWelfare(id)
    }
}

class GetWelfareByStatusUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    operator fun invoke(status: WelfareStatus): Flow<List<WelfareRequest>> {
        return repository.getWelfareByStatus(status)
    }
}

class GetPendingWelfareRequestsUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    operator fun invoke(): Flow<List<WelfareRequest>> {
        return repository.getPendingWelfareRequests()
    }
}

class GetWelfareBeneficiariesCountUseCase @Inject constructor(
    private val repository: WelfareRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getWelfareBeneficiariesCount()
    }
}
