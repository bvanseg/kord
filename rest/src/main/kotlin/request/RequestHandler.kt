package dev.kord.rest.request

/**
 * Handles Discord API requests.
 */
interface RequestHandler {

    /**
     * The Discord bot authorization token used on requests.
     */
    val token: String

    /**
     * Executes the [request], abiding by the active rate limits and returning the response [R].
     * Throws an [RestRequestException] when a non-rate limit error response is returned.
     */
    @Throws(RestRequestException::class)
    suspend fun <B : Any, R> handle(request: Request<B, R>): R

}
