package black.bracken.neji.ext

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

fun <R : Any> Flow<Either<*, R>>.squeezeRight(): Flow<R> = this.mapNotNull { it.orNull() }

fun <L : Any> Flow<Either<L, *>>.squeezeLeft(): Flow<L> = this.mapNotNull { it.swap().orNull() }