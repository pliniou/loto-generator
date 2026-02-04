package com.cebolao.app.util

import com.cebolao.domain.error.AppError

fun AppError.toUserMessage(): String =
    when (this) {
        is AppError.Network -> "Não foi possível conectar. Verifique sua internet e tente novamente."
        is AppError.DiskIO -> "Falha ao salvar dados no dispositivo."
        is AppError.DataCorruption -> message ?: "Dados locais corrompidos. Tente reiniciar o app."
        is AppError.Validation -> message ?: "Dados inválidos."
        is AppError.Unknown -> message ?: "Ocorreu um erro inesperado."
    }
