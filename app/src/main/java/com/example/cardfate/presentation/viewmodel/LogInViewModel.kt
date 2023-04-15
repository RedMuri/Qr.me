package com.example.cardfate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthError
import com.example.auth.AuthProgress
import com.example.auth.AuthState
import com.example.auth.AuthSuccess
import com.example.data.exceptions.UserDoesNotExistsException
import com.example.data.exceptions.WrongPasswordLogInException
import com.example.domain.use_cases.LogInUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogInViewModel @Inject constructor(
    private val logInUseCase: LogInUseCase,
) : ViewModel() {

    private var _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun logIn(phone: String, password: String) {
        val fieldsValid = validateInputLogIn(phone, password)
        if (fieldsValid) {
            _authState.value = AuthProgress
            viewModelScope.launch {
                try {
                    logInUseCase.invoke(phone, password) {
                        _authState.value = AuthSuccess
                    }
                } catch (e: UserDoesNotExistsException) {
                    _authState.value = AuthError(ERROR_USER_DOES_NOT_EXISTS)
                } catch (e: WrongPasswordLogInException) {
                    _authState.value = AuthError(ERROR_WRONG_PASSWORD)
                }
            }
        }
    }

    private fun validateInputLogIn(phone: String, password: String): Boolean {
        if (phone.isBlank()) {
            _authState.value = AuthError(ERROR_EMPTY_PHONE)
            return false
        }
        if (password.isBlank()) {
            _authState.value = AuthError(ERROR_EMPTY_PASSWORD)
            return false
        }
        return true
    }

    companion object {

        const val ERROR_EMPTY_PHONE = 1
        const val ERROR_EMPTY_PASSWORD = 2
        const val ERROR_USER_DOES_NOT_EXISTS = 3
        const val ERROR_WRONG_PASSWORD = 4
    }
}